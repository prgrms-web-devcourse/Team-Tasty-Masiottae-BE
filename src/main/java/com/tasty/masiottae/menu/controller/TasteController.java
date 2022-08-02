package com.tasty.masiottae.menu.controller;

import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.menu.dto.TasteSaveRequest;
import com.tasty.masiottae.menu.dto.TasteSaveResponse;
import com.tasty.masiottae.menu.service.TasteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tastes")
@RequiredArgsConstructor
public class TasteController {

    private final TasteService tasteService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TasteFindResponse>> findAllTaste() {
        return ResponseEntity.ok(tasteService.findAllTaste());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TasteSaveResponse> createTaste(@RequestBody @Validated TasteSaveRequest request) {
        return new ResponseEntity<>(tasteService.createTaste(request), HttpStatus.CREATED);
    }
}
