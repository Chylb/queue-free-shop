package agh.queueFreeShop.controller;

import agh.queueFreeShop.physical.EntranceWeight;
import agh.queueFreeShop.physical.ExitWeight;
import agh.queueFreeShop.service.ShopService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/physical")
public class PhysicalController {
    private ShopService shopService;
    private EntranceWeight entranceWeight;
    private ExitWeight exitWeight;

    PhysicalController(ShopService shopService, EntranceWeight entranceWeight, ExitWeight exitWeight) {
        this.shopService = shopService;
        this.entranceWeight = entranceWeight;
        this.exitWeight = exitWeight;
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
        shopService.onScannedEnteringCustomer(userId);
    }

    @PostMapping("/scanner/exit")
    public void sendScannedLeavingCustomer(@RequestParam Long userId) {
        shopService.onScannedLeavingCustomer(userId);
    }
}
