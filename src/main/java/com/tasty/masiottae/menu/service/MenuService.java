package com.tasty.masiottae.menu.service;

import com.tasty.masiottae.common.util.AwsS3Service;
import com.tasty.masiottae.menu.MenuConverter;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.repository.MenuRepository;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
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

    @Transactional
    public MenuSaveResponse createMenu(MenuSaveRequest request, MultipartFile imageFile) {
        String menuImageUrl = null;

        if (Objects.nonNull(imageFile)) {
            menuImageUrl = s3Service.uploadMenuImage(imageFile);
        }

        Menu menu = menuConverter.toMenu(request, menuImageUrl);
        menuRepository.save(menu);

        return menuConverter
                .toMenuSaveResponse(menuRepository.save(menu));
    }

    public MenuFindResponse findOneMenu(Long menuId) {
        Menu findMenu = menuRepository.findByIdFetch(menuId).orElseThrow(
                () -> new EntityNotFoundException()
        );
        return menuConverter.toMenuFindResponse(findMenu);
    }
}
