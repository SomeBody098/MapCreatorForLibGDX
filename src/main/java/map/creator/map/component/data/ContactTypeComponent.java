package map.creator.map.component.data;

import map.creator.map.component.ObjectComponent;

/// Contains type contact between two {@link com.badlogic.gdx.physics.box2d.Fixture}'s.
/// @see ContactType
public class ContactTypeComponent extends ObjectComponent {

    /**
     * Contact type between two objects.
     * @see ContactType
     */
    public ContactType type;

    public ContactTypeComponent(ContactType type) {
        super("contact-type-data", "data", null);
        this.type = type;
    }
}
