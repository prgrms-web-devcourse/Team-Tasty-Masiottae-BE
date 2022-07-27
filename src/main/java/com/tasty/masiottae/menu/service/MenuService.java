package com.tasty.masiottae.menu.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_MENU_ERR;

import com.tasty.masiottae.menu.MenuConverter;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.repository.MenuRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuConverter menuConverter;

    public MenuFindResponse findOneMenu(Long menuId) {
        Menu findMenu = menuRepository.findByIdFetch(menuId).orElseThrow(
                () -> new EntityNotFoundException(NOT_FOUND_MENU_ERR.getMessage())
        );
        return menuConverter.toMenuFindResponse(findMenu);
    }
}
