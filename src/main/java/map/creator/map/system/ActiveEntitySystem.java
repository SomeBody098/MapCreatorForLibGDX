package map.creator.map.system;

import map.creator.map.component.body.BodyComponent;
import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.body.UserData;
import map.creator.map.system.contact.impl.ContactFullIteratingSystem;

public class ActiveEntitySystem extends ContactFullIteratingSystem {

    @Override
    public boolean beginContact(ContactDataComponent component, float deltaTime) {
        ObjectEntity currentEntity = getCurrentEntityContactedWithActiveZone(component);
        if (currentEntity == null) return false;

        activeZoneHandlerBegin(currentEntity);

        return true;
    }

    @Override
    public boolean stayContact(ContactDataComponent component, float deltaTime) {
        return false;
    }

    @Override
    public boolean endContact(ContactDataComponent component, float deltaTime) {
        ObjectEntity currentEntity = getCurrentEntityContactedWithActiveZone(component);
        if (currentEntity == null) return false;

        activeZoneHandlerEnd(currentEntity);

        return true;
    }

    private ObjectEntity getCurrentEntityContactedWithActiveZone(ContactDataComponent component) {
        ObjectEntity AEntity = component.AEntity;
        ObjectEntity BEntity = component.BEntity;

        UserData AFixtureData = component.AFixtureData;
        UserData BFixtureData = component.BFixtureData;

        if (AFixtureData.type.equals("active-zone")){
            return BEntity;
        } else if (BFixtureData.type.equals("active-zone")){
            return AEntity;
        } else {
            return null;
        }
    }

    private void activeZoneHandlerBegin(ObjectEntity currentEntity){
        if (getEngine().getEntities().contains(currentEntity, true)) return;
        getEngine().addEntity(currentEntity);

        if (currentEntity.getComponent(BodyComponent.class) == null) return;
        currentEntity.getComponent(BodyComponent.class).getBody().setAwake(true);
    }

    private void activeZoneHandlerEnd(ObjectEntity currentEntity){
        if (!getEngine().getEntities().contains(currentEntity, true)) return;
        getEngine().removeEntity(currentEntity);

        if (currentEntity.getComponent(BodyComponent.class) == null) return;
        currentEntity.getComponent(BodyComponent.class).getBody().setAwake(false);
    }

}
