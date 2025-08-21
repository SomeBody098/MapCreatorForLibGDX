package map.creator.map.component.data;

import map.creator.map.component.ObjectComponent;

/// Used to clear the dataComponents in class {@link map.creator.map.system.MapContactListener}
/// @see map.creator.map.system.MapContactListener
public class CleanComponent extends ObjectComponent {

    /**
     * if this value is true, then entity, which stores this component, will be deleting {@link map.creator.map.system.MapContactListener}.
     */
    public boolean isMustBeDelete = false;

    public CleanComponent() {
        super("clean-data", "data", null);
    }

}
