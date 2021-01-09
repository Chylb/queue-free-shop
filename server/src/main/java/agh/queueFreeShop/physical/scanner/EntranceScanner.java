package agh.queueFreeShop.physical.scanner;

import agh.queueFreeShop.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EntranceScanner {
    private final Logger logger = LoggerFactory.getLogger(EntranceScanner.class);

    private final ShopService shopService;

    public EntranceScanner(ShopService shopService){
        this.shopService = shopService;
    }

    public synchronized void scan(Long id){
        logger.info("Scanned customer " + id);
        shopService.onScannedEnteringCustomer(id);
    }
}
