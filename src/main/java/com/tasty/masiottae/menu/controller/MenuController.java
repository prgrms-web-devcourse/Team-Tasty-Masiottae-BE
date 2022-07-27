package com.tasty.masiottae.menu.controller;

import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuFindResponse> getOneMenu(@PathVariable Long menuId) {
        MenuFindResponse menu = menuService.findOneMenu(menuId);
        return ResponseEntity.ok().body(menu);
    }
}
