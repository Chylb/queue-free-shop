package agh.queueFreeShop.physical;

import agh.queueFreeShop.physical.scanner.EntranceScanner;
import agh.queueFreeShop.physical.scanner.ExitScanner;
import agh.queueFreeShop.service.ShopService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Unit test of scanners.
 */

public class ScannerTest {

    @Test
    public void entranceScanner_should_inform_shopService() {
        ShopService shopService = mock(ShopService.class);
        EntranceScanner entranceScanner = new EntranceScanner(shopService);
        entranceScanner.scan(0L);
        verify(shopService, times(1)).onScannedEnteringCustomer(0L);
    }

    @Test
    public void exitScanner_should_inform_shopService() {
        ShopService shopService = mock(ShopService.class);
        ExitScanner exitScanner = new ExitScanner(shopService);
        exitScanner.scan(0L);
        verify(shopService, times(1)).onScannedLeavingCustomer(0L);
    }
}
