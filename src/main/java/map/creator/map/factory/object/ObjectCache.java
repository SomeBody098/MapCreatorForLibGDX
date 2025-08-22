package map.creator.map.factory.object;

import com.badlogic.gdx.maps.MapProperties;
import map.creator.map.entity.ObjectEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Universal cache for storing game objects and their parameters
 */
public class ObjectCache {

    /**
     * Cache of game entities by their names.
     */
    private final Map<String, ObjectEntity> entityMap;

    /**
     * Cache of objects-properties (Such objects are usually impossible to create).
     */
    private final Map<String, MapProperties> dataObjects;

    public ObjectCache() {
        entityMap = new HashMap<>();
        dataObjects = new HashMap<>();
    }

    public ObjectCache(Map<String, ObjectEntity> entityMap, Map<String, MapProperties> dataObjects) {
        this.entityMap = entityMap;
        this.dataObjects = dataObjects;
    }

    public void clear(){
        entityMap.clear();
        dataObjects.clear();
    }

    public Map<String, ObjectEntity> getEntityMap() {
        return entityMap;
    }

    public Map<String, MapProperties> getDataObjects() {
        return dataObjects;
    }
}
