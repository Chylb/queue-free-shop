package agh.queueFreeShop.physical.weight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Weight {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int weight = 0;

    public synchronized int readWeight() {
        logger.info("Read weight " + weight);
        return weight;
    }

    public synchronized void updateReading(int weight) {
        this.weight = weight;
    }
}
