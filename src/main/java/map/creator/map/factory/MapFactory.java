package map.creator.map.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import map.creator.map.controller.MapContainer;
import map.creator.map.factory.object.ObjectCreator;
import map.creator.map.factory.object.ObjectFactory;
import map.creator.map.utils.exception.NotInitializedObjectException;

import java.util.HashMap;

/**
 * Factory for loading and creating game maps from Tiled .tmx files.
 * Supports both synchronous and asynchronous resource loading.
 *
 * <p><b>Key features:</b></p>
 * <ul>
 * <li>Download Tiled maps (.tmx) with support for both synchronous and asynchronous modes</li>
 * <li>Create physical collisions from map layer features</li>
 * <li>Registration of custom object creators for various types of entities</li>
 * <li>Zone loading of objects to optimize performance</li>
 * <li>Integration with Box2D World and Ashley Engine</li>
 * </ul>
 *
 * <p><b>Modes of operation:</b></p>
 * <ol>
 * <li><b>Asynchronous</b> - Uses AssetManager for background loading</li>
 * <li><b>Synchronous</b> - uses a direct TmxMapLoader for immediate loading</li>
 * </ol>
 *
 * <p><b>Example of use:</b></p>
 * <pre>
 * {@code
 * // Creating a factory with asynchronous loading
 * MapFactory factory = new MapFactory(world, engine, true);
 *
 * // Registration of custom creators
 * factory.registerCreator("enemy", new EnemyCreator());
 * factory.registerCreator("item", new ItemCreator());
 *
 * // Loading the map
 * factory.loadMap("maps/level1.tmx");
 *
 * // Waiting for the download to complete
 * while (!factory.isDone()) {
 *     float progress = factory.getProgress();
 * // Show download progress...
 * }
 *
 * // Create a map container
 * MapContainer map = factory.createMap("maps/level1.tmx");
 *
 * // Create collisions for all features in the layer
 * factory.createCollisions(map, "collisions");
 *
 * // Synchronizing the object cache with the engine
 * factory.synchronizeEngineOnCacheObjects();
 * }
 * </pre>
 *
 * @see MapContainer
 * @see ObjectFactory
 * @see AsynchronousFactory
 * @see Disposable
 */
public class MapFactory implements AsynchronousFactory, Disposable {

    private final HashMap<String, TiledMap> tiledMaps;
    private final TmxMapLoader loader;

    private final AssetManager manager;
    private Thread loadingThread;
    private volatile boolean isDone = true;
    private volatile boolean isFail = false;

    private final ObjectFactory objectsFactory;
    private final boolean isAsynchronousLoading;

    public MapFactory(World world, Engine engine, boolean isAsynchronousLoading) {
        this.isAsynchronousLoading = isAsynchronousLoading;

        if (isAsynchronousLoading) {
            manager = new AssetManager();
            manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
            loader = null;
            tiledMaps = null;
        } else {
            loader = new TmxMapLoader();
            tiledMaps = new HashMap<>();
            manager = null;
        }

        objectsFactory = new ObjectFactory(world, engine);
    }

    /**
     * Checks for the completion of asynchronous boot operations.
     *
     * @return true if all operations are completed successfully
     * @throws RuntimeException if a boot error occurred (isFail=true)
     * @throws IllegalStateException if raised in synchronous mode (in validateAsynchronous() method).
     */
    @Override
    public boolean isDone() {
        validateAsynchronous();
        if (isFail) throw new RuntimeException("\"isFail\" - true. Some going wrong...");

        if (manager.update() && isDone) {
            loadingThread = null;
            manager.finishLoading();
            return true;
        }

        return false;
    }

    /**
     * Returns the progress of loading resources.
     *
     * @return progress value from 0.0 to 1.0
     * @throws IllegalStateException if raised in synchronous mode (in validateAsynchronous() method).
     */
    @Override
    public float getProgress(){
        validateAsynchronous();
        return manager.getProgress();
    }

    /**
     * Registers the creator of objects for a specific type.
     *
     * @param type object type (must match the type in Tiled)
     * @param creator of objects for this type
     * @see ObjectCreator
     */
    public void registerCreator(String type, ObjectCreator creator){
        objectsFactory.registerCreator(type, creator);
    }

    /**
     * Unregisters the creator of objects for a specific type.
     *
     * @param type object type (must match the type in Tiled)
     * @see ObjectCreator
     */
    public void unregisterCreator(String type){
        objectsFactory.unregisterCreator(type);
    }

    /**
     * Creates a map container from a downloaded .tmx file.
     *
     * @param path path to the .tmx file
     * @return container with map and auxiliary parameters
     * @throws NullPointerException if path is null
     * @throws NotInitializedObjectException if the map is not yet loaded (in asynchronous mode)
     */
    public MapContainer getMap(String path){
        if (path == null) throw new NullPointerException("Path - is null!");

        MapContainer container;
        if (isAsynchronousLoading){
            try {
                container = new MapContainer(manager.get(path, TiledMap.class));
            } catch (GdxRuntimeException e){
                throw new NotInitializedObjectException("AssetManager didn't have time to download your .tmx file - " + path +
                    ".\nPlease, use method \"isDone\" for know when it finishing download. However, if you don't want use asynchronous loading, then just put isAsynchronousLoading in false in constructor MapFactory."
                );
            }
        } else {
            container = new MapContainer(tiledMaps.get(path));
        }

        return container;
    }

