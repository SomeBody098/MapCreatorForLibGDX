package map.creator.map.system.contact;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import map.creator.map.component.data.CleanComponent;
import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.component.data.ContactTypeComponent;
import map.creator.map.system.ObjectEntityFilter;

public abstract class ContactIteratingSystem extends IteratingSystem implements ContactSystem{

    private final ObjectEntityFilter filter;

    protected ContactIteratingSystem() {
        super(getDefaultFamily());

        this.filter = null;
    }

    protected ContactIteratingSystem(ObjectEntityFilter filter) {
        super(getDefaultFamily());

        this.filter = filter;
    }

    private static Family getDefaultFamily(){
        return Family.all(
                ContactTypeComponent.class,
                ContactDataComponent.class,
                CleanComponent.class
        ).get();
    }

    protected boolean validateFilter(Entity entity) {
        ContactDataComponent contactDataComponent = ComponentMapper.getFor(ContactDataComponent.class).get(entity);
        if (filter == null) return true;

        return filter.check(contactDataComponent.AEntity, contactDataComponent.BEntity);
    }

}
