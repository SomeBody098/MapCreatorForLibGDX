package map.creator.map.system;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import map.creator.map.component.ObjectComponent;
import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.component.trigger.Trigger;
import map.creator.map.factory.body.UserData;
import map.creator.map.utils.exception.UnexpectedBehaviorException;

/**
 * Base system for working any triggers.
 */
public class TriggerSystem extends ContactIteratingSystem {

    /**
     * First contact with trigger.
     * @param component contact data.
     * @param deltaTime frame time.
     * @return true if trigger be found and will work out correct.
     */
    @Override
    public boolean beginContact(ContactDataComponent component, float deltaTime) {
        Trigger trigger = getTrigger(component.AEntity, component.BEntity, component.AFixtureData, component.BFixtureData);
        if (trigger == null) return false;
        return trigger.beginContact(component.AEntity, component.BEntity, deltaTime);
    }

    /**
     * The object continue contact with trigger.
     * @param component contact data
     * @param deltaTime frame time
     * @return true if trigger be found and will work out correct.
     */
    @Override
    public boolean stayContact(ContactDataComponent component, float deltaTime) {
        Trigger trigger = getTrigger(component.AEntity, component.BEntity, component.AFixtureData, component.BFixtureData);
        if (trigger == null) return false;
        return trigger.stayContact(component.AEntity, component.BEntity, deltaTime);
    }

    /**
     * The object going from trigger.
     * @param component contact data
     * @param deltaTime frame time
     * @return true if trigger be found and will work out correct.
     */
    @Override
    public boolean endContact(ContactDataComponent component, float deltaTime) {
        Trigger trigger = getTrigger(component.AEntity, component.BEntity, component.AFixtureData, component.BFixtureData);
        if (trigger == null) return false;
        return trigger.endContact(component.AEntity, component.BEntity, deltaTime);
    }

    /**
     * Getting current trigger.
     * (P.S: if will be discovered 2 triggers in contact, then will be return null)
     * @param entityA First entity which contains contacting fixture.
     * @param entityB Second entity which contains contacting fixture.
     * @param userDataA the user data of the first contacting fixture.
     * @param userDataB the user data of the second contacting fixture.
     * @return trigger if it will be found, else throw exception.
     * @throws UnexpectedBehaviorException if trigger for some reason not be found.
     */
    private Trigger getTrigger(Entity entityA, Entity entityB, UserData userDataA, UserData userDataB){
        String triggerName = "trigger";
        if (userDataA.type.equals(triggerName) && userDataB.type.equals(triggerName)) return null;

        Entity currentEntity;
        UserData currentUserData;
        if (userDataA.type.equals(triggerName)){
            currentEntity = entityA;
            currentUserData = userDataA;

        } else if (userDataB.type.equals(triggerName)){
            currentEntity = entityB;
            currentUserData = userDataB;

        } else {
            return null;
        }

        for (Component component : currentEntity.getComponents()) {
            ObjectComponent tileComponent = (ObjectComponent) component;

            if (tileComponent.getName().equals(currentUserData.name)) return (Trigger) tileComponent;
        }

        throw new UnexpectedBehaviorException("Trigger not be found.");
    }

}
