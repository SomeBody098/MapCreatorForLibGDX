package map.creator.map.factory.body.param;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Parses MapProperty to compose a BodyParam
 */
public class BodyParamParserByProperties implements BodyParamParser{

    private final BodyParamFactoryElements elements;

    protected BodyParamParserByProperties(BodyParamFactoryElements elements) {
        this.elements = elements;
    }

    @Override
    public BodyParam createBodyParam(MapProperties properties, FormBody formBody, Shape2D boundsObject) {
        BodyDef bodyDef = createBodyDefByProperties(properties);
        FixtureDef fixtureDef = createFixtureDefByProperties(properties);
        UserData userData = createUserDataByProperties(properties);

        return new BodyParam(
                FormBody.getFormBodyOnString(properties.get("formBody", formBody.name(), String.class)),
                bodyDef, fixtureDef, boundsObject, userData
        );
    }

    private UserData createUserDataByProperties(MapProperties properties) {
        return elements.createUserData(
                properties.get("name", null, String.class),
                properties.get("type", null, String.class),
                properties.get("owner", null, String.class)
        );
    }

    private FixtureDef createFixtureDefByProperties(MapProperties properties) {
        return elements.createFixtureDef(
                properties.get("friction", 0.2f, Float.class),
                properties.get("restitution", 0f, Float.class),
                properties.get("density", 0f, Float.class),
                properties.get("isSensor", false, Boolean.class),
                elements.createFilter(
                        properties.get("categoryBits", (short) 0x0001, Short.class),
                        properties.get("maskBits", (short) -1, Short.class),
                        properties.get("groupIndex", (short) 0, Short.class)
                )
        );
    }

    private BodyDef createBodyDefByProperties(MapProperties properties){
        return elements.createBodyDef(
                elements.getBodyTypeByString(properties.get("type", "StaticBody", String.class)),
                properties.get("angle", 0f, Float.class),
                new Vector2(properties.get("x", 0f, Float.class), properties.get("y", 0f, Float.class)),
                properties.get("angularVelocity", 0f, Float.class),
                properties.get("linearDamping", 0f, Float.class),
                properties.get("angularDamping", 0f, Float.class),
                properties.get("allowSleep", true, Boolean.class),
                properties.get("awake", true, Boolean.class),
                properties.get("fixedRotation", false, Boolean.class),
                properties.get("bullet", false, Boolean.class),
                properties.get("active", false, Boolean.class),
                properties.get("gravityScale", 1f, Float.class)
        );
    }
}
