package agh.queueFreeShop.controller;

import agh.queueFreeShop.physical.scanner.EntranceScanner;
import agh.queueFreeShop.physical.scanner.ExitScanner;
import agh.queueFreeShop.physical.weight.EntranceWeight;
import agh.queueFreeShop.physical.weight.ExitWeight;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controls dummy physical infrastructure.
 * Only PHYSICAL_INFRASTRUCTURE user can use it.
 */

@RestController
@RequestMapping(path = "/physical")
@PreAuthorize("hasRole('ROLE_PHYSICAL_INFRASTRUCTURE')")
@ApiResponses(@ApiResponse(code = 401, message = "Unauthorized"))
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
    @ApiOperation(value = "Read entrance weight value")
    public int readEntranceWeight() {
        return entranceWeight.readWeight();
    }

    @PostMapping("/weight/entrance")
    @ApiOperation(value = "Update entrance weight value")
    public int updateEntranceWeight(@RequestParam int weight) {
        entranceWeight.updateReading(weight);
        return entranceWeight.readWeight();
    }

    @GetMapping("/weight/exit")
    @ApiOperation(value = "Read exit weight value")
    public int readExitWeight() {
        return exitWeight.readWeight();
    }

    @PostMapping("/weight/exit")
    @ApiOperation(value = "Update exit weight value")
    public int updateExitWeight(@RequestParam int weight) {
        exitWeight.updateReading(weight);
        return exitWeight.readWeight();
    }

    @PostMapping("/scanner/entrance")
    @ApiOperation(value = "Scan entering customer", notes = "This request makes entrance scanner \"scan\" given user.")
    public void sendScannedEnteringCustomer(@RequestParam Long userId) {
        entranceScanner.scan(userId);
    }

    @PostMapping("/scanner/exit")
    @ApiOperation(value = "Scan leaving customer", notes = "This request makes exit scanner \"scan\" given user.")
    public void sendScannedLeavingCustomer(@RequestParam Long userId) {
        exitScanner.scan(userId);
    }
}
