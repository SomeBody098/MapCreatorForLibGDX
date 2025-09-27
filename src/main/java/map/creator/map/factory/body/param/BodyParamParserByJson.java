package map.creator.map.factory.body.param;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Parses the JSON file to compose the BodyParam
 */
public class BodyParamParserByJson implements BodyParamParser{

    /**
     * The name of property, which contains the path to the file.
     */
    private final String nameBodyParamJson;

    /**
     * Lightweight JSON parser.
     * @see JsonReader
     */
    private final JsonReader reader;


    private final BodyParamFactoryElements createElements;

    protected BodyParamParserByJson(BodyParamFactoryElements createElements, String nameBodyParamJson) {
        reader = new JsonReader();
        this.createElements = createElements;
        this.nameBodyParamJson = nameBodyParamJson;
    }

    @Override
    public BodyParam createBodyParam(MapProperties properties, FormBody formBody, Shape2D bounds) {
        if (!properties.containsKey(nameBodyParamJson)) return null;
        return createBodyParamByJSON(reader.parse(Gdx.files.internal(properties.get(nameBodyParamJson, String.class))), formBody, bounds);
    }

    private BodyParam createBodyParamByJSON(JsonValue jsonValue, FormBody defaultForm, Shape2D boundsObject){
        BodyDef bodyDef = createBodyDefByJSON(jsonValue);
        FixtureDef fixtureDef = createFixtureDefByJSON(jsonValue);
        UserData userData = createUserDataByJSON(jsonValue);

        return new BodyParam(
                FormBody.getFormBodyOnString(jsonValue.getString("formBody", defaultForm.name())),
                bodyDef, fixtureDef, boundsObject, userData
        );
    }

    private UserData createUserDataByJSON(JsonValue jsonValue) {
        JsonValue userDataParam = jsonValue.get("userData");

        return createElements.createUserData(
                userDataParam.getString("name", null),
                userDataParam.getString("type", null),
                userDataParam.getString("owner", null)
        );
    }

    private FixtureDef createFixtureDefByJSON(JsonValue jsonValue) {
        JsonValue fixtureDefParam = jsonValue.get("fixtureDef");
        JsonValue filterParam = fixtureDefParam.get("filter");

        return createElements.createFixtureDef(
                fixtureDefParam.getFloat("friction", 0.2f),
                fixtureDefParam.getFloat("restitution", 0),
                fixtureDefParam.getFloat("density", 0),
                fixtureDefParam.getBoolean("isSensor", false),
                createElements.createFilter(
                        filterParam.getShort("categoryBits", (short) 0x0001),
                        filterParam.getShort("maskBits", (short) -1),
                        filterParam.getShort("groupIndex", (short) 0)
                )
        );
    }

    private BodyDef createBodyDefByJSON(JsonValue jsonValue){
        JsonValue bodyDefParam = jsonValue.get("bodyDef");
        JsonValue linearVelocityParam = bodyDefParam.get("linearVelocity");

        return createElements.createBodyDef(
                createElements.getBodyTypeByString(bodyDefParam.getString("type", "StaticBody")),
                bodyDefParam.getFloat("angle", 0),
                new Vector2(linearVelocityParam.getFloat("x", 0), linearVelocityParam.getFloat("y", 0)),
                bodyDefParam.getFloat("angularVelocity", 0),
                bodyDefParam.getFloat("linearDamping", 0),
                bodyDefParam.getFloat("angularDamping", 0),
                bodyDefParam.getBoolean("allowSleep", true),
                bodyDefParam.getBoolean("awake", true),
                bodyDefParam.getBoolean("fixedRotation", false),
                bodyDefParam.getBoolean("bullet", false),
                bodyDefParam.getBoolean("active", false),
                bodyDefParam.getFloat("gravityScale", 1)
        );
    }

}
