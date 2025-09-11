package map.creator.map;

import map.creator.map.system.contact.ContactSystem;

/**
 * Basic class for all entities and components.
 */
public interface GameObject {

    /**
     * The unique name of the object.
     * @return The name of the object
     */
    String getName();

    /**
     * Type of object - it is determined by it: how the object will be created {@link map.creator.map.factory.object.ObjectCreator} and how it will be processed in the collision {@link ContactSystem}.
     * @return The type of object.
     */
    String getType();

}
