package map.creator.map.component.body;

import com.badlogic.gdx.physics.box2d.Body;
import map.creator.map.component.ObjectComponent;

/// Component that stores the body
/// @see Body
public class BodyComponent extends ObjectComponent {

    private final Body body;

    public BodyComponent(Body body, String owner) {
        super("body-component", "body", owner);
        this.body = body;
    }

    public Body getBody() {
        return body;
    }
}
