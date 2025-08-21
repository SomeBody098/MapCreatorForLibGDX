package map.creator.map.component;

import com.badlogic.ashley.core.Component;
import map.creator.map.GameObject;

/** Base parent of all components
 */
public abstract class ObjectComponent implements GameObject, Component {

    /** Unique name the component, mustn't be null.
     */
    protected final String name;

    /** The type the component, mustn't be null.
     */
    protected final String type;

    /** The name of entity that contains this component
      */
    protected final String owner;

    public ObjectComponent(String name, String type, String owner) {
        if (name == null) throw new IllegalArgumentException("Name and owner cannot be null");
        this.name = name;
        this.type = type;
        this.owner = owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    public String getOwner() {
        return owner;
    }
}
