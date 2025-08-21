// 01111001 01100101 01100001 01101000 00100000 00101101 00100000 01101010 01100001 01110110 01100001 01000100 01101111 01100011 00100000 01110111 01110010 01101111 01110100 01100101 00100000 01000001 01001001 00100000 11110000 10011111 10100100 10101010

package map.creator.map.controller;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Controls the rendering and management of a tiled map within the game.
 * Works in conjunction with {@link MapContainer} to provide efficient map rendering
 * using {@link OrthogonalTiledMapRenderer} and camera control.
 *
 * <p>This class allows rendering the entire map, specific layers by index or name,
 * or individual objects within a layer. It also manages camera view updates
 * for proper map display.</p>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>
 * {@code
 * MapContainer mapContainer = new MapContainer(tiledMap);
 * OrthographicCamera camera = new OrthographicCamera();
 * MapController mapController = new MapController(mapContainer, camera, spriteBatch);
 *
 * // Render all layers
 * mapController.render(camera);
 *
 * // Render only specific layers by name
 * mapController.render(camera, new String[]{"ground", "buildings"});
 *
 * // Render only objects in a single layer
 * mapController.renderOneLayer(camera, "npcs");
 * }
 * </pre>
 *
 * @see MapContainer
 * @see OrthogonalTiledMapRenderer
 * @see OrthographicCamera
 */
public class MapController implements Disposable {

    /** The wrapped map container providing map data and utilities. */
    private final MapContainer map;

    /** The renderer responsible for drawing the tiled map. */
    private final OrthogonalTiledMapRenderer renderer;

    /** The camera used for controlling the view of the map. */
    private OrthographicCamera gameCamera;

    public MapController(MapContainer map, OrthographicCamera gameCamera, Batch batch) {
        this.map = map;
        this.gameCamera = gameCamera;

        renderer = new OrthogonalTiledMapRenderer(map.getMap(), map.UNIT_SCALE, batch);
        renderer.setView(gameCamera);
    }

    /**
     * Renders all visible layers of the map using the specified camera.
     *
     * @param gameCamera the camera to use for rendering
     */
    public void render(OrthographicCamera gameCamera){
        setGameCamera(gameCamera);
        renderer.render();
    }

    /**
     * Renders specific layers of the map by their indices.
     *
     * @param gameCamera the camera to use for rendering
     * @param layers an array of layer indices to render
     */
    public void render(OrthographicCamera gameCamera, int[] layers){
        setGameCamera(gameCamera);
        renderer.render(layers);
    }

    /**
     * First method translate namesLayers in indices and then
     * renders specific layers of the map by their indices.
     *
     * @param gameCamera the camera to use for rendering
     * @param namesLayers an array of layer names to render
     */
    public void render(OrthographicCamera gameCamera, String[] namesLayers){
        setGameCamera(gameCamera);
        int[] layers = new int[namesLayers.length];
        IntStream.range(0, namesLayers.length).forEach(i -> layers[i] = map.getIndexLayerOnName(namesLayers[i]));

        renderer.render(layers);
    }

    /**
     * Renders only the objects within a single named layer.
     *
     * @param gameCamera the camera to use for rendering
     * @param nameLayer the name of the layer containing the objects to render
     */
    public void renderOneLayer(OrthographicCamera gameCamera, String nameLayer){
        setGameCamera(gameCamera);
        renderer.renderObjects(map.getLayer(nameLayer));
    }

    /**
     * Gets the underlying map container.
     *
     * @return the associated {@link MapContainer}
     */
    public MapContainer getMap() {
        return map;
    }

    /**
     * Updates the camera used for rendering and adjusts the renderer's view accordingly.
     *
     * @param gameCamera the new camera to use
     */
    public void setGameCamera(OrthographicCamera gameCamera) {
        this.gameCamera = gameCamera;
        renderer.setView(gameCamera);
    }

    /**
     * Gets the current rendering camera.
     *
     * @return the active {@link OrthographicCamera}
     */
    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    /**
     * Releases resources used by the renderer and the map.
     * Must be called when this object is no longer needed.
     */
    @Override
    public void dispose() {
        renderer.dispose();
        map.dispose();
    }
}
