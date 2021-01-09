package agh.queueFreeShop.physical.scanner;

import agh.queueFreeShop.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Dummy physical entrance scanner. Sends scanned user id to {@link ShopService#onScannedEnteringCustomer(Long)}.
 */

@Component
public class EntranceScanner {
    private final Logger logger = LoggerFactory.getLogger(EntranceScanner.class);

    private final ShopService shopService;

    public EntranceScanner(ShopService shopService){
        this.shopService = shopService;
    }

    /**
     * Called when scanner <i>scans</i> the customer.
     */
    public synchronized void scan(Long id){
        logger.info("Scanned customer " + id);
        shopService.onScannedEnteringCustomer(id);
    }
}
