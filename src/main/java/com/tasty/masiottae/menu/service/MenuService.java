package com.tasty.masiottae.menu.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_MENU;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.service.AccountEntityService;
import com.tasty.masiottae.common.aws.AwsS3ImageUploader;
import com.tasty.masiottae.common.util.PageInfo;
import com.tasty.masiottae.common.util.PageUtil;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.service.FranchiseService;
import com.tasty.masiottae.menu.MenuConverter;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.dto.SearchCond;
import com.tasty.masiottae.menu.dto.SearchMenuRequest;
import com.tasty.masiottae.menu.dto.SearchMenuResponse;
import com.tasty.masiottae.menu.dto.SearchMyMenuRequest;
import com.tasty.masiottae.menu.enums.MenuSortCond;
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
            menuImageUrl = s3ImageUploader.uploadMenuImage(image);
        }
        return menuImageUrl;
    }

    public MenuFindResponse findOneMenu(Long menuId) {

        Menu findMenu = menuRepository.findByIdFetch(menuId).orElseThrow(
            () -> new EntityNotFoundException(NOT_FOUND_MENU.getMessage())
        );

        return menuConverter.toMenuFindResponse(findMenu);
    }

    @Transactional
    public void updateMenu(Long menuId, MenuSaveUpdateRequest request, MultipartFile image) {
        Menu originMenu = findEntity(menuId);
        String menuImageUrl = getImageUrl(originMenu.getPictureUrl(), image);
        Menu menu = menuConverter.toMenu(request, menuImageUrl);
        menu.setId(menuId);
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
    public void delete(Long id) {
        menuRepository.deleteById(id);
    }

    public SearchMenuResponse searchAllMenu(SearchMenuRequest request) {
        Franchise franchise =
                request.franchiseId() == 0 ? null : franchiseService.findOneFranchiseEntity(
                        request.franchiseId());
        List<Taste> findTasteByIds = tasteService.findTasteByIds(request.tasteIdList());
        MenuSortCond sortCond = MenuSortCond.find(request.sort());
        SearchCond searchCond = new SearchCond(null, request.keyword(), sortCond, franchise, findTasteByIds);
        return searchMenu(searchCond, new PageInfo(request.offset(), request.limit()));
    }

    public SearchMenuResponse searchMyMenu(Account account, SearchMyMenuRequest request) {
        List<Taste> findTasteByIds = tasteService.findTasteByIds(request.tasteIdList());
        MenuSortCond sortCond = MenuSortCond.find(request.sort());
        SearchCond searchCond = new SearchCond(account, request.keyword(), sortCond, null, findTasteByIds);
        return searchMenu(searchCond, new PageInfo(request.offset(), request.limit()));
    }

    private SearchMenuResponse searchMenu(SearchCond searchCond, PageInfo pageInfo) {
        List<Menu> menus = menuRepository.search(searchCond);

        if (isNotEmptyTastes(searchCond.tastes())) {
            menus =  menus.stream()
                    .filter(menu -> menu.getMenuTasteList().stream()
                            .map(MenuTaste::getTaste).collect(Collectors.toSet())
                            .containsAll(searchCond.tastes())).toList();
        }

        try {
            return new SearchMenuResponse(PageUtil.page(pageInfo, menus).stream()
                    .map(menuConverter::toMenuFindResponse)
                    .toList());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean isNotEmptyTastes(List<Taste> findTasteByIds) {
        return !findTasteByIds.isEmpty();
    }


}
