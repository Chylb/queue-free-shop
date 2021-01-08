package agh.queueFreeShop.physical.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gate {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public synchronized void open() {
        logger.info("Gate opened");
    }
}
