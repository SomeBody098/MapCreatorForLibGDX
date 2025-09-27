package map.creator.map.entity;

import com.badlogic.ashley.core.Entity;
import map.creator.map.GameObject;
import map.creator.map.factory.object.creator.ObjectCreator;
import map.creator.map.system.contact.ContactSystem;

/**
 * Base entity for all game objects.
 */
public class ObjectEntity extends Entity implements GameObject {

    /**
     * The unique name of the object
     */
    protected final String name;

    /**
     * Type of object, it is defined as the object will be created {@link ObjectCreator} and processed {@link ContactSystem}
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
