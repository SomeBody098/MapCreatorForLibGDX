package map.creator.map.factory.body.param;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Creates elements for BodyParam
 */
public class BodyParamFactoryElements {

    protected BodyParamFactoryElements() {

    }

    /**
     * Creating {@link UserData}
     * @param name name of object
     * @param type type of object
     * @param owner owner of this object
     * @return {@link UserData}
     */
    public UserData createUserData(String name, String type, String owner) {
        return new UserData(name, type, owner);
    }


    public FixtureDef createFixtureDef(float friction, float restitution, float density, boolean isSensor, Filter filter) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.density = density;
        fixtureDef.isSensor = isSensor;
        fixtureDef.filter.set(filter);

        return fixtureDef;
    }

    public Filter createFilter(short categoryBits, short maskBits, short groupIndex) {
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        filter.maskBits = maskBits;
        filter.groupIndex = groupIndex;

        return filter;
    }

    public BodyDef createBodyDef(BodyDef.BodyType type, float angle, Vector2 linearVelocity,
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

    public BodyDef.BodyType getBodyTypeByString(String type){
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
