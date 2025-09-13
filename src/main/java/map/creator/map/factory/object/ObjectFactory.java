package map.creator.map.factory.object;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import map.creator.map.component.body.BodyComponent;
import map.creator.map.controller.MapContainer;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.body.BodyFactory;
import map.creator.map.factory.body.BodyParam;
import map.creator.map.factory.body.FormBody;
import map.creator.map.factory.body.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Central factory for creating game objects from the Tiled map.
 * Coordinates the process of converting Tiled objects into game entities with physical bodies.
 * Works in tandem with {@link map.creator.map.factory.MapFactory} so its creation is not required, but is possible for specific cases.
 *
 * <p><b>Main Features:</b></p>
 * <ul>
 * <li>Parsing objects from Tiled .tmx files</li>
 * <li>Creating physical bodies through BodyFactory</li>
 * <li>Manage custom object creators</li>
 * <li>Caching of created entities and data objects</li>
 * <li>Support zone loading for optimization</li>
 * <li>Ashley ECS Integration</li>
 * </ul>
 * @see BodyFactory
 * @see ObjectCreator
 * @see ObjectCache
 * @see Disposable
 */
public class ObjectFactory implements Disposable {

    private final Engine engine;

    private final Map<String, ObjectCreator> objectCreators;

    private final BodyFactory bodyFactory;

    private final ObjectCache cache;

    public ObjectFactory(World world, Engine engine) {
        this.engine = engine;
        bodyFactory = new BodyFactory(world, 0);
        objectCreators = new HashMap<>();
        cache = new ObjectCache();
    }

    public ObjectFactory(World world, Engine engine, boolean isDebug) {
        this.engine = engine;
        bodyFactory = new BodyFactory(world, 0);
        objectCreators = new HashMap<>();
        bodyFactory.setDebug(isDebug);
        cache = new ObjectCache();
    }

    public ObjectFactory(World world, Engine engine, boolean isDebug, ObjectCache cache) {
        this.engine = engine;
        this.bodyFactory = new BodyFactory(world, 0);
        objectCreators = new HashMap<>();
        bodyFactory.setDebug(isDebug);
        this.cache = cache;
    }

    /**
     * Sets debug (Logs of object creation will appear).
     * @param debug if true - BodyFactory will debug
     */
    public void setDebug(boolean debug) {
        bodyFactory.setDebug(debug);
    }

    public ObjectCache getCache() {
        return cache;
    }

    public BodyFactory getBodyFactory() {
        return bodyFactory;
    }

    /**
     * Registers the creator of objects for a specific type.
     *
     * @param type object type (must match the "type" property in Tiled)
     * @param creator creator objects.
     */
    public void registerCreator(String type, ObjectCreator creator){
        objectCreators.put(type, creator);
    }

    /**
     * Deletes the creator of objects by type.
     *
     * @param type of object to be deleted
     */
    public void unregisterCreator(String type){
        objectCreators.remove(type);
    }

    /**
     * Synchronizes all cached entities with the Ashley engine.
     */
    public void synchronizeEngineOnCacheObjects(){
        cache.getEntityMap().values().forEach(engine::addEntity);
    }

    /**
     * Clears all registered creators.
     */
    public void clearCreators(){
        objectCreators.clear();
    }

    /**
     * Clears the object cache.
     * @see ObjectCache
     */
    public void clearCache(){
        cache.clear();
    }

    /**
     * Creates features in the specified zone on the map layer.
     *
     * @param map container map
     * @param nameLayer the name of the layer to be processed
     * @param zoneLoad zone for loading objects
     */
    public synchronized void createObjectsOnLayer(MapContainer map, String nameLayer, Shape2D zoneLoad) {
        MapObjects objects = map.getMapObjects(nameLayer);
        bodyFactory.setUnitScale(map.UNIT_SCALE);
        createObjects(objects, zoneLoad);
    }

    /**
     * Creates a game objects from body parameters.
     *
     * @param objects objects array
     * @param zoneLoad There will be a check for the content of objects in this area, if they are contained, then they will be created, if not contained then not (if it not null).
     */
    private synchronized void createObjects(MapObjects objects, Shape2D zoneLoad){
        ArrayList<BodyParam> staticObjects = new ArrayList<>();

        loadAllDataObject(objects);
        for (Iterator<MapObject> iterator = objects.iterator(); iterator.hasNext(); ) {
            MapObject object = iterator.next();
            MapProperties properties = object.getProperties();

            if (properties.get("data", Boolean.class) != null && properties.get("data", Boolean.class)) continue;
            
            String nameObject = object.getName();
            String classObject = properties.get("type", String.class);
            String customForm = properties.get("form", String.class);

            if (classObject == null) {
                Gdx.app.error("ObjectsFactory", "Imposable create TileObject because it type is null!", new NullPointerException());
                return;
            }

            FormBody defaultForm;
            Shape2D boundsObject;
            if (object instanceof RectangleMapObject) {
                boundsObject = ((RectangleMapObject) object).getRectangle();
                defaultForm = FormBody.RECTANGLE;

            } else if (object instanceof CircleMapObject) {
                boundsObject = ((CircleMapObject) object).getCircle();
                defaultForm = FormBody.CIRCLE;

            } else if (object instanceof EllipseMapObject) {
                boundsObject = ((EllipseMapObject) object).getEllipse();
                defaultForm = FormBody.ELLIPSE;

            } else if (object instanceof PolylineMapObject) {
                boundsObject = ((PolylineMapObject) object).getPolyline();
                defaultForm = FormBody.CHAIN;

            } else if (object instanceof PolygonMapObject) {
                boundsObject = ((PolygonMapObject) object).getPolygon();
                defaultForm = FormBody.POLYGON;

            } else {
                Gdx.app.error("ObjectsFactory", "Shape object not found. The object - " + nameObject, new IllegalArgumentException());
                continue;
            }

            FormBody formBody = customForm == null ? defaultForm : FormBody.getFormBodyOnString(customForm);
            if (zoneLoad != null && isObjectContainsInZone(zoneLoad, formBody, boundsObject)) continue;

            if (classObject.equals("static")) {
                staticObjects.add(createBodyParamForStaticObject(nameObject, classObject, formBody, boundsObject, properties));
                continue;
            }

            if (!objectCreators.containsKey(classObject)) {
                Gdx.app.error("ObjectsFactory", "Unknown type object - " + classObject);
                continue;
            }

            ObjectCreator creator = objectCreators.get(classObject);
            nameObject = getAnotherNameIfThatExists(nameObject, cache.getEntityMap());
            cache.getEntityMap().put(
                    nameObject,
                creator.createObject(
                    nameObject,
                    properties,
                    cache.getDataObjects(),
                    bodyFactory,
                    formBody,
                    boundsObject
                )
            );
        }

        createStaticObjects(staticObjects);
    }

