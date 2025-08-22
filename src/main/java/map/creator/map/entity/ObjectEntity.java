package map.creator.map.entity;

import com.badlogic.ashley.core.Entity;
import map.creator.map.GameObject;

/**
 * Base entity for all tile objects.
 */
public class ObjectEntity extends Entity implements GameObject {

    /**
     * The unique name of the object
     */
    protected final String name;

    /**
     * Type of object, it is defined as the object will be created {@link map.creator.map.factory.object.ObjectCreator} and processed {@link map.creator.map.system.ContactSystem}
     */
    protected final String type;

    public ObjectEntity(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

}
