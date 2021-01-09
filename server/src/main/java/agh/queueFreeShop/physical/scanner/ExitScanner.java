package agh.queueFreeShop.physical.scanner;

import agh.queueFreeShop.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Dummy physical exit scanner. Sends scanned user id to {@link ShopService#onScannedLeavingCustomer(Long)}.
 */

@Component
public class ExitScanner {
    private final Logger logger = LoggerFactory.getLogger(ExitScanner.class);

    private final ShopService shopService;

    public ExitScanner(ShopService shopService){
        this.shopService = shopService;
    }

    /**
     * Called when scanner <i>scans</i> the customer.
     */
    public synchronized void scan(Long id){
        logger.info("Scanned customer " + id);
        shopService.onScannedLeavingCustomer(id);
    }
}
