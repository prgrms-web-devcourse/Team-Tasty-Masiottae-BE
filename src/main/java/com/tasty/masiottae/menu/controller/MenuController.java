package com.tasty.masiottae.menu.controller;


import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.menu.dto.*;
import com.tasty.masiottae.menu.service.MenuService;
import com.tasty.masiottae.security.annotation.LoginAccount;
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
    public ResponseEntity<MenuSaveResponse> saveMenu(@RequestPart MenuSaveRequest data,
            @RequestPart(required = false) MultipartFile image, @LoginAccount Account account) {
        return new ResponseEntity<>(menuService.createMenu(account, data, image),
                HttpStatus.CREATED);
    }

    @GetMapping(value = "/menu/{menuId}")
    public ResponseEntity<MenuFindOneResponse> getOneMenu(@PathVariable Long menuId, @LoginAccount Account account) {
        MenuFindOneResponse menu = menuService.findOneMenu(menuId, account);
        return ResponseEntity.ok().body(menu);
    }

    @PostMapping(value = "/menu/{menuId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMenu(@RequestPart MenuUpdateRequest data,
            @RequestPart(required = false) MultipartFile image,
            @PathVariable Long menuId, @LoginAccount Account account) {
        menuService.updateMenu(menuId, data, image, account);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/menu/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId,
            @LoginAccount Account account) {
        menuService.delete(account, menuId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/my-menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchMenuResponse> searchMyMenu(
            @ModelAttribute @Validated SearchMyMenuRequest request, @LoginAccount Account account) {
        return ResponseEntity.ok(menuService.searchMyMenu(account, request));
    }

    @GetMapping(value = "/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchMenuResponse> searchMenu(
            @ModelAttribute @Validated SearchMenuRequest request) {
        return ResponseEntity.ok(menuService.searchAllMenu(request));
    }
}
