package map.creator.map.factory.body;

/// Sets form body for object.
public enum FormBody {

    /// Form rectangle/cube - full inside.
    RECTANGLE,

    /// Form circle - full inside.
    CIRCLE,

    /// Form ellipse - full inside.
    /// (P.S: but this shape is essentially are POLYGON (since Box2D don't have ellipse shape) however it vertices calculated auto in BodyFactory, learn more in this class).
    /// @see BodyFactory
    ELLIPSE,

    /// Form polygon - full inside.
    /// POLYGON - is a shape with N count vertices, however because of restrictions Box2D engine (3 - 8 vertices in ONE polygon)
    /// if polygon has been have more 8 vertices that it will be divided on triangles using EarClippingTriangulator. Learn more in BodyFactory class
    /// @see BodyFactory
    POLYGON,

    /// Form chain - hollow inside, if vertices touched each other (first with last).
    /// CHAIN - is copula N of the number of vertices.
    CHAIN,

    /// Form edge - it just line with 2 vertices.
    EDGE;

    /// getter shape in string
    /// @param form name shape
    /// @exception IllegalArgumentException if was not a single coincidence
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
