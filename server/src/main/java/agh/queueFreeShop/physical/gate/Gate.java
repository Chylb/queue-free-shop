package agh.queueFreeShop.physical.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy physical gate that logs being opened.
 */

public class Gate {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <i>Opens</i> the gate.
     */
    public synchronized void open() {
        logger.info("Gate opened");
    }
}
