package agh.queueFreeShop.physical.weight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy physical weight.
 */

public class Weight {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int weight = 0;

    /**
     * Reads weight.
     * Called when {@link agh.queueFreeShop.service.ShopService} wants to know the customer weight.
     */
    public synchronized int readWeight() {
        logger.info("Read weight " + weight);
        return weight;
    }

    /**
     * Sets weight.
     */
    public synchronized void updateReading(int weight) {
        this.weight = weight;
    }
}
