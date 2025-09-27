package map.creator.map.component.trigger;

import map.creator.map.component.ObjectComponent;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.system.ObjectEntityFilter;

/// Contains instructions for actions in each method. Methods are called depending on the value of the ContactType in TriggerSystem
/// @see map.creator.map.component.data.ContactType
/// @see map.creator.map.system.TriggerSystem
public abstract class Trigger extends ObjectComponent {

    private final ObjectEntityFilter filter;

    public Trigger(String name, String owner, ObjectEntityFilter filter) {
        super(name, "trigger", owner);
        this.filter = filter;
        this.filter.entityTypes.add("trigger");
    }

    /// Called when ContactType is BEGIN
    ///
    /// @return if trigger been worked out correct, false if not.
    public boolean beginContact(ObjectEntity aEntity, ObjectEntity bEntity, float deltaTime){
        return check(aEntity, bEntity);
    }

    /// Called when ContactType is STAY
    ///
    /// @return if trigger been worked out correct, false if not.
    public boolean stayContact(ObjectEntity aEntity, ObjectEntity bEntity, float deltaTime){
        return check(aEntity, bEntity);
    }

    /// Called when ContactType is END
    ///
    /// @return if trigger been worked out correct, false if not.
    public boolean endContact(ObjectEntity aEntity, ObjectEntity bEntity, float deltaTime){
        return check(aEntity, bEntity);
    }

    /**
     * Automatically checks for object types in a contact.
     * @param aEntity first {@link ObjectEntity} in contact.
     * @param bEntity second {@link ObjectEntity} in contact.
     * @return true if types of entity's contained in filter, other ways - false.
     */
    private boolean check(ObjectEntity aEntity, ObjectEntity bEntity){
        return filter.check(aEntity, bEntity);
    }

}
