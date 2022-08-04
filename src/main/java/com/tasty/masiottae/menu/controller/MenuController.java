package com.tasty.masiottae.menu.controller;


import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.dto.SearchMyMenuRequest;
import com.tasty.masiottae.menu.dto.SearchMyMenuResponse;
import com.tasty.masiottae.menu.service.MenuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping(value = "/menu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuSaveResponse> saveMenu(@RequestPart MenuSaveUpdateRequest data,
            @RequestPart MultipartFile image) {
        return new ResponseEntity<>(menuService.createMenu(data, image), HttpStatus.CREATED);
    }

    @GetMapping(value = "/menu/{menuId}")
    public ResponseEntity<MenuFindResponse> getOneMenu(@PathVariable Long menuId) {
        MenuFindResponse menu = menuService.findOneMenu(menuId);
        return ResponseEntity.ok().body(menu);
    }

    @PostMapping(value = "/menu/{menuId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMenu(@RequestPart MenuSaveUpdateRequest data,
            @RequestPart MultipartFile image, @PathVariable Long menuId) {
        menuService.updateMenu(menuId, data, image);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/menu/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.delete(menuId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MenuFindResponse>> findAllMenu() {
        return ResponseEntity.ok(menuService.findAllMenu());
    }

    @GetMapping(value = "/accounts/{accountId}/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchMyMenuResponse> searchMyMenu(
            @ModelAttribute @Validated SearchMyMenuRequest request, @PathVariable Long accountId) {
        return ResponseEntity.ok(menuService.searchMyMenu(accountId, request));
    }
}
