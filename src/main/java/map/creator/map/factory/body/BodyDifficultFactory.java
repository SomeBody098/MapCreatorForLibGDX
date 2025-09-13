package map.creator.map.factory.body;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ShortArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating complex physical bodies and figures in BOX2D.
 * Supports the creation of ellipses, polygons, chains and Edge figure with automatic triangulation.
 *
 * <p> <b> Features: </b> </p>
 * <ul>
 * <li> Automatic triangulation of complex landfills (more 8 peaks) </li>
 * <li> support of ellipses through approximation by polygons </li>
 * <li> correct converting coordinates and scaling </li>
 * <li> Integration with the debugging system </li>
 * </ul>
 *
 * @see BodyFactory
 * @see BodyFactoryDebugger
 * @see com.badlogic.gdx.math.EarClippingTriangulator
 */
public class BodyDifficultFactory {

    private final World world;
    private final BodyFactoryDebugger debugger;

    protected BodyDifficultFactory(World world, BodyFactoryDebugger debugger) {
        this.world = world;
        this.debugger = debugger;
    }

    /**
     * Creates an edge solid.
     *
     * @param def body settings
     * @param fixtureDef fixture settings
     * @param bounds polygon defining the shape of the edge
     * @param unitScale conversion scale pixels-to-meters
     * @param userData user data for fixture
     * @return created body
     *
     * @throws IllegalArgumentException if the polygon does not contain 4 vertices
     */
    public Body createEdge(BodyDef def, FixtureDef fixtureDef, Polygon bounds, float unitScale, Object userData) {
        def.position.set(bounds.getX(), bounds.getY());
        Body body = world.createBody(def);

        Shape shape = createEdgeShape(bounds, body.getPosition(), unitScale);
        fixtureDef.shape = shape;

        Fixture fixture = createFixture(body, fixtureDef, userData);
        shape.dispose();

        return body;
    }

    /**
     * Creates an ellipse-shaped body.
     * The ellipse is approximated by a polygon with an automatically calculated number of segments.
     *
     * @param def body settings
     * @param fixtureDef fixture settings
     * @param bounds ellipse that defines the shape
     * @param unitScale conversion scale pixels-to-meters
     * @param userData user data for fixture
     * @return created body
     */
    public Body createEllipse(BodyDef def, FixtureDef fixtureDef, Ellipse bounds, float unitScale, Object userData) {
        def.position.set(
            (bounds.x + bounds.width / 2) * unitScale,
            (bounds.y + bounds.height / 2) * unitScale
        );
        Body body = world.createBody(def);

        for (PolygonShape shape : createEllipseShapes(bounds, body.getPosition(), unitScale)) {
            fixtureDef.shape = shape;

            Fixture fixture = createFixture(body, fixtureDef, userData);
            shape.dispose();
        }

        debugger.debugPrintAboutBody(
            FormBody.ELLIPSE, userData, fixtureDef, def,
            bounds.x * unitScale, bounds.y * unitScale,
            (bounds.width / 2) * unitScale, (bounds.height / 2) * unitScale
        );

        return body;
    }

    /**
     * Creates a solid with a complex polygonal shape.
     * Polygons with more than 8 vertices are automatically triangulated.
     *
     * @param def body settings
     * @param fixtureDef fixture settings
     * @param bounds polygon that defines the shape
     * @param unitScale conversion scale pixels-to-meters
     * @param userData user data for fixture
     * @return created body
     *
     * @throws IllegalArgumentException if the polygon has the wrong number of vertices
     */
    public Body createPolygon(BodyDef def, FixtureDef fixtureDef, Polygon bounds, float unitScale, Object userData) {
        def.position.set(bounds.getX(), bounds.getY());
        Body body = world.createBody(def);

        for (PolygonShape shape : createPolygonShapes(bounds, body.getPosition(), unitScale)) {
            fixtureDef.shape = shape;

            Fixture fixture = createFixture(body, fixtureDef, userData);
            shape.dispose();
        }

        debugger.debugPrintAboutBody(
            FormBody.CHAIN, userData, fixtureDef, def,
            bounds.getX() * unitScale, bounds.getY() * unitScale,
            bounds.area() * unitScale, 0
        );

        return body;
    }

    /**
     * Creates a chain - a sequence of connected edge segments.
     *
     * @param def body settings
     * @param fixtureDef fixture settings
     * @param isLooping, if true, creates a closed circuit
     * @param bounds polygon defining the vertices of the circuit
     * @param unitScale conversion scale pixels-to-meters
     * @param userData user data for fixture
     * @return created body
     */
    public Body createChain(BodyDef def, FixtureDef fixtureDef, boolean isLooping, Polygon bounds, float unitScale, Object userData) {
        def.position.set(bounds.getX(), bounds.getY());
        Body body = world.createBody(def);

        Shape shape = createChainShape(bounds, body.getPosition(), unitScale, isLooping);
        fixtureDef.shape = shape;

        Fixture fixture = createFixture(body, fixtureDef, userData);
        shape.dispose();

        debugger.debugPrintAboutBody(
            FormBody.CHAIN, userData, fixtureDef, def,
            bounds.getX() * unitScale, bounds.getY() * unitScale,
            bounds.area() * unitScale, 0
        );

        return body;
    }

