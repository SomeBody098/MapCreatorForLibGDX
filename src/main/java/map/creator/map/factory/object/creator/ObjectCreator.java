package map.creator.map.factory.object.creator;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Shape2D;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.body.BodyFactory;
import map.creator.map.factory.body.param.FormBody;

import java.util.Map;

/**
 * Used to create entities with the necessary logic.
 */
public interface ObjectCreator {

    /**
     * Used for creating a special entity.
     * <b>WARNING:</b> Do not load textures, sounds, or other source require OpenGL calls (learn more - https://libgdx.com/wiki/app/threading).
     * It is better to download yourself in advance or via {@link com.badlogic.gdx.assets.AssetManager} and use DI (Dependency injection).
     * <p><b>Example:</b></p>
     * <pre>
     * {@code
     *     public class PlayerCreator {
     *         private Texture someTexture;
     *
     *         public PlayerCreator(Texture someTexture){
     *             this.someTexture = someTexture;
     *         }
     *
     *         public ObjectEntity createObject(...) {
     *              // Further use in createObject method...
     *         }
     *     }
     * }
     * </pre>
     *
     * @param nameBody the name of current object
     * @param properties properties of the object
     * @param dataObjects all "data" objects (P.S: These are objects that will not be created on the map - they are only needed as a designation, for example: the spawn points of goblins from the barracks).
     * @param bodyFactory One-stop factory for creating physical bodies and Box2D figures. {@link BodyFactory}
     * @param formBody Sets form body for object. {@link FormBody}
     * @param boundsObject bound of current object
     * @return special {@link ObjectEntity}
     */
    ObjectEntity createObject(
        String nameBody,
        MapProperties properties,
        Map<String, MapProperties> dataObjects,
        BodyFactory bodyFactory,
        FormBody formBody,
        Shape2D boundsObject
    );

}
