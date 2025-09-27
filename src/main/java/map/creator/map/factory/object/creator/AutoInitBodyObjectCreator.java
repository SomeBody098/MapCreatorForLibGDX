package map.creator.map.factory.object.creator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.body.BodyFactory;
import map.creator.map.factory.body.BodyParam;
import map.creator.map.factory.body.FormBody;
import map.creator.map.factory.body.UserData;

import java.util.Map;

/**
 * A special creator to automatically create a body for an object. Reads the JSON file, which MUST be in the properties of the object.
 * The property where the path to the JSON file is stored should be named "body-param" or you can change it name in constructor of the class.
 * According to the JSON file, it makes up BodyParam.
 * All fields in the BodyParam must be specified in the lowerCamelCase style.
 * If a field is don't specified, then this field will take the default values.
 * For example, you do not need to specify formBody - it is determined automatically (the exception is the override of the POLYGON shape to CHAIN or EDGE)
 <p><b>Example of a JSON file:</b></p>
 * <pre>
 * {@code
 *{
 *   "bodyDef": {
 *     "bodyType": "StaticBody,
 *     "angle": 0,
 *     "linearVelocity": {"x": 0, "y": 0},
 *     "angularVelocity": 0,
 *     "linearDamping": 0,
 *     "angularDamping": 0,
 *     "allowSleep": true,
 *     "awake": true,
 *     "fixedRotation": false,
 *     "bullet": false,
 *     "active": true,
 *     "gravityScale": 0
 *   },
 *   "fixture": {
 *     "friction": 0.2,
 *     "restitution": 0.1,
 *     "density": 1.0,
 *     "isSensor": false,
 *     "filter": {
 *       "categoryBits": 1,
 *       "maskBits": 65535,
 *       "groupIndex": 0
 *     }
 *   },
 *   "userData": {
 *       "name": "anybody",
 *       "type": "human",
 *       "owner": null
 *   },
 *   "formBody": CHAIN,
 *   "isLooping": true // In CHAIN form only
 * }
 * }
 * </pre>
 */
public abstract class AutoInitBodyObjectCreator implements ObjectCreator{

    private final String nameBodyParamJson;

    private final JsonReader reader;

    public AutoInitBodyObjectCreator() {
        reader = new JsonReader();
        nameBodyParamJson = "body-param";
    }

    public AutoInitBodyObjectCreator(String nameBodyParamJson) {
        reader = new JsonReader();
        this.nameBodyParamJson = nameBodyParamJson;
    }

    public abstract ObjectEntity createObject(String nameBody, MapProperties properties, Map<String, MapProperties> dataObjects, float unitScale, Body body);

    @Override
    public ObjectEntity createObject(String nameBody, MapProperties properties, Map<String, MapProperties> dataObjects, BodyFactory bodyFactory, FormBody formBody, Shape2D boundsObject) {
        BodyParam bodyParam = createBodyParam(properties, formBody, boundsObject);
        Body body = bodyFactory.createBody(bodyParam);

        return createObject(nameBody, properties, dataObjects, bodyFactory.getUnitScale(), body);
    }

    private BodyParam createBodyParam(MapProperties properties, FormBody formBody, Shape2D boundsObject) {
        if (properties.containsKey(nameBodyParamJson))
            return createBodyParamByJSON(reader.parse(Gdx.files.internal(properties.get(nameBodyParamJson, String.class))), formBody, boundsObject);
        else
            return createBodyParamByProperties(properties, formBody, boundsObject);
    }

    private BodyParam createBodyParamByProperties(MapProperties properties, FormBody formBody, Shape2D boundsObject) {
        BodyDef bodyDef = createBodyDefByProperties(properties);
        FixtureDef fixtureDef = createFixtureDefByProperties(properties);
        UserData userData = createUserDataByProperties(properties);

        return new BodyParam(
                FormBody.getFormBodyOnString(properties.get("formBody", formBody.name(), String.class)),
                bodyDef, fixtureDef, boundsObject, userData
        );
    }

