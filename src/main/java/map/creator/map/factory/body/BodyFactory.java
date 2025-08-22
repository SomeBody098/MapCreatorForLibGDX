package map.creator.map.factory.body;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

/**
 * One-stop factory for creating physical bodies and Box2D figures.
 * Combines the functionality of simple and complex factories into a single interface.
 *
 * <p><b>Features:</b></p>
 * <ul>
 * <li>Create solids of all supported shapes (rectangles, circles, polygons, ellipses, nets, edges)</li>
 * <li>Support for composite bodies (multiple fixtures on one body)</li>
 * <li>Automatic centering of composite bodies</li>
 * <li>Integration with debugging and visualization system</li>
 * <li>Coordinate conversion and scaling</li>
 * </ul>
 *
 * <p><b>Architecture:</b></p>
 * <ul>
 * <li>{@link BodySimpleFactory} - simple shapes (rectangles, circles)</li>
 * <li>{@link BodyDifficultFactory} - complex shapes (polygons, ellipses, chains)</li>
 * <li>{@link BodyFactoryDebugger} - debugging and logging system</li>
 * </ul>
 *
 * <p><b>Example of use:</b></p>
 * <pre>
 * {@code
 * // Creation of a factory
 * BodyFactory factory = new BodyFactory(world, 1/16f);
 * factory.setDebug(true); Enable debugging
 *
 * // Creating Body Parameters
 * BodyParam param = new BodyParam.BodyParamBuilder()
 *     .formBody(FormBody.RECTANGLE)
 *     .bodyDef(bodyDef)
 *     .fixtureDef(fixtureDef)
 *     .bounds(new Rectangle(10, 10, 32, 32))
 *     .userData(new UserData("player", "dynamic", null))
 *     .build();
 *
 * // Creation of the body
 * Body body = factory.createCollision(param);
 *
 * // Creating a Composite Body from Multiple Shapes
 * List<BodyParam> params = Arrays.asList(param1, param2, param3);
 * Body compoundBody = factory.createCollisionsUnderOneBody(params);
 * }
 * </pre>
 *
 * @see BodySimpleFactory
 * @see BodyDifficultFactory
 * @see BodyFactoryDebugger
 * @see BodyParam
 */
public class BodyFactory {

    private final World world;
    private final BodySimpleFactory simpleFactory;
    private final BodyDifficultFactory difficultFactory;
    private final BodyFactoryDebugger debugger;

    private float unitScale;

    public BodyFactory(World world, float unitScale) {
        this.world = world;
        this.unitScale = unitScale;
        debugger = new BodyFactoryDebugger(false);
        simpleFactory = new BodySimpleFactory(world, debugger);
        difficultFactory = new BodyDifficultFactory(world, debugger);
    }

    /**
     * Converts the string representation of the body type to Box2D enum.
     *
     * @param bodyType string representation of the type ("STATIC", "KINEMATIC", "DYNAMIC")
     * @return matching BodyType
     * @throws IllegalArgumentException if an unknown type is passed
     */
    public BodyType getBodyType(String bodyType){
        switch (bodyType.toUpperCase()){
            case "STATIC":
                return BodyType.StaticBody;
            case "KINEMATIC":
                return BodyType.KinematicBody;
            case "DYNAMIC":
                return BodyType.DynamicBody;
        }

        throw new IllegalArgumentException("Unknown body type - " + bodyType + ".");
    }

    /**
     * Getting isDebug value.
     * @return is debug mode. (true if on).
     */
    public boolean isDebug() {
        return debugger.isDebug();
    }

    /**
     * Setting debug mode.
     */
    public void setDebug(boolean debug) {
        debugger.setDebug(debug);
    }

    /**
     * Gets the scale of the conversion.
     */
    public float getUnitScale() {
        return unitScale;
    }

    /**
     * Sets the scale of the conversion.
     *
     * @param unitScale new scale pixels-meters
     */
    public void setUnitScale(float unitScale) {
        this.unitScale = unitScale;
    }

    /**
     * Creates a composite body consisting of several others specified in {@link BodyParam}.
     * All fixtures are created on a single body with automatic centering.
     *
     * @param bodyParams a list of parameters for objects.
     * @return created composite body.
     */
    public Body createCollisionsUnderOneBody(List<BodyParam> bodyParams) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.fixedRotation = true;

        bodyDef.position.set(toCenterBody(bodyParams, unitScale));
        Body body = world.createBody(bodyDef);

        for (BodyParam param : bodyParams) {
            for (Shape shape : createShapes(param, body.getPosition())) {
                param.fixtureDef.shape = shape;
                Fixture fixture = body.createFixture(param.fixtureDef);
                fixture.setUserData(param.userData);
                shape.dispose();
            }
        }

