package com.tasty.masiottae.menu.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_MENU;
import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_NULL_FRANCHISE_ID;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.service.AccountEntityService;
import com.tasty.masiottae.common.aws.AwsS3ImageUploader;
import com.tasty.masiottae.common.exception.ErrorMessage;
import com.tasty.masiottae.common.exception.custom.ForbiddenException;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.service.FranchiseService;
import com.tasty.masiottae.menu.MenuConverter;
import com.tasty.masiottae.menu.MenuSearchList;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindOneResponse;
import com.tasty.masiottae.menu.dto.MenuSaveRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuUpdateRequest;
import com.tasty.masiottae.menu.dto.SearchCond;
import com.tasty.masiottae.menu.dto.SearchMenuRequest;
import com.tasty.masiottae.menu.dto.SearchMenuResponse;
import com.tasty.masiottae.menu.enums.MenuSortCond;
import com.tasty.masiottae.menu.enums.SearchType;
import com.tasty.masiottae.menu.repository.MenuRepository;
import com.tasty.masiottae.menu.repository.MenuTasteRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final AwsS3ImageUploader s3ImageUploader;
    private final MenuConverter menuConverter;
    private final MenuTasteRepository menuTasteRepository;
    private final TasteService tasteService;
    private final AccountEntityService accountEntityService;
    private final FranchiseService franchiseService;

    @Transactional
    public MenuSaveResponse createMenu(Account account, MenuSaveRequest request,
            MultipartFile image) {
        String menuImageUrl = getImageUrl(null, image, false);
        Menu menu = menuConverter.toMenu(account, request, menuImageUrl);
        return menuConverter
                .toMenuSaveResponse(menuRepository.save(menu));
    }

    private String getImageUrl(String imageUrl, MultipartFile image, boolean isChange) {
        String menuImageUrl = imageUrl;

        if (Objects.nonNull(image)) {
            menuImageUrl = s3ImageUploader.uploadMenuImage(image);
        } else if (isChange) {
            menuImageUrl = null;
        }
        return menuImageUrl;
    }

    public MenuFindOneResponse findOneMenu(Long menuId, Account account) {

        Menu findMenu = menuRepository.findByIdFetch(menuId).orElseThrow(
                () -> new EntityNotFoundException(NOT_FOUND_MENU.getMessage())
        );

        return menuConverter.toMenuFindOneResponse(findMenu, account);
    }

    @Transactional
    public void updateMenu(Long menuId, MenuUpdateRequest request, MultipartFile image,
            Account account) {
        Menu originMenu = findEntity(menuId);

        if (!originMenu.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException(ErrorMessage.NOT_ACCESS_ANOTHER_ACCOUNT.getMessage());
        }

        String menuImageUrl = getImageUrl(originMenu.getPictureUrl(), image,
                request.isRemoveImage());
        Menu menu = menuConverter.toMenu(menuId, request, account, menuImageUrl);
        menuTasteRepository.deleteAll(menu.getMenuTasteList());

        Set<MenuTaste> menuTasteSet = request.tasteIdList().stream().map(tasteId -> {
            Taste taste = Taste.createTaste(tasteId);
            return MenuTaste.createMenuTaste(menu, taste);
        }).collect(Collectors.toSet());
        menuTasteRepository.saveAll(menuTasteSet);

        originMenu.update(menu, menuTasteSet);
    }

    public Menu findByFetchEntity(Long menuId) {
        return menuRepository.findByIdFetch(menuId).orElseThrow(EntityNotFoundException::new);
    }

    public Menu findEntity(Long menuId) {
        return menuRepository.findById(menuId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void delete(Account account, Long id) {
        Menu menu = findEntity(id);
        boolean isUseAccount = menu.getAccount().getId().equals(account.getId());
        if (!isUseAccount) {
            throw new ForbiddenException(ErrorMessage.NOT_ACCESS_ANOTHER_ACCOUNT.getMessage());
        }
        menuRepository.deleteById(id);
    }

    public SearchMenuResponse searchAllMenu(SearchMenuRequest request) {
        validateFranchiseIdIsNotNull(request.franchiseId());
        Franchise franchise =
                request.franchiseId() == 0 ? null : franchiseService.findOneFranchiseEntity(
                        request.franchiseId());
        List<Taste> findTasteByIds = tasteService.findTasteByIds(request.tasteIdList());
        MenuSortCond sortCond = MenuSortCond.find(request.sort());
        SearchCond searchCond = new SearchCond(SearchType.ALL_MENU, null, request.keyword(), sortCond, franchise,
                findTasteByIds);
        return buildSearchMenuResponse(request, searchCond);
    }

    public SearchMenuResponse searchMyMenu(Account account, SearchMenuRequest request) {
        List<Taste> findTasteByIds = tasteService.findTasteByIds(request.tasteIdList());
        MenuSortCond sortCond = MenuSortCond.find(request.sort());
        SearchCond searchCond = new SearchCond(SearchType.MY_MENU, account, request.keyword(), sortCond, null,
                findTasteByIds);
        return buildSearchMenuResponse(request, searchCond);
    }

    private SearchMenuResponse buildSearchMenuResponse(SearchMenuRequest request,
            SearchCond searchCond) {
        try {
            List<Menu> menus = MenuSearchList.of(menuRepository.search(searchCond))
                    .filterByTaste(searchCond.tastes())
                    .paging(request.offset(), request.limit())
                    .getMenus();

            return new SearchMenuResponse(menuConverter.toMenuFindResponseList(menus));
        } catch (IllegalArgumentException e) {
            return new SearchMenuResponse(null);
        }
    }

    private void validateFranchiseIdIsNotNull(Long franchiseId) {
        if (Objects.isNull(franchiseId)) {
            throw new IllegalArgumentException(NOT_NULL_FRANCHISE_ID.getMessage());
        }
    }

}