    private UserData createUserDataByProperties(MapProperties properties) {
        return createUserData(
                properties.get("name", null, String.class),
                properties.get("type", null, String.class),
                properties.get("owner", null, String.class)
        );
    }

    private FixtureDef createFixtureDefByProperties(MapProperties properties) {
        return createFixtureDef(
                properties.get("friction", 0.2f, Float.class),
                properties.get("restitution", 0f, Float.class),
                properties.get("density", 0f, Float.class),
                properties.get("isSensor", false, Boolean.class),
                createFilter(
                        properties.get("categoryBits", (short) 0x0001, Short.class),
                        properties.get("maskBits", (short) -1, Short.class),
                        properties.get("groupIndex", (short) 0, Short.class)
                )
        );
    }

    private BodyDef createBodyDefByProperties(MapProperties properties){
        return createBodyDef(
                getBodyTypeByString(properties.get("type", "StaticBody", String.class)),
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

        return createUserData(
                userDataParam.getString("name", null),
                userDataParam.getString("type", null),
                userDataParam.getString("owner", null)
        );
    }

    private FixtureDef createFixtureDefByJSON(JsonValue jsonValue) {
        JsonValue fixtureDefParam = jsonValue.get("fixtureDef");
        JsonValue filterParam = fixtureDefParam.get("filter");

        return createFixtureDef(
                fixtureDefParam.getFloat("friction", 0.2f),
                fixtureDefParam.getFloat("restitution", 0),
                fixtureDefParam.getFloat("density", 0),
                fixtureDefParam.getBoolean("isSensor", false),
                createFilter(
                        filterParam.getShort("categoryBits", (short) 0x0001),
                        filterParam.getShort("maskBits", (short) -1),
                        filterParam.getShort("groupIndex", (short) 0)
                )
        );
    }

    private BodyDef createBodyDefByJSON(JsonValue jsonValue){
        JsonValue bodyDefParam = jsonValue.get("bodyDef");
        JsonValue linearVelocityParam = bodyDefParam.get("linearVelocity");

        return createBodyDef(
                getBodyTypeByString(bodyDefParam.getString("type", "StaticBody")),
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

    private UserData createUserData(String name, String type, String owner) {
        return new UserData(name, type, owner);
    }

    private FixtureDef createFixtureDef(float friction, float restitution, float density, boolean isSensor, Filter filter) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.density = density;
        fixtureDef.isSensor = isSensor;
        fixtureDef.filter.set(filter);

        return fixtureDef;
    }

    private Filter createFilter(short categoryBits, short maskBits, short groupIndex) {
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        filter.maskBits = maskBits;
        filter.groupIndex = groupIndex;

        return filter;
    }

    private BodyDef createBodyDef(BodyDef.BodyType type, float angle, Vector2 linearVelocity,
                                  float angularVelocity, float linearDamping, float angularDamping,
                                  boolean allowSleep, boolean awake, boolean fixedRotation,
                                  boolean bullet, boolean active, float gravityScale)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.angle = angle;
        bodyDef.linearVelocity.set(linearVelocity);
        bodyDef.angularVelocity = angularVelocity;
        bodyDef.linearDamping = linearDamping;
        bodyDef.angularDamping = angularDamping;
        bodyDef.allowSleep = allowSleep;
        bodyDef.awake = awake;
        bodyDef.fixedRotation = fixedRotation;
        bodyDef.bullet = bullet;
        bodyDef.active = active;
        bodyDef.gravityScale = gravityScale;

        return bodyDef;
    }

    private BodyDef.BodyType getBodyTypeByString(String type){
        switch (type){
            case "StaticBody":
                return BodyDef.BodyType.StaticBody;
            case "DynamicBody":
                return BodyDef.BodyType.DynamicBody;
            case "KinematicBody":
                return BodyDef.BodyType.KinematicBody;
            default:
                throw new IllegalArgumentException("Unknown body type: " + type);
        }
    }

}