        return body;
    }

    /**
     * Creates an array of shapes for the specified parameters.
     *
     * @param param body parameters
     * @param center of the body for coordinate conversion
     * @return Shape Array
     * @throws IllegalArgumentException if an unknown shape or incompatible types are specified
     */
    public Shape[] createShapes(BodyParam param, Vector2 center){
        Shape[] shapes = new Shape[1];
        try {
            switch (param.formBody) {
                case RECTANGLE:
                    shapes[0] = simpleFactory.createRectangleShape(
                        (Rectangle) param.bounds, center, unitScale
                    );
                    break;
                case CIRCLE:
                    shapes[0] = simpleFactory.createCircleShape(
                        (Circle) param.bounds, center, unitScale
                    );
                    break;
                case ELLIPSE:
                    shapes = difficultFactory.createEllipseShapes(
                        (Ellipse) param.bounds, center, unitScale
                    );
                    break;
                case POLYGON:
                    shapes = difficultFactory.createPolygonShapes(
                        (Polygon) param.bounds, center, unitScale
                    );
                    break;
                case CHAIN:
                    shapes[0] = difficultFactory.createChainShape(
                        (Polygon) param.bounds, center, unitScale, param.isLooping
                    );
                    break;
                case EDGE:
                    shapes[0] = difficultFactory.createEdgeShape(
                        (Polygon) param.bounds, center, unitScale
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unknown name form - " + param.formBody.name() + ".");
            }
        } catch (ClassCastException e){
            throw new IllegalArgumentException("The shape of the object is incorrectly specified. Shape form - " + param.formBody.name() + ". Object - " + param.userData + ".");
        }

        return shapes;
    }

    /**
     * Creates a physical body with the specified parameters.
     *
     * @param param body parameters
     * @return created Box2D body
     * @throws IllegalArgumentException if an unknown form is specified
     */
    public Body createCollision(BodyParam param){
        switch (param.formBody){
            case RECTANGLE:
                return simpleFactory.createRectangleBody(
                    param.bodyDef,
                    param.fixtureDef,
                    (Rectangle) param.bounds,
                    unitScale,
                    param.userData
                );
            case CIRCLE:
                return simpleFactory.createCircleBody(
                    param.bodyDef,
                    param.fixtureDef,
                    (Circle) param.bounds,
                    unitScale,
                    param.userData
                );
            case ELLIPSE:
                return difficultFactory.createEllipse(
                    param.bodyDef,
                    param.fixtureDef,
                    (Ellipse) param.bounds,
                    unitScale,
                    param.userData
                );
            case POLYGON:
                return difficultFactory.createPolygon(
                    param.bodyDef,
                    param.fixtureDef,
                    (Polygon) param.bounds,
                    unitScale,
                    param.userData
                );
            case CHAIN:
                return difficultFactory.createChain(
                    param.bodyDef,
                    param.fixtureDef,
                    param.isLooping,
                    (Polygon) param.bounds,
                    unitScale,
                    param.userData
                );
            case EDGE:
                return difficultFactory.createEdge(
                    param.bodyDef,
                    param.fixtureDef,
                    (Polygon) param.bounds,
                    unitScale,
                    param.userData
                );
            default:
                throw new IllegalArgumentException("Unknown name form - " + param.formBody.name());
        }
    }

    /**
     * Calculates the center of mass for a composite body.
     *
     * @param bodyParams a list of fixture parameters
     * @param unitScale conversion scale
     * @return Center of Mass Vector
     */
    private Vector2 toCenterBody(List<BodyParam> bodyParams, float unitScale){
        Vector2 min = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
        Vector2 max = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);

        for (BodyParam param : bodyParams) {
            switch (param.formBody) {
                case RECTANGLE:
                    Rectangle rect = (Rectangle) param.bounds;
                    min.x = Math.min(min.x, rect.x);
                    min.y = Math.min(min.y, rect.y);
                    max.x = Math.max(max.x, rect.x + rect.width);
                    max.y = Math.max(max.y, rect.y + rect.height);
                    continue;

                case CIRCLE:
                    Circle circle = (Circle) param.bounds;
                    min.x = Math.min(min.x, circle.x - circle.radius);
                    min.y = Math.min(min.y, circle.y - circle.radius);
                    max.x = Math.max(max.x, circle.x + circle.radius);
                    max.y = Math.max(max.y, circle.y + circle.radius);
                    continue;

                case ELLIPSE:
                    Ellipse ellipse = ((Ellipse) param.bounds);
                    min.x = Math.min(min.x, ellipse.x);
                    min.y = Math.min(min.y, ellipse.y);
                    max.x = Math.max(max.x, ellipse.x + ellipse.width);
                    max.y = Math.max(max.y, ellipse.y + ellipse.height);
                    continue;

                case CHAIN:
                case POLYGON:
                    Polygon polygon = ((Polygon) param.bounds);
                    min.x = Math.min(min.x, polygon.getX());
                    min.y = Math.min(min.y, polygon.getY());
                    max.x = Math.max(max.x, polygon.getX() + polygon.area());
                    max.y = Math.max(max.y, polygon.getY() + polygon.area());
                    continue;

                case EDGE:
                    Polyline polyline = ((Polyline) param.bounds);
                    min.x = Math.min(min.x, polyline.getX());
                    min.y = Math.min(min.y, polyline.getY());
                    max.x = Math.max(max.x, polyline.getX() + polyline.getLength());
                    max.y = Math.max(max.y, polyline.getY() + polyline.getLength());
                    continue;
                default:
                    throw new IllegalArgumentException("Unknown name form - " + param.formBody.name() + ".");
            }
        }

        return new Vector2(
            (min.x + max.x) / 2 * unitScale,
            (min.y + max.y) / 2 * unitScale
        );
    }

    /**
     * Creates a fixture on the body with user data.
     *
     * @param body body to create a fixture
     * @param fixtureDef fixture settings
     * @param userData user data
     * @return created fixture
     */
    public Fixture createFixture(Body body, FixtureDef fixtureDef, Object userData){
        return simpleFactory.createFixture(body, fixtureDef, userData);
    }

}
