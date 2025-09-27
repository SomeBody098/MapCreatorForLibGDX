package map.creator.map.factory.body.param;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Shape2D;

/**
 * Parser for creating {@link BodyParam}.
 */
public interface BodyParamParser {
    /**
     * Parsing JSON/MapProperties for create {@link BodyParam}
     * @param properties Set of string indexed values representing map elements... {@link MapProperties}
     * @param formBody Sets form body for object.
     * @param bounds Object body
     * @return {@link BodyParam}
     */
    BodyParam createBodyParam(MapProperties properties, FormBody formBody, Shape2D bounds);
}
