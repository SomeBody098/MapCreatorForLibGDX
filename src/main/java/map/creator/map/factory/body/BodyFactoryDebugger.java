package map.creator.map.factory.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.Arrays;

/**
 * The debugger for {@link BodyFactory}, {@link BodySimpleFactory} and {@link BodyDifficultFactory}.
 */
public class BodyFactoryDebugger {

    private boolean isDebug;

    protected BodyFactoryDebugger(boolean isDebug) {
        this.isDebug = isDebug;
    }

    /**
     * Getting isDebug value.
     * @return is debug mode. (true if on).
     */
    public boolean isDebug() {
        return isDebug;
    }

    /**
     * Setting debug mode.
     */
    public void setDebug(boolean debug) {
        this.isDebug = debug;
    }

    /**
     * Print info about created shape in {@link BodySimpleFactory}.
     * @param x location of the shape by X coordinate
     * @param y location of the shape by Y coordinate
     * @param width width of shape
     * @param height height of shape
     * @param form The form shape (RECTANGLE, CIRCLE... - learn more in {@link FormBody})
     */
    public void debugPrintAboutSimpleShape(float x, float y, float width, float height, FormBody form){
        if (!isDebug) return;

        switch (form){
            case CIRCLE:
                Gdx.app.log("BodyFactory",
                    String.format("Shape was created! Form: %s, Bounds: %s", form.name(), "[" + x + "," + y + "," + width + "]")
                );
                return;
            case RECTANGLE:
            case ELLIPSE:
                Gdx.app.log("BodyFactory",
                    String.format("Shape was created! Form: %s, Bounds: %s",
                        form.name(),
                        "[" + x + "," + y + "," + width + "," + height + "]"
                    )
                );
        }
    }

    /**
     * Print info about created shape in {@link BodyDifficultFactory}.
     * @param transformVertices all vertices of the hard shape
     * @param form The form shape (RECTANGLE, CIRCLE... - learn more in {@link FormBody})
     */
    public void debugPrintAboutDifficultShape(float[] transformVertices, FormBody form){
        Gdx.app.log(
                "BodyFactory",
                String.format(
                        "Shape was created! Form: %s, Vertices: [%s]",
                        form.name(), Arrays.toString(transformVertices)
                )
        );
    }



    /**
     * Print info for created body.
     * @param form The form shape (RECTANGLE, CIRCLE... - learn more in {@link FormBody})
     * @param userData The {@link UserData} body
     * @param fixtureDef A fixture definition. {@link FixtureDef}
     * @param bodyDef A body definition. {@link BodyDef}
     * @param x The x position.
     * @param y The y position.
     * @param width Width body.
     * @param height Height body.
     */
    public void debugPrintAboutBody(FormBody form, Object userData, FixtureDef fixtureDef, BodyDef bodyDef, float x, float y, float width, float height) {
        if (!isDebug) return;

        switch (form){
            case CIRCLE:
                Gdx.app.log("BodyFactory",
                    String.format(
                        "Body was created! [form: %s], [userData: %s],\n[fixtureDef: %s],\n[BodyDef: %s],\n[bounds: (x: %s), (y: %s), (radius: %s)]",
                        form, userData, getStringImageFixtureDef(fixtureDef), getStringImageBodyDef(bodyDef), x, y, width
                    )
                );
                return;
            case RECTANGLE:
            case ELLIPSE:
                Gdx.app.log("BodyFactory",
                    String.format(
                        "Body was created! [form: %s], [userData: %s],\n[fixtureDef: %s],\n[BodyDef: %s],\n[bounds: (x: %s), (y: %s), (width: %s), (height: %s)]",
                        form, userData, getStringImageFixtureDef(fixtureDef), getStringImageBodyDef(bodyDef), x, y, width, height
                    )
                );
                return;
            case CHAIN:
                Gdx.app.log("BodyFactory",
                    String.format(
                        "Body was created! [form: %s], [userData: %s],\n[fixtureDef: %s],\n[BodyDef: %s],\n[bounds: (x: %s), (y: %s), (area: %s)]",
                        form, userData, getStringImageFixtureDef(fixtureDef), getStringImageBodyDef(bodyDef), x, y, width
                    )
                );
                return;
            case EDGE:
                Gdx.app.log("BodyFactory",
                    String.format(
                        "Body was created! [form: %s], [userData: %s],\n[fixtureDef: %s],\n[BodyDef: %s],\n[bounds: (x: %s), (y: %s), (length: %s)]",
                        form, userData, getStringImageFixtureDef(fixtureDef), getStringImageBodyDef(bodyDef), x, y, width
                    )
                );
        }
    }

    /**
     * Parsing {@link BodyDef} to string.
     * @param bodyDef A body definition.
     * @return string with parameters {@link BodyDef}.
     */
    private String getStringImageBodyDef(BodyDef bodyDef){
        return String.format(
            "(type: %s), (angle: %s), (linearVelocity: %s), (angularVelocity: %s), (linearDamping: %s), (angularDamping: %s),\n" +
                "(allowSleep: %s), (awake: %s), (fixedRotation: %s), (bullet: %s), (active: %s), (gravityScale: %s)",
            bodyDef.type, bodyDef.angle, bodyDef.linearVelocity, bodyDef.angularVelocity, bodyDef.linearDamping,
            bodyDef.angularDamping, bodyDef.allowSleep, bodyDef.awake, bodyDef.fixedRotation, bodyDef.bullet,
            bodyDef.active, bodyDef.gravityScale
        );
    }

    /**
     * Parsing {@link FixtureDef} to string.
     * @param fixtureDef A fixture definition.
     * @return string with parameters {@link FixtureDef}.
     */
    private String getStringImageFixtureDef(FixtureDef fixtureDef){
        return String.format(
            "(friction: %s), (restitution: %s), (density: %s), (isSensor: %s)",
            fixtureDef.friction, fixtureDef.restitution, fixtureDef.density, fixtureDef.isSensor
        );
    }
}
