package com.tasty.masiottae.menu.controller;


import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuSaveResponse> saveMenu(@RequestPart MenuSaveRequest data,
            @RequestPart MultipartFile imageFile) {
        return new ResponseEntity<>(menuService.createMenu(data, imageFile), HttpStatus.CREATED);
    }
    
    @GetMapping("/{menuId}")
    public ResponseEntity<MenuFindResponse> getOneMenu(@PathVariable Long menuId) {
        MenuFindResponse menu = menuService.findOneMenu(menuId);
        return ResponseEntity.ok().body(menu);
    }
}
