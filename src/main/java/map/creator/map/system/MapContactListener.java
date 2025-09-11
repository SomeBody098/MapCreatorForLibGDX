package map.creator.map.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Pool;
import map.creator.map.component.data.CleanComponent;
import map.creator.map.component.data.ContactDataComponent;
import map.creator.map.component.data.ContactType;
import map.creator.map.component.data.ContactTypeComponent;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.object.ObjectCache;
import map.creator.map.factory.body.UserData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Standard implementation for handler contacts.
 * @see ContactListener
 */
public class MapContactListener implements ContactListener {

    protected final Engine engine;
    protected final Pool<Entity> entityPool;
    protected final Map<String, Entity> contactDataComponents;

    protected final Map<String, Integer> activeContacts = new HashMap<>();

    protected final ObjectCache objectsCache;

    protected boolean isDebug;

    public MapContactListener(Engine engine, ObjectCache objectsCache) {
        this.engine = engine;
        this.objectsCache = objectsCache;
        contactDataComponents = new HashMap<>();

        entityPool = new Pool<Entity>() {
            @Override
            protected Entity newObject() {
                return new Entity();
            }
        };
        isDebug = false;
    }

    public MapContactListener(Engine engine, Pool<Entity> entityPool, Map<String, Entity> dataComponents, ObjectCache objectsCache) {
        this.engine = engine;
        this.entityPool = entityPool;
        this.contactDataComponents = dataComponents;
        this.objectsCache = objectsCache;
        isDebug = false;
    }

    public MapContactListener(Engine engine, ObjectCache objectsCache, boolean isDebug) {
        this.engine = engine;
        this.objectsCache = objectsCache;
        this.isDebug = isDebug;
        contactDataComponents = new HashMap<>();

        entityPool = new Pool<Entity>() {
            @Override
            protected Entity newObject() {
                return new Entity();
            }
        };
    }

    public MapContactListener(Engine engine, Pool<Entity> entityPool, Map<String, Entity> dataComponents, ObjectCache objectsCache, boolean isDebug) {
        this.engine = engine;
        this.entityPool = entityPool;
        this.contactDataComponents = dataComponents;
        this.objectsCache = objectsCache;
        this.isDebug = isDebug;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * Called when two fixtures begin to touch.
     *
     * @param contact data of the current contact.
     */
    @Override
    public void beginContact(Contact contact) {
        clearDataComponent();

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        UserData userDataA = getUserData(fixtureA);
        UserData userDataB = getUserData(fixtureB);
        if (userDataA == null || userDataB == null) return;

        if (isDebug) Gdx.app.log("beginContact", userDataA + " " + userDataB);

        ObjectEntity entityA = getEntity(userDataA);
        ObjectEntity entityB = getEntity(userDataB);
        if (entityA == null || entityB == null) return;

        // CAUTION - POLYGONS WITH COMPLEX SHAPES MAY INCORRECTLY ACTIVATE/DEACTIVATE THE TRIGGER!!!
        handlerBegun(contact, entityA, entityB, userDataA, userDataB);
    }

    /**
     * Called when two fixtures cease to touch.
     *
     * @param contact data of the current contact.
     */
    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        UserData userDataA = getUserData(fixtureA);
        UserData userDataB = getUserData(fixtureB);
        if (userDataA == null || userDataB == null) return;

        if (isDebug) Gdx.app.log("endContact", userDataA + " " + userDataB);

        handlerEnd(userDataA, userDataB);
    }

    /**
     * This is called after a contact is updated.
     * @see ContactListener
     */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    /**
     * This lets you inspect a contact after the solver is finished.
     * @see ContactListener
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    /**
     * Handles the start of communication between two objects.
     *
     * @param entityA   the first contact object
     * @param entityB   the second contact object
     * @param userDataA user data of the first object
     * @param userDataB user data of the second object
     */
    protected void handlerBegun(Contact contact, ObjectEntity entityA, ObjectEntity entityB, UserData userDataA, UserData userDataB) {
        String name = userDataA.name + userDataB.name;

        if (activeContacts.containsKey(name)) {
            activeContacts.computeIfPresent(name, (k, v) -> v + 1);
        } else {
            activeContacts.put(name, 1);
        }

        Entity entity = entityPool.obtain()
                .add(new CleanComponent())
                .add(new ContactTypeComponent(ContactType.BEGIN))
                .add(new ContactDataComponent(contact, entityA, entityB, userDataA, userDataB));

        engine.addEntity(entity);
        contactDataComponents.put(name, entity);
    }

    /**
     * Handles the end of the interaction between two objects.
     *
     * @param userDataA user data of the first object
     * @param userDataB user data of the second object
     */
    protected void handlerEnd(UserData userDataA, UserData userDataB) {
        String name = userDataA.name + userDataB.name;

        if (activeContacts.containsKey(name)) {
            activeContacts.computeIfPresent(name, (k, v) -> v - 1);
            Integer integer = activeContacts.get(name);

            if (integer == 0) {
                contactDataComponents.get(name).getComponent(ContactTypeComponent.class).type = ContactType.END;
            }
        }
    }

    /**
     * Clean unnecessary up unnecessary entities by CleanComponent.
     * If cleanComponent.isMustBeDelete == true, then object going to delete.
     */
    protected final void clearDataComponent() {
        if (contactDataComponents.isEmpty()) return;

        Iterator<Map.Entry<String, Entity>> iterator = contactDataComponents.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Entity> entry = iterator.next();
            Entity entity = entry.getValue();
            String key = entry.getKey();

            CleanComponent comp = entity.getComponent(CleanComponent.class);
            if (comp != null && comp.isMustBeDelete) {
                entityPool.free(entity);
                engine.removeEntity(entity);
                iterator.remove();
                activeContacts.remove(key);
                break;
            }
        }
    }

    protected final UserData getUserData(Fixture fixture) {
        return (UserData) fixture.getUserData();
    }

    /**
     * Getting ObjectEntity from {@link ObjectCache} by name owner {@link UserData}. If it is null, then method will seek ObjectEntity by its name.
     * @param userData user data fixtures.
     * @return ObjectEntity if it will be found, else null.
     */
    protected final ObjectEntity getEntity(UserData userData) {
        return objectsCache.getEntityMap().get(userData.owner == null || userData.owner.isEmpty() ? userData.name : userData.owner);
    }

}
