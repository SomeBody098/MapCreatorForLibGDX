package map.creator.map.system.contact.impl;

import com.badlogic.ashley.core.Entity;
import map.creator.map.component.data.CleanComponent;
import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.component.data.ContactType;
import map.creator.map.component.data.ContactTypeComponent;
import map.creator.map.system.ObjectEntityFilter;
import map.creator.map.system.contact.ContactIteratingSystem;

/**
 * Abstract class to track contacts.
 */
public abstract class ContactFullIteratingSystem extends ContactIteratingSystem {

    public ContactFullIteratingSystem() {
        super();
    }

    public ContactFullIteratingSystem(ObjectEntityFilter filter) {
        super(filter);
    }

    /**
     * The method determines the type of contact, as well as performs special actions if the result of the methods: <b>beginContact</b>, <b>stayContact</b>, and <b>endContact</b> was true.
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

            case STAY:
                if (stayContact(dataComponent, deltaTime)){
                    // some logic coming soon... >;)
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
}
