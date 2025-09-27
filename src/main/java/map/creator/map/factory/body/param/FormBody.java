package map.creator.map.factory.body.param;

import map.creator.map.factory.body.BodyFactory;

/**
 * Sets form body for object.
 */
public enum FormBody {

    /**
     * Form rectangle/cube - full inside.
     */
    RECTANGLE(0),

    /**
     * Form circle - full inside.
     */
    CIRCLE(1),

    /**
     * Form ellipse - full inside.
     * (P.S: but this shape is essentially are POLYGON (since Box2D don't have ellipse shape) however it vertices calculated auto in BodyFactory, learn more in this class).
     * @see BodyFactory
     */
    ELLIPSE(2),

    /**
     * Form polygon - full inside.
     * POLYGON - is a shape with N count vertices, however because of restrictions Box2D engine (3 - 8 vertices in ONE polygon)
     * if polygon has been have more 8 vertices that it will be divided on triangles using EarClippingTriangulator. Learn more in BodyFactory class
     * @see BodyFactory
     */
    POLYGON(3),

    /**
     * Form chain - hollow inside, if vertices touched each other (first with last).
     * CHAIN - is copula N of the number of vertices.
     */
    CHAIN(4),

    /**
     * Form edge - it just line with 2 vertices.
     */
    EDGE(5);

    private final int value;

    FormBody(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * getter shape in string
     * @param form name shape
     * @exception IllegalArgumentException if was not a single coincidence
     * @return Form of body by string
     */
    public static FormBody getFormBodyOnString(String form) {
        switch (form.toUpperCase()) {
            case "RECTANGLE":
                return RECTANGLE;
            case "CIRCLE":
                return CIRCLE;
            case "ELLIPSE":
                return ELLIPSE;
            case "POLYGON":
                return POLYGON;
            case "CHAIN":
                return CHAIN;
            case "EDGE":
                return EDGE;
            default:
                throw new IllegalArgumentException("Form " + form + "is not exist!");
        }
    }
}
