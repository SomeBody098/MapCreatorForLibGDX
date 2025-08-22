package map.creator.map.factory.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Factory for creating simple physical bodies and Box2D figures.
 * Specializes in creating circles and rectangles - basic Box2D primitives.
 *
 * <p><b>Features:</b></p>
 * <ul>
 * <li>Creating CircleShapes</li>
 * <li>Create rectangular solids (PolygonShape with setAsBox)</li>
 * <li>Automatic calculation of centers of mass</li>
 * <li>Correct coordinate conversion and scaling</li>
 * <li>Integration with the debugging and logging system</li>
 * <li>Automatic memory management (dispose shapes)</li>
 * </ul>
 *
 * @see BodyDifficultFactory
 * @see BodyFactory
 * @see BodyFactoryDebugger
 */
public class BodySimpleFactory {

    private final World world;
    private final BodyFactoryDebugger debugger;

    protected BodySimpleFactory(World world, BodyFactoryDebugger debugger) {
        this.world = world;
        this.debugger = debugger;
    }

    /**
     * Creates a circular physical body.
     *
     * @param def body settings (the position will be overwritten)
     * @param fixtureDef fixture settings
     * @param bounds circle that defines the shape and dimensions
     * @param unitScale conversion scale pixels-to-meters
     * @param userData user data for fixture
     * @return created circular body
     */
    public Body createCircleBody(BodyDef def, FixtureDef fixtureDef, Circle bounds, float unitScale, Object userData) {
        float centerX = (bounds.x + bounds.radius / 2) * unitScale;
        float centerY = (bounds.y + bounds.radius / 2) * unitScale;
        def.position.set(centerX, centerY);

        Body body = world.createBody(def);

        CircleShape shape = createCircleShape(bounds, body.getPosition(), unitScale);
        fixtureDef.shape = shape;

        Fixture fixture = createFixture(body, fixtureDef, userData);
        shape.dispose();

        debugger.debugPrintAboutBody(
            FormBody.CIRCLE, userData, fixtureDef, def,
            bounds.x * unitScale, bounds.y * unitScale,
            (bounds.radius / 2) * unitScale, 0
        );

        return body;
    }

    /**
     * Creates a rectangular physical body.
     *
     * @param def body settings (the position will be overwritten)
     * @param fixtureDef fixture settings
     * @param bounds rectangle that defines the shape and dimensions
     * @param unitScale conversion scale pixels-to-meters
     * @param userData user data for fixture
     * @return created rectangular body
     */
    public Body createRectangleBody(BodyDef def, FixtureDef fixtureDef, Rectangle bounds, float unitScale, Object userData) {
        float centerX = (bounds.x + bounds.width / 2) * unitScale;
        float centerY = (bounds.y + bounds.height / 2) * unitScale;
        def.position.set(centerX, centerY);

        Gdx.app.log("ggg", bounds.toString());

        Body body = world.createBody(def);

        PolygonShape shape = createRectangleShape(bounds, body.getPosition(), unitScale);
        fixtureDef.shape = shape;

        Fixture fixture = createFixture(body, fixtureDef, userData);
        shape.dispose();

        debugger.debugPrintAboutBody(
            FormBody.RECTANGLE, userData, fixtureDef, def,
            bounds.getX() * unitScale, bounds.getY() * unitScale,
            bounds.width / 2 * unitScale, bounds.height / 2 * unitScale
        );

        return body;
    }

    /**
     * Creates a circular shape at the local coordinates of the body.
     *
     * @param circle in pixel coordinates
     * @param center of the body in meters
     * @param unitScale conversion scale
     * @return created by CircleShape
     */
    public CircleShape createCircleShape(Circle circle, Vector2 center, float unitScale){
        CircleShape circleShape = new CircleShape();

        float hr = (circle.radius / 2) * unitScale;
        float centerX = (circle.x + circle.radius / 2) * unitScale - center.x;
        float centerY = (circle.y + circle.radius / 2) * unitScale - center.y;

        circleShape.setRadius(hr);
        circleShape.setPosition(new Vector2(centerX, centerY));

        debugger.debugPrintAboutShape(FormBody.CIRCLE, circle, unitScale);

        return circleShape;
    }

    /**
     * Creates a rectangular shape at the local coordinates of the body.
     *
     * @param rectangle rectangle in pixel coordinates
     * @param center of the body in meters
     * @param unitScale conversion scale
     * @return created by PolygonShape
     */
    public PolygonShape createRectangleShape(Rectangle rectangle, Vector2 center, float unitScale){
        PolygonShape polygonShape = new PolygonShape();

        float hx = (rectangle.width / 2) * unitScale;
        float hy = (rectangle.height / 2) * unitScale;
        float centerX = (rectangle.x + rectangle.width / 2) * unitScale - center.x;
        float centerY = (rectangle.y + rectangle.height / 2) * unitScale - center.y;

        polygonShape.setAsBox(hx, hy, new Vector2(centerX, centerY), 0);

        debugger.debugPrintAboutShape(FormBody.RECTANGLE, rectangle, unitScale);

        return polygonShape;
    }

    /**
     * Creates a fixture on the body with user data.
     * Utilitarian method for uniform creation of fixtures.
     *
     * @param body body to create a fixture
     * @param fixtureDef fixture settings
     * @param userData user data
     * @return created fixture
     */
    public Fixture createFixture(Body body, FixtureDef fixtureDef, Object userData){
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(userData);

        return fixture;
    }
}
