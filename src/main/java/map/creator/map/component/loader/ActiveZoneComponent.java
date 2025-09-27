package map.creator.map.component.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import map.creator.map.component.ObjectComponent;
import map.creator.map.factory.body.param.UserData;

/// Contains and generates a fixture - a zone for enabling activity on entity's in {@link com.badlogic.ashley.core.Engine} and on dynamic body's in Box2D.
/// All {@link Body}'s, which will be contacted ActiveZone, will be get up
/// ({@code body.setAwake(false)}).
public class ActiveZoneComponent extends ObjectComponent {

    private final Fixture activeZone;

    public ActiveZoneComponent(String owner, FixtureDef fixtureDef, Body body) {
        super("active-zone", "active-zone", owner);
        if (!fixtureDef.isSensor) {
            fixtureDef.isSensor = true;
            Gdx.app.error("ActiveZoneComponent", "The shape ActiveZoneComponent must be sensor! (isSensor = true)\nFor this reason, isSensor been transferred to true in owner " + owner + ".", new IllegalArgumentException());
        }

        activeZone = body.createFixture(fixtureDef);
        activeZone.setUserData(
            new UserData(
                "active-zone",
                "active-zone",
                owner
            )
        );

    }

    public Fixture getActiveZone() {
        return activeZone;
    }
}
