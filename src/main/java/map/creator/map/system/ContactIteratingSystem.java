package map.creator.map.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import map.creator.map.component.data.CleanComponent;
import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.component.data.ContactType;
import map.creator.map.component.data.ContactTypeComponent;

/**
 * Abstract class to track contacts.
 */
public abstract class ContactIteratingSystem extends IteratingSystem implements ContactSystem{

    public ContactIteratingSystem() {
        super(
            Family.all(
                ContactTypeComponent.class,
                ContactDataComponent.class,
                CleanComponent.class
                ).get()
        );
    }

    /**
     * The method determines the type of contact, as well as performs special actions if the result of the methods: <b>beginContact</b>, <b>stayContact</b>, and <b>endContact</b> was true.
     * @param entity The current Entity being processed.
     * @param deltaTime The delta time between the last and current frame.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
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
