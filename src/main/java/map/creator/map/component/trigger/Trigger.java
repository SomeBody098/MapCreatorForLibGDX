package map.creator.map.component.trigger;

import map.creator.map.component.ObjectComponent;
import map.creator.map.entity.ObjectEntity;

/// Contains instructions for actions in each method. Methods are called depending on the value of the ContactType in TriggerSystem
/// @see map.creator.map.component.data.ContactType
/// @see map.creator.map.system.TriggerSystem
public abstract class Trigger extends ObjectComponent {

    public Trigger(String name, String owner) {
        super(name, "trigger", owner);
    }

    /// Called when ContactType is BEGIN
    ///
    /// @return if trigger been worked out correct, false if not.
    public abstract boolean beginContact(ObjectEntity AEntity, ObjectEntity BEntity, float deltaTime);

    /// Called when ContactType is STAY
    ///
    /// @return if trigger been worked out correct, false if not.
    public abstract boolean stayContact(ObjectEntity AEntity, ObjectEntity BEntity, float deltaTime);

    /// Called when ContactType is END
    ///
    /// @return if trigger been worked out correct, false if not.
    public abstract boolean endContact(ObjectEntity AEntity, ObjectEntity BEntity, float deltaTime);

}
