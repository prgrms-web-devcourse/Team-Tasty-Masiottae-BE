package com.tasty.masiottae.likemenu.service;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.likemenu.domain.LikeMenu;
import com.tasty.masiottae.likemenu.domain.LikeMenuId;
import com.tasty.masiottae.likemenu.repository.LikeMenuRepository;
import com.tasty.masiottae.menu.MenuConverter;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LikeMenuService {

    private final LikeMenuRepository likeMenuRepository;
    private final MenuRepository menuRepository;
    private final MenuConverter menuConverter;

    private void addLike(Account account, Menu menu) {
        likeMenuRepository.save(new LikeMenu(account, menu));
        menu.addLike();
    }

    private void cancelLike(Account account, Menu menu) {
        LikeMenuId likeMenuId = new LikeMenuId(account.getId(), menu.getId());
        LikeMenu likeMenu = likeMenuRepository.findById(likeMenuId).orElseThrow();
        likeMenu.getMenu().removeLike();
        likeMenuRepository.delete(likeMenu);
    }

    @Transactional
    public void changeLike(Account account, Long menuId) {
        Menu menu = menuRepository.findByIdForUpdate(menuId).orElseThrow();
        boolean isLike = likeMenuRepository.existsByAccountAndMenu(account, menu);
        if (isLike) {
            cancelLike(account, menu);
        } else {
            addLike(account, menu);
        }
    }

    public Page<MenuFindResponse> getPageLikeMenuByAccount(Account account, Pageable pageable) {

       return likeMenuRepository.findEntityGraphNByAccount(account, pageable)
                .map(LikeMenu::getMenu)
                .map(menuConverter::toMenuFindResponse);

    }


}
