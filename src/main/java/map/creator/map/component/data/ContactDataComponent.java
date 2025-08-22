package map.creator.map.component.data;

import com.badlogic.gdx.physics.box2d.Contact;
import map.creator.map.component.ObjectComponent;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.body.UserData;

/// A class that contains the state of a contact
/// @see ContactType
public class ContactDataComponent extends ObjectComponent {

    /**
     * Current contact.
     * @see Contact
     */
    public final Contact contact;

    /**
     * First {@link ObjectEntity}, which contains {@link com.badlogic.gdx.physics.box2d.Fixture} participating in contact.
     */
    public final ObjectEntity AEntity;

    /**
     * Second {@link ObjectEntity}, which contains {@link com.badlogic.gdx.physics.box2d.Fixture} participating in contact.
     */
    public final ObjectEntity BEntity;

    /**
     * The user data of the first {@link com.badlogic.gdx.physics.box2d.Fixture} participating in contact.
     * @see UserData
     */
    public final UserData AFixtureData;

    /**
     * The user data of the second {@link com.badlogic.gdx.physics.box2d.Fixture} participating in contact.
     * @see UserData
     */
    public final UserData BFixtureData;

    public ContactDataComponent(Contact contact, ObjectEntity AEntity, ObjectEntity BEntity, UserData AFixtureData, UserData BFixtureData) {
        super("contact-data", "data", null);
        this.contact = contact;
        this.AEntity = AEntity;
        this.BEntity = BEntity;
        this.AFixtureData = AFixtureData;
        this.BFixtureData = BFixtureData;
    }

}
