package org.skypro.Controller;

import org.skypro.FullRecommendationsDTO;
import org.skypro.Service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/recommendation/{user_id}")
    public ResponseEntity<FullRecommendationsDTO> listRecommendation (@PathVariable(name = "user_id") UUID uuid){
        if (uuid == null ) return ResponseEntity.badRequest().build();
        FullRecommendationsDTO recommendationsDTO = new FullRecommendationsDTO(String.valueOf(uuid), bankService.getRecommendationsByUUID(uuid));
        return ResponseEntity.ok(recommendationsDTO);
    }
}
