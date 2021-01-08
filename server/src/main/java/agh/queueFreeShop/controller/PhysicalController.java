package agh.queueFreeShop.controller;

import agh.queueFreeShop.physical.scanner.EntranceScanner;
import agh.queueFreeShop.physical.scanner.ExitScanner;
import agh.queueFreeShop.physical.weight.EntranceWeight;
import agh.queueFreeShop.physical.weight.ExitWeight;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/physical")
@PreAuthorize("hasRole('ROLE_PHYSICAL_INFRASTRUCTURE')")
public class PhysicalController {
    private final EntranceWeight entranceWeight;
    private final ExitWeight exitWeight;
    private final EntranceScanner entranceScanner;
    private final ExitScanner exitScanner;

    PhysicalController(EntranceWeight entranceWeight, ExitWeight exitWeight, EntranceScanner entranceScanner, ExitScanner exitScanner) {
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
