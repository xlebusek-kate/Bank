package org.skypro.Controller;

import org.skypro.Repository.BankRepository;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final BankRepository bankRepository;
    private final BuildProperties buildProperties;

    public ManagementController(BankRepository bankRepository, BuildProperties buildProperties) {
        this.bankRepository = bankRepository;
        this.buildProperties = buildProperties;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        bankRepository.clearAllCaches();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion()
        ));
    }
}