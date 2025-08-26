package map.creator.map.system.contact.impl;

import com.badlogic.ashley.core.Entity;
import map.creator.map.component.data.CleanComponent;
import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.component.data.ContactType;
import map.creator.map.component.data.ContactTypeComponent;
import map.creator.map.system.ObjectEntityFilter;
import map.creator.map.system.contact.ContactIteratingSystem;
import map.creator.map.utils.exception.UnexpectedBehaviorException;

import java.util.Objects;

public abstract class ContactStayIteratingSystem extends ContactIteratingSystem {

    public ContactStayIteratingSystem() {
        super();
    }

    public ContactStayIteratingSystem(ObjectEntityFilter filter) {
        super(filter);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!validateFilter(entity)) return;

        ContactTypeComponent typeComponent = entity.getComponent(ContactTypeComponent.class);
        ContactDataComponent dataComponent = entity.getComponent(ContactDataComponent.class);

        if (Objects.requireNonNull(typeComponent.type) == ContactType.STAY && stayContact(dataComponent, deltaTime)) {
            entity.getComponent(CleanComponent.class).isMustBeDelete = true;
            getEngine().removeEntity(entity);
        }

    }

    @Override
    public boolean beginContact(ContactDataComponent component, float deltaTime) {
        throw new UnexpectedBehaviorException("Mustn't call beginContact in ContactStayIteratingSystem!");
    }

    @Override
    public boolean endContact(ContactDataComponent component, float deltaTime) {
        throw new UnexpectedBehaviorException("Mustn't call endContact in ContactStayIteratingSystem!");
    }
}
