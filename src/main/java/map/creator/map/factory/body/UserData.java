package map.creator.map.factory.body;

/**
 * A class for storing user data associated with physical objects (e.g. Fixture in Box2D).
 * Contains information about the name, type and owner of the object, which allows you to identify it
 * when handling collisions and other game events.
 *
 * Example of use:
 * <pre>
 * {@code
 * FixtureDef fixtureDef = new FixtureDef();
 * fixtureDef.userData = new UserData("sword_hitbox", "attack_zone", "player_1");
 * }
 * </pre>
 */
public class UserData {

    /**
     * Unique name of the object (for example, "door_1", "arrow_42").
     * Must not be null or empty.
     */
    public final String name;

    /**
     * Object type (e.g. "trigger", "attack_zone", "npc").
     * Used for filtering when handling collisions.
     */
    public final String type;

    /**
     * The name of the owner of the object (e.g. "player_1" for the player's sword).
     * If the object is not owned by anyone, you can use "" or null. Then, in the event of a collision, the search for the entity will be determined by its name
     */
    public final String owner;

    /**
     * Creates a new instance of UserData.
     *
     * @param name Unique name of the object (must not be null).
     * @param type Object type (e.g. "hitbox", "sensor").
     * @param owner Owner's name (if any).
     * @throws IllegalArgumentException if the name or type is null.
     */
    public UserData(String name, String type, String owner) {
        if (name == null || type == null) {
            throw new IllegalArgumentException("Name and type cannot be null");
        }
        this.name = name;
        this.type = type;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "UserData{" +
            "name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", owner='" + owner + '\'' +
            '}';
    }
}
