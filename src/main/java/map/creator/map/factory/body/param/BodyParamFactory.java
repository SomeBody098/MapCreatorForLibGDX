package map.creator.map.factory.body.param;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Shape2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Special factory for automatic parameter generation for the body.
 */
public class BodyParamFactory implements BodyParamParser{

    private final List<BodyParamParser> parsers;

    public BodyParamFactory() {
        parsers = new ArrayList<>();
        initParsers("body-param");
    }

    public BodyParamFactory(String nameBodyParamJson) {
        parsers = new ArrayList<>();
        initParsers(nameBodyParamJson);
    }

    private void initParsers(String nameBodyParamJson) {
        BodyParamFactoryElements elements = new BodyParamFactoryElements();
        parsers.add(new BodyParamParserByJson(elements, nameBodyParamJson));
        parsers.add(new BodyParamParserByProperties(elements));
    }

    @Override
    public BodyParam createBodyParam(MapProperties properties, FormBody formBody, Shape2D boundsObject) {
        for (BodyParamParser parser : parsers) {
            BodyParam result = parser.createBodyParam(properties, formBody, boundsObject);
            if (result != null) return result;
        }

        throw new RuntimeException("No BodyParam parser found");
    }
}
