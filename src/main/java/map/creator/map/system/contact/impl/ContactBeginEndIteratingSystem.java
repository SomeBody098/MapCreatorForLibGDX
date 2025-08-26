package map.creator.map.system.contact.impl;

import com.badlogic.ashley.core.Entity;
import map.creator.map.component.data.CleanComponent;
import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.component.data.ContactType;
import map.creator.map.component.data.ContactTypeComponent;
import map.creator.map.system.contact.ContactIteratingSystem;
import map.creator.map.utils.exception.UnexpectedBehaviorException;

/**
 * Abstract class to track contacts.
 * Only supports beginContact and endContact method!
 */
public abstract class ContactBeginEndIteratingSystem extends ContactIteratingSystem {

    /**
     * The method determines the type of contact, as well as performs special actions if the result of the methods: <b>beginContact</b> and <b>endContact</b> was true.
     * @param entity The current Entity being processed.
     * @param deltaTime The delta time between the last and current frame.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!validateFilter(entity)) return;

        ContactTypeComponent typeComponent = entity.getComponent(ContactTypeComponent.class);
        ContactDataComponent dataComponent = entity.getComponent(ContactDataComponent.class);

        switch (typeComponent.type) {
            case BEGIN:
                if (beginContact(dataComponent, deltaTime)) {
                    typeComponent.type = ContactType.STAY;
                }
                break;

            case END:
                if (endContact(dataComponent, deltaTime)) {
                    entity.getComponent(CleanComponent.class).isMustBeDelete = true;
                    getEngine().removeEntity(entity);
                }
                break;
        }
    }


    @Override
    public boolean stayContact(ContactDataComponent component, float deltaTime) {
        throw new UnexpectedBehaviorException("Mustn't call stayContact in ContactBeginEndIteratingSystem!");
    }

}
