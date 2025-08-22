package map.creator.map.factory.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

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
     * Print info about created shape.
     * @param form The form shape (RECTANGLE, CIRCLE... - learn more in {@link FormBody})
     * @param shape Bounds shape.
     * @param unitScale scale of the conversion.
     */
    public void debugPrintAboutShape(FormBody form, Shape2D shape, float unitScale){
        if (!isDebug) return;

        switch (form){
            case CIRCLE:
                Circle circle = (Circle) shape;
                circle.set(circle.x * unitScale, circle.y * unitScale, circle.radius / 2 * unitScale);

                Gdx.app.log("BodyFactory",
                    String.format("Shape was created! Form: %s, Bounds: %s", form.name(), circle)
                );
                return;
            case RECTANGLE:
                Rectangle rectangle = (Rectangle) shape;
                rectangle.set(rectangle.x * unitScale, rectangle.y * unitScale, rectangle.width / 2 * unitScale, rectangle.height / 2 * unitScale);

                Gdx.app.log("BodyFactory",
                    String.format("Shape was created! Form: %s, Bounds: %s", form.name(), rectangle)
                );
                return;
            case ELLIPSE:
                Ellipse ellipse = (Ellipse) shape;
                ellipse.set(ellipse.x * unitScale, ellipse.y * unitScale, ellipse.width / 2 * unitScale, ellipse.height / 2 * unitScale);

                Gdx.app.log("BodyFactory",
                    String.format("Shape was created! Form: %s, Bounds: %s",
                        form.name(),
                        "[" + ellipse.x + "," + ellipse.y + "," + ellipse.width + "," + ellipse.height + "]"
                    )
                );
                return;
            case CHAIN:
                Polygon polygon = (Polygon) shape;

                Gdx.app.log("BodyFactory",
                    String.format("Shape was created! Form: %s, Bounds: [x: %s], [y: %s], [area: %s]",
                        form.name(),
                        polygon.getX() * unitScale,
                        polygon.getY() * unitScale,
                        polygon.area() * unitScale
                    )
                );
                return;
            case EDGE:
                Gdx.app.log("BodyFactory", "https://youtu.be/dQw4w9WgXcQ?si=7Jk3XXtue7P64M5p");
        }
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
