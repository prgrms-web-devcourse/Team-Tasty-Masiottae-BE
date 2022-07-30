package com.tasty.masiottae.franchise.controller;

import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.franchise.dto.FranchiseSaveRequest;
import com.tasty.masiottae.franchise.dto.FranchiseSaveResponse;
import com.tasty.masiottae.franchise.service.FranchiseService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;

    @GetMapping
    public List<FranchiseFindResponse> getAllFranchise() {
        return franchiseService.findAllFranchise();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FranchiseSaveResponse> saveFranchise(
            @Valid @RequestPart FranchiseSaveRequest franchiseSaveRequest,
            @RequestPart MultipartFile logoImg) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(franchiseService.createFranchise(franchiseSaveRequest, logoImg));
    }
}
