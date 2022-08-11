package com.tasty.masiottae.franchise.controller;

import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.franchise.dto.FranchiseSaveRequest;
import com.tasty.masiottae.franchise.dto.FranchiseSaveResponse;
import com.tasty.masiottae.franchise.service.FranchiseService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;

    @GetMapping
    public ResponseEntity<List<FranchiseFindResponse>> getAllFranchise() {
        return ResponseEntity.ok().body(franchiseService.findAllFranchise());
    }

    @PostMapping
    public ResponseEntity<FranchiseSaveResponse> saveFranchise(
            @Valid @ModelAttribute FranchiseSaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(franchiseService.createFranchise(request));
    }
}