    /**
     * Auto creating body param for static object.
     * @param nameObject name object.
     * @param classObject class object.
     * @param formBody form body.
     * @param boundsObject bounds object.
     * @param properties properties object.
     * @return BodyParam of static object.
     */
    private synchronized BodyParam createBodyParamForStaticObject(String nameObject, String classObject, FormBody formBody, Shape2D boundsObject, MapProperties properties){
        BodyDef bodyDef = new BodyDef();
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;

        BodyParam param = new BodyParam(
            formBody,
            bodyDef,
            fixtureDef,
            boundsObject,
            new UserData(nameObject == null ? "static" : nameObject, classObject, "static")
        );

        if (param.formBody == FormBody.CHAIN) {
            Boolean isLooping = properties.get("looping", Boolean.class);
            if (isLooping == null) isLooping = false;

            param.isLooping = isLooping;
        }

        return param;
    }

    /**
     * Load all data objects in {@link ObjectCache} (P.S: These are objects that will not be created on the map - they are only needed as a designation, for example: the spawn points of goblins from the barracks).
     * @param objects objects array
     */
    private synchronized void loadAllDataObject(MapObjects objects){
        for (MapObject object : objects) {
            MapProperties properties = object.getProperties();

            Boolean isData = properties.get("data", Boolean.class);
            if (isData != null && isData) {
                String name = object.getName();
                if (name == null)
                    throw new IllegalArgumentException("If object is data, then this one must have a name!");

                cache.getDataObjects().put(
                    getAnotherNameIfThatExists(name, cache.getDataObjects()),
                    properties
                );
            }
        }
    }

    /**
     * Creates a game object from body parameters.
     *
     * @param param body parameters
     * @return created game entity
     */
    public ObjectEntity createObject(BodyParam param) {
        Body body = bodyFactory.createCollision(param);

        ObjectEntity tileEntity = new ObjectEntity(param.userData.name, param.userData.type);
        tileEntity.add(new BodyComponent(body, tileEntity.getName()));

        cache.getEntityMap().put(param.userData.name, tileEntity);

        return tileEntity;
    }

    private synchronized void createStaticObjects(ArrayList<BodyParam> bodyParams){
        Body body = bodyFactory.createCollisionsUnderOneBody(bodyParams);

        String name = getAnotherNameIfThatExists("static", cache.getEntityMap());
        ObjectEntity tileEntity = new ObjectEntity(name, "static");
        tileEntity.add(new BodyComponent(body, tileEntity.getName()));

        cache.getEntityMap().put(name, tileEntity);
    }

    private synchronized boolean isObjectContainsInZone(Shape2D zoneLoad, FormBody formBody, Shape2D boundsObject){
        boolean isContains = false;

        if (Objects.requireNonNull(formBody) == FormBody.RECTANGLE) {
            Rectangle bounds = ((Rectangle) boundsObject);
            isContains = zoneLoad.contains(bounds.x, bounds.y);

        } else if (formBody == FormBody.CIRCLE) {
            Circle bounds = ((Circle) boundsObject);
            isContains = zoneLoad.contains(bounds.x, bounds.y);

        } else if (formBody == FormBody.ELLIPSE) {
            Ellipse bounds = ((Ellipse) boundsObject);
            isContains = zoneLoad.contains(bounds.x, bounds.y);

        } else if (formBody == FormBody.CHAIN || formBody == FormBody.EDGE) {
            Polyline bounds = ((Polyline) boundsObject);
            isContains = zoneLoad.contains(bounds.getX(), bounds.getY());

        } else if (formBody == FormBody.POLYGON) {
            Polygon bounds = ((Polygon) boundsObject);
            isContains = zoneLoad.contains(bounds.getX(), bounds.getY());
        }

        return isContains;
    }

    /**
     * Giving another name if that exists in {@link Map}.
     * @param currentName name object.
     * @param map current map.
     * @return if name exists, then will be added numbers until it is unique.
     */
    private synchronized String getAnotherNameIfThatExists(String currentName, Map<?, ?> map) {
        int count = 0;
        String anotherName = currentName;

        while (map.containsKey(anotherName)) {
            anotherName = currentName;
            anotherName += count;
            count++;
        }

        return anotherName;
    }

    @Override
    public void dispose() {
        clearCache();
        clearCreators();
        clearCache();
    }
}
