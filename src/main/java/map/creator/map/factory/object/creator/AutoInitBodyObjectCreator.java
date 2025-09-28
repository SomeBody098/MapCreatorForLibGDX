package map.creator.map.factory.object.creator;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.physics.box2d.Body;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.body.BodyFactory;
import map.creator.map.factory.body.param.BodyParam;
import map.creator.map.factory.body.param.BodyParamFactory;
import map.creator.map.factory.body.param.FormBody;

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
 *   "fixtureDef": {
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

    private final BodyParamFactory bodyParamFactory;

    public AutoInitBodyObjectCreator(BodyParamFactory bodyParamFactory) {
        this.bodyParamFactory = bodyParamFactory;
    }

    public abstract ObjectEntity createObject(String nameBody, MapProperties properties, Map<String, MapProperties> dataObjects, float unitScale, Body body);

    /**
     * Created body for object auto.
     * @param nameBody the name of current object
     * @param properties properties of the object
     * @param dataObjects all "data" objects (P.S: These are objects that will not be created on the map - they are only needed as a designation, for example: the spawn points of goblins from the barracks).
     * @param bodyFactory One-stop factory for creating physical bodies and Box2D figures. {@link BodyFactory}
     * @param formBody Sets form body for object. {@link FormBody}
     * @param boundsObject bound of current object
     * @return Once a body is created, the user defines its logic/behavior/structure and then returned modified of {@link ObjectEntity}
     */
    @Override
    public ObjectEntity createObject(String nameBody, MapProperties properties, Map<String, MapProperties> dataObjects, BodyFactory bodyFactory, FormBody formBody, Shape2D boundsObject) {
        BodyParam bodyParam = bodyParamFactory.createBodyParam(properties, formBody, boundsObject);
        Body body = bodyFactory.createBody(bodyParam);

        return createObject(nameBody, properties, dataObjects, bodyFactory.getUnitScale(), body);
    }

}
