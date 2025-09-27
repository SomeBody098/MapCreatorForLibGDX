package map.creator.map.controller;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.Disposable;

/**
 * This is a container for TileMap, which creates additional parameters and more convenient manipulation of the map.
 * Used in {@link MapController}.
 */
public class MapContainer implements Disposable {

    /**
     * Size tiles - width and height.
     * If width and height was be different then will happen exception.
     */
    public final float PPM;

    /**
     * A parameter that defines the basic unit of measurement for the length of a scene and is used to adjust the size of objects.
     * With its help, you can set exact dimensions in millimeters, centimeters, or meters.
     */
    public final float UNIT_SCALE;

    /**
     * Width map on pixels.
     * For example: MAP_WIDTH_METERS = 10, PPM = 16 then MAP_WIDTH_PIXELS = 160 (MAP_WIDTH_METERS * PPM).
     */
    public final float MAP_WIDTH_PIXELS;

    /**
     * Height map on pixels.
     * For example: MAP_HEIGHT_METERS = 10, PPM = 16 then MAP_HEIGHT_PIXELS = 160 (MAP_HEIGHT_METERS * PPM).
     */
    public final float MAP_HEIGHT_PIXELS;

    /**
     * This is the number of tiles horizontally.
     */
    public final float MAP_WIDTH_METERS;

    /**
     * This is the number of tiles vertical.
     */
    public final float MAP_HEIGHT_METERS;

    /**
     * Represents a tiled map, adds the concept of tiles and tilesets.
     * @see TiledMap
     */
    private final TiledMap map;

    /**
     * @param map Represents a tiled map
     * @throws IllegalArgumentException If tileWidth and tileHeight was be different then will happen exception
     */
    public MapContainer(TiledMap map) {
        this.map = map;

        MAP_WIDTH_METERS = map.getProperties().get("width", Integer.class);
        MAP_HEIGHT_METERS = map.getProperties().get("height", Integer.class);

        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        if (tileWidth != tileHeight) throw new IllegalArgumentException("Width and height tile is different.");
        PPM = tileWidth;
        UNIT_SCALE = 1f / PPM;

        MAP_WIDTH_PIXELS = MAP_WIDTH_METERS * PPM;
        MAP_HEIGHT_PIXELS = MAP_HEIGHT_METERS * PPM;
    }

    /**
     * @param nameLayer The name layer, where contains object.
     * @param nameObject The name search object.
     * @throws IllegalArgumentException if won't be found MapObject.
     * @return The {@link MapObject} which been in layer.
     */
    public MapObject getObjectOnNameInLayer(String nameLayer, String nameObject){
        MapObject object = getMapObjects(nameLayer).get(nameObject);
        if (object == null) throw new IllegalArgumentException("Object on name " + nameObject + " - not exist in layer " + nameLayer + "!");

        return object;
    }

    /**
     * @param nameLayer The name layer.
     * @throws IllegalArgumentException if won't be found MapLayer.
     * @return The {@link MapLayer} on name.
     */
    public MapLayer getLayer(String nameLayer){
        MapLayer layer = map.getLayers().get(nameLayer);
        if (layer == null) throw new IllegalArgumentException("Layer " + nameLayer + " - not exist!");

        return layer;
    }

    /**
     * @param nameLayer The name layer.
     * @return All {@link MapObjects}, which contains in layer.
     */
    public MapObjects getMapObjects(String nameLayer){
        MapObjects objects = getLayer(nameLayer).getObjects();
        if (objects == null) throw new NullPointerException("On layer " + "\"" + nameLayer + "\"" + "no one object!");
        return objects;
    }

    /**
     * Gives the index of the layer by its name. Needed for separate drawing of layers.
     * @param nameLayer The name layer.
     * @return Index this layer in {@link TiledMap}
     */
    public int getIndexLayerOnName(String nameLayer){
        return map.getLayers().getIndex(nameLayer);
    }

    /**
     * Getter the {@link TiledMap}.
     * @return {@link TiledMap}.
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * Getter {@link TiledMapTileSets}.
     * @return {@link TiledMapTileSets}
     */
    public TiledMapTileSets getTileSets(){
        return map.getTileSets();
    }

    /**
     * cleans up resources
     */
    @Override
    public void dispose(){
        map.dispose();
    }
}
