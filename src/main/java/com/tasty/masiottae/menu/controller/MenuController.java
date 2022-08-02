package com.tasty.masiottae.menu.controller;


import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuSaveResponse> saveMenu(@RequestPart MenuSaveUpdateRequest data,
            @RequestPart MultipartFile image) {
        return new ResponseEntity<>(menuService.createMenu(data, image), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{menuId}")
    public ResponseEntity<MenuFindResponse> getOneMenu(@PathVariable Long menuId) {
        MenuFindResponse menu = menuService.findOneMenu(menuId);
        return ResponseEntity.ok().body(menu);
    }

    @PostMapping(value = "/{menuId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMenu(@RequestPart MenuSaveUpdateRequest data, @RequestPart MultipartFile image, @PathVariable Long menuId) {
        menuService.updateMenu(menuId, data, image);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.delete(menuId);
        return ResponseEntity.ok().build();
    }
}
