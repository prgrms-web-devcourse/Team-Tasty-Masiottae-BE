package com.tasty.masiottae.likemenu.controller;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.likemenu.service.LikeMenuService;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.security.annotation.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeMenuController {

    private final LikeMenuService likeMenuService;

    @PostMapping(value = "/menu/{menuId}/like", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeLike(@PathVariable Long menuId, @LoginAccount Account account) {
        likeMenuService.changeLike(account, menuId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/accounts/like")
    public ResponseEntity<Page<MenuFindResponse>> getMenuByAccountLike(@LoginAccount Account account, @PageableDefault
            Pageable pageable) {
        Page<MenuFindResponse> menuByAccountLikeList = likeMenuService.getPageLikeMenuByAccount(account, pageable);
        return ResponseEntity.ok(menuByAccountLikeList);
    }

}
