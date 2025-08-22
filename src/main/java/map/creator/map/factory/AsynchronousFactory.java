package map.creator.map.factory;

/**
 * Class for factories supporting asynchronous loading.
 */
public interface AsynchronousFactory {

    /**
     * Checks whether the factory graduated or not.
     * @return false when still works - else true.
     */
    boolean isDone();

    /**
     * Getting the progress loading.
     * @return progress loading.
     */
    float getProgress();

}