    /**
     * Creates collisions for all features in the specified layers.
     *
     * @param map container map
     * @param namesLayers the names of the layers to be processed
     * @throws IllegalArgumentException if namesLayers is empty or null
     */
    public void createCollisions(MapContainer map, String... namesLayers) {
        rebootWorld();
        createCollisions(map, null, namesLayers);
    }

    /**
     * Creates collisions for features in specified layers in a specific zone.
     *
     * @param map container map
     * @param zoneLoad rectangular area for loading objects
     * @param namesLayers the names of the layers to be processed
     * @throws IllegalArgumentException if namesLayers is empty or null
     */
    public void createCollisions(MapContainer map, Rectangle zoneLoad, String... namesLayers) {
        rebootWorld();

        if (namesLayers == null || namesLayers.length == 0) {
            throw new IllegalArgumentException("\"namesLayers\" mustn't be empty! Please - write name layer, where contains some objects!");
        }

        if (!isAsynchronousLoading) {
            syncCollisions(map, zoneLoad);
            return;
        }

        isDone = false;
        loadingThread = new Thread(() -> {
            try {
                syncCollisions(map, zoneLoad, namesLayers);
                isDone = true;
                synchronizeEngineOnCacheObjects();
            } catch (Exception e){
                loadingThread.interrupt();
                isFail = true;
                Gdx.app.log("MapFactory", "Been exception in " + Thread.currentThread().getName(), e);
            } finally {
                isDone = true;
            }
        });
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private synchronized void syncCollisions(MapContainer map, Rectangle zoneLoad, String... namesLayers) {
        rebootWorld();

        for (String nameLayer : namesLayers) {
            objectsFactory.createObjectsOnLayer(map, nameLayer, zoneLoad);
        }
    }

    /**
     * Loads the map into memory.
     * In asynchronous mode, it starts background loading.
     * In synchronous mode, it loads immediately.
     *
     * @param path path to the .tmx file
     */
    public void loadMap(String path){
        if (isAsynchronousLoading) {
            if (isLoadMap(path)) return;
            manager.load(path, TiledMap.class);

        } else {
            if (tiledMaps.containsKey(path)) return;
            tiledMaps.put(path, loader.load(path));
        }
    }

    /**
     * Loading map and creating collision.
     * @param path path to the .tmx file
     * @param namesLayers the names of the layers to be processed
     */
    public void createMap(String path, String... namesLayers){
        createMap(path, null, namesLayers);
    }

    /**
     * Loading map and creating collision if they located in zoneLoad.
     * @param path path to the .tmx file
     * @param zoneLoad zone load - if some body will be containing in it then this body will be created.
     * @param namesLayers the names of the layers to be processed
     */
    public void createMap(String path, Rectangle zoneLoad, String... namesLayers){
        if (isAsynchronousLoading) {
            isDone = false;
            manager.load(path, TiledMap.class);

            loadingThread = new Thread(() -> {
                try {
                    while (!manager.isLoaded(path)) {
                        Thread.yield();
                    }

                    syncCollisions(
                            new MapContainer(manager.get(path, TiledMap.class)),
                            zoneLoad,
                            namesLayers
                    );

                    isDone = true;
                } catch (Exception e){
                    loadingThread.interrupt();
                    isFail = true;
                    Gdx.app.log("MapFactory", "Been exception in " + Thread.currentThread().getName(), e);
                } finally {
                    isDone = true;
                }

                isDone = true;
                synchronizeEngineOnCacheObjects();
            });
            loadingThread.setDaemon(true);
            loadingThread.start();
        } else {
            TiledMap map = loader.load(path);
            tiledMaps.put(path, map);
            syncCollisions(new MapContainer(map), zoneLoad, namesLayers);
        }
    }

    /**
     * Checks if the specified card is loaded.
     *
     * @param path path to the .tmx file
     * @return true if the card is already loaded
     */
    public boolean isLoadMap(String path) {
        return manager.isLoaded(path);
    }

    /**
     * Returns the {@link ObjectFactory}.
     *
     * @return Factory Objects
     */
    public ObjectFactory getObjectsFactory() {
        return objectsFactory;
    }

    /**
     * Synchronizes objects with the Ashley engine.
     * Adds all created entities to the engine for processing by systems.
     */
    public void synchronizeEngineOnCacheObjects(){
        objectsFactory.synchronizeEngineOnCacheObjects();
    }

    private void validateAsynchronous(){
        if (!isAsynchronousLoading) throw new IllegalArgumentException("\"isAsynchronousLoading\" - false. The MapFactory is not worked in asynchronous mode.");
    }

    /**
     * Removes ALL bodies from the world - called automatically if you to create new collisions for {@link TiledMap}.
     */
    public void rebootWorld(){
        objectsFactory.getBodyFactory().reboot();
    }

    /**
     * Frees up resources and stops background threads.
     * <p><b>REQUIRED TO BE CALLED WHEN THE APPLICATION IS TERMINATED!</b></p>
     */
    @Override
    public void dispose(){
        if (isAsynchronousLoading) {
            if (loadingThread != null) {
                loadingThread.interrupt();
                loadingThread = null;
            }

            manager.dispose();
        } else {
            tiledMaps.clear();
        }
    }
}