    /**
     * Creates an EdgeShape from a polygon.
     * Uses the first 4 vertices of a polygon to create a line.
     *
     * @param polygon polygon with vertices
     * @param center of the body for coordinate conversion
     * @param unitScale conversion scale
     * @return created edge shape
     *
     * @throws IllegalArgumentException if the polygon contains fewer than 4 vertices
     */
    public EdgeShape createEdgeShape(Polygon polygon, Vector2 center, float unitScale) {
        float[] vertices = getTransformedVerticesOnUnitScale(polygon.getTransformedVertices(), center, unitScale);

        if (vertices.length != 4) throw new IllegalArgumentException("Edge shape must have 4 vertices!");

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(vertices[0], vertices[1], vertices[2], vertices[4]);

        debugger.debugPrintAboutDifficultShape(vertices, FormBody.EDGE);

        return edgeShape;
    }

    /**
     * Creates an array of polygonal shapes that approximate an ellipse.
     *
     * @param ellipse ellipse for approximation
     * @param center of the body for coordinate conversion
     * @param unitScale conversion scale
     * @return an array of PolygonShapes representing an ellipse
     */
    public PolygonShape[] createEllipseShapes(Ellipse ellipse, Vector2 center, float unitScale) {
        int segments = calculateSegments(ellipse.width, ellipse.height);
        float angleStep = 2 * MathUtils.PI / segments;

        float[] vertices = new float[segments * 2];
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            float x = ((ellipse.width / 2 * MathUtils.cos(angle)) * unitScale) + (((ellipse.x + ellipse.width / 2) * unitScale) - center.x);
            float y = ((ellipse.height / 2 * MathUtils.sin(angle)) * unitScale) + (((ellipse.y + ellipse.height / 2) * unitScale) - center.y);

            vertices[i * 2] = x;
            vertices[i * 2 + 1] = y;
        }

        debugger.debugPrintAboutDifficultShape(vertices, FormBody.POLYGON);

        return computeTriangles(vertices);
    }

    /**
     * Creates an array of polygon shapes from a polygon.
     * Triangulates if necessary.
     *
     * @param polygon source polygon
     * @param center of the body for coordinate conversion
     * @param unitScale conversion scale
     * @return PolygonShape array
     */
    public PolygonShape[] createPolygonShapes(Polygon polygon, Vector2 center, float unitScale) {
        float[] transformVertices = getTransformedVerticesOnUnitScale(polygon.getTransformedVertices(), center, unitScale);
        PolygonShape[] polygonShapes = computeTriangles(transformVertices);

        debugger.debugPrintAboutDifficultShape(transformVertices, FormBody.POLYGON);
        return polygonShapes;
    }

    /**
     * Creates a ChainShape from a polygon.
     *
     * @param polygon polygon with net vertices
     * @param center of the body for coordinate conversion
     * @param unitScale conversion scale
     * @param isLooping, if true, creates a closed circuit
     * @return created chain shape
     */
    public ChainShape createChainShape(Polygon polygon, Vector2 center, float unitScale, boolean isLooping) {
        float[] transformVertices = getTransformedVerticesOnUnitScale(polygon.getTransformedVertices(), center, unitScale);

        ChainShape shape = new ChainShape();
        if (isLooping) shape.createLoop(transformVertices);
        else shape.createChain(transformVertices);

        debugger.debugPrintAboutDifficultShape(transformVertices, FormBody.CHAIN);

        return shape;
    }

    /**
     * Calculates the optimal number of segments to approximate the ellipse.
     *
     * @param width ellipse width
     * @param height ellipse height
     * @return number of segments (8-24)
     */
    private int calculateSegments(float width, float height) {
        float avgSize = (width + height) / 2;
        float stretch = Math.max(width, height) / Math.min(width, height);
        int segments = (int) (8 + (avgSize * 0.1f) + (stretch * 2));

        return MathUtils.clamp(segments, 8, 24);
    }

    /**
     * Converts vertices to the local coordinates of the solid, taking into account the scale.
     *
     * @param vertices source vertices in pixels
     * @param center of the body
     * @param unitScale conversion scale
     * @return converted vertices in meters
     */
    private float[] getTransformedVerticesOnUnitScale(float[] vertices, Vector2 center, float unitScale) {
        float[] transformVertices = new float[vertices.length];

        for (int i = 0; i < transformVertices.length; i += 2) {
            transformVertices[i] = ((vertices[i] * unitScale) - center.x);
            transformVertices[i + 1] = ((vertices[i + 1] * unitScale) - center.y);
        }

        return transformVertices;
    }

    /**
     * Triangulates the polygon into triangles.
     * Uses the Ear Clipping algorithm for polygons with more than 8 vertices.
     *
     * @param vertices of polygon vertices
     * @return an array of PolygonShapes representing triangles
     */
    private PolygonShape[] computeTriangles(float[] vertices) {
        PolygonShape[] shapes;

        if (vertices.length > 8) {
            EarClippingTriangulator triangulator = new EarClippingTriangulator();
            ShortArray triangles = triangulator.computeTriangles(vertices);

            List<PolygonShape> shapesList = new ArrayList<>();

            for (int i = 0; i < triangles.size; i += 3) {
                float[] triangle = new float[6];
                for (int j = 0; j < 3; j++) {
                    int vertexIndex = triangles.get(i + j) * 2;
                    triangle[j * 2] = vertices[vertexIndex];
                    triangle[j * 2 + 1] = vertices[vertexIndex + 1];
                }

                PolygonShape shape = new PolygonShape();
                shape.set(triangle);
                shapesList.add(shape);
            }

            shapes = shapesList.toArray(new PolygonShape[0]);
        } else {
            PolygonShape shape = new PolygonShape();
            shape.set(vertices);

            shapes = new PolygonShape[1];
            shapes[0] = shape;
        }

        return shapes;
    }

    /**
     * Creates a fixture for the body with custom data.
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
