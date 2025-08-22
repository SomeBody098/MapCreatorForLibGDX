package map.creator.map.system;

import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.component.data.ContactType;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.body.UserData;

/**
 * Processes all the {@link ContactDataComponent} that it sends {@link MapContactListener} on any contact.
 * <p><b>Usage Example:</b></p>
 * <pre>
 * {@code @Override
 * public boolean beginContact(ContactDataComponent component, float deltaTime) {
 *     TileEntity AEntity = component.AEntity;
 *     TileEntity BEntity = component.BEntity;
 *
 *      UserData AFixtureData = component.AFixtureData;
 *      UserData BFixtureData = component.BFixtureData;
 *
 *      if (AFixtureData.type.equals("player") || BFixtureData.type.equals("player") &&
 *         AFixtureData.type.equals("enemy") || BFixtureData.type.equals("enemy")){
 *
 *         AEntity.getComponent(HPComponenet.class).hit(-10);
 *         BEntity.getComponent(HPComponenet.class).hit(-10);
 *
 *         return true;
 *      } else {
 *         return false;
 *      }
 *
 * }
 * }
 * </pre>
 * First we get {@link ObjectEntity}'s and {@link UserData}'s fixtures,
 * then we check if the necessary objects are in the service by type in {@link UserData}.
 * If so, perform some action and return true, otherwise false.
 * <p>Keep in mind: </p> <p><b> Returning true affects the lifecycle of a component.</b></p>
 * <p>For example:</p>
 * If the beginContact method returned true, then {@link ContactType} of the current {@link ContactDataComponent} will be change on STATE.
 * Or if endContact method returned true, then current {@link ContactDataComponent} will be deleting from {@link com.badlogic.ashley.core.Engine}.
 * It is could influence on processing of other services. So if there is no logic in some methods better return false - NOT TRUE.
 */
public interface ContactSystem {

    /**
     * Called when two objects first contact.
     * @param component contact data
     * @param deltaTime frame time
     * @return true if contact was processed and component state should be changed, else false
     */
    boolean beginContact(ContactDataComponent component, float deltaTime);

    /**
     * Called when two objects continue contact.
     * @param component contact data
     * @param deltaTime frame time
     * @return true if contact was processed and component state should be changed, else false.
     */
    boolean stayContact(ContactDataComponent component, float deltaTime);

    /**
     * Called when two objects end contact.
     * @param component contact data
     * @param deltaTime frame time
     * @return true if contact was processed and component state should be changed, else false.
     */
    boolean endContact(ContactDataComponent component, float deltaTime);

}
