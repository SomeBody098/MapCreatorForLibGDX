package map.creator.map.factory.object;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Shape2D;
import map.creator.map.entity.ObjectEntity;
import map.creator.map.factory.body.BodyFactory;
import map.creator.map.factory.body.FormBody;

import java.util.Map;

public interface ObjectCreator {

    ObjectEntity createObject(
        String nameBody,
        MapProperties properties,
        Map<String, MapProperties> dataObjects,
        BodyFactory bodyFactory,
        FormBody formBody,
        Shape2D boundsObject
    );

}
