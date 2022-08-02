package com.tasty.masiottae.menu.service;

import com.tasty.masiottae.common.util.AwsS3Service;
import com.tasty.masiottae.menu.MenuConverter;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.repository.MenuRepository;

import java.util.Objects;
import javax.persistence.EntityNotFoundException;

import com.tasty.masiottae.menu.repository.MenuTasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final AwsS3Service s3Service;
    private final MenuConverter menuConverter;
    private final MenuTasteRepository menuTasteRepository;

    @Transactional
    public MenuSaveResponse createMenu(MenuSaveUpdateRequest request, MultipartFile image) {
        String menuImageUrl = getImageUrl(null, image);

        Menu menu = menuConverter.toMenu(request, menuImageUrl);
        menuRepository.save(menu);

        return menuConverter
                .toMenuSaveResponse(menuRepository.save(menu));
    }

    private String getImageUrl(String imageUrl, MultipartFile image) {
        String menuImageUrl = imageUrl;

        if (Objects.nonNull(image)) {
            menuImageUrl = s3Service.uploadMenuImage(image);
        }
        return menuImageUrl;
    }

    public MenuFindResponse findOneMenu(Long menuId) {
        Menu findMenu = findByFetchEntity(menuId);
        return menuConverter.toMenuFindResponse(findMenu);
    }

    @Transactional
    public void updateMenu(Long menuId, MenuSaveUpdateRequest request, MultipartFile image) {
        Menu originMenu = findEntity(menuId);
        String menuImageUrl = getImageUrl(originMenu.getPictureUrl(), image);
        Menu menu = menuConverter.toMenu(request, menuImageUrl);
        menu.setId(menuId);
        menuTasteRepository.deleteAll(menu.getMenuTasteSet());
        originMenu.update(menu);
    }

    public Menu findByFetchEntity(Long menuId) {
        return menuRepository.findByIdFetch(menuId).orElseThrow(EntityNotFoundException::new);
    }

    public Menu findEntity(Long menuId) {
        return menuRepository.findById(menuId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void delete(Long id) {
        menuRepository.deleteById(id);
    }

}
