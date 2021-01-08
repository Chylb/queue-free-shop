package agh.queueFreeShop.controller;

import agh.queueFreeShop.physical.scanner.EntranceScanner;
import agh.queueFreeShop.physical.scanner.ExitScanner;
import agh.queueFreeShop.physical.weight.EntranceWeight;
import agh.queueFreeShop.physical.weight.ExitWeight;
import agh.queueFreeShop.service.ShopService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/physical")
public class PhysicalController {
    private ShopService shopService;
    private EntranceWeight entranceWeight;
    private ExitWeight exitWeight;
    private EntranceScanner entranceScanner;
    private ExitScanner exitScanner;

    PhysicalController(ShopService shopService, EntranceWeight entranceWeight, ExitWeight exitWeight, EntranceScanner entranceScanner, ExitScanner exitScanner) {
        this.shopService = shopService;
        this.entranceWeight = entranceWeight;
        this.exitWeight = exitWeight;
        this.entranceScanner = entranceScanner;
        this.exitScanner = exitScanner;
    }

    @GetMapping("/weight/entrance")
    public int readEntranceWeight() {
        return entranceWeight.readWeight();
    }

    @PostMapping("/weight/entrance")
    public int updateEntranceWeight(@RequestParam int weight) {
        entranceWeight.updateReading(weight);
        return entranceWeight.readWeight();
    }

    @GetMapping("/weight/exit")
    public int readExitWeight() {
        return exitWeight.readWeight();
    }

    @PostMapping("/weight/exit")
    public int updateExitWeight(@RequestParam int weight) {
        exitWeight.updateReading(weight);
        return exitWeight.readWeight();
    }

    @PostMapping("/scanner/entrance")
    public void sendScannedEnteringCustomer(@RequestParam Long userId) {
        entranceScanner.scan(userId);
    }

    @PostMapping("/scanner/exit")
    public void sendScannedLeavingCustomer(@RequestParam Long userId) {
        exitScanner.scan(userId);
    }
}
