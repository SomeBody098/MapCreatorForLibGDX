package map.creator.map.system;

import map.creator.map.entity.ObjectEntity;
import map.creator.map.system.contact.ContactIteratingSystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter to check the components of the entities involved in the contact.
 * Allows you to define which types of entities can interact.
 *
 * <p>Example of use:</p>
 * <pre>
 * {@code
 * ComponentFilter filter = new ComponentFilter("player", "enemy");
 *
 * // in ContactIteratingSystem
 * engine.addSystem(new TriggerSystem(filter));
 * }
 * }
 * </pre>
 *
 * "ComponentFilter" will check all incoming entities and lock if they don't match the types in "entityTypes".
 * @see ContactIteratingSystem
 */
public class ObjectEntityFilter {

    /**
     * All types supported entities.
     */
    public final Set<String> entityTypes;

    public ObjectEntityFilter(String... entityTypes) {
        this.entityTypes = new HashSet<>(Arrays.asList(entityTypes));
    }

    /**
     * Will check "entityTypes" contains types "AEntity" and "BEntity".
     * @param AEntity The first {@link ObjectEntity} in contact.
     * @param BEntity The second {@link ObjectEntity} in contact.
     * @return True if in "entityTypes" contains types "AEntity" and "BEntity". Else - false.
     */
    public boolean check(ObjectEntity AEntity, ObjectEntity BEntity){
        return entityTypes.contains(AEntity.getType()) && entityTypes.contains(BEntity.getType());
    }

}
