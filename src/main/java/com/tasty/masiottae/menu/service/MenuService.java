package com.tasty.masiottae.menu.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_MENU;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.service.AccountEntityService;
import com.tasty.masiottae.common.util.AwsS3Service;
import com.tasty.masiottae.menu.MenuConverter;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.dto.SearchCond;
import com.tasty.masiottae.menu.dto.SearchMyMenuRequest;
import com.tasty.masiottae.menu.dto.SearchMyMenuResponse;
import com.tasty.masiottae.menu.enums.MenuSortCond;
import com.tasty.masiottae.menu.repository.MenuRepository;
import com.tasty.masiottae.menu.repository.MenuTasteRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    private final AwsS3Service s3Service;
    private final MenuConverter menuConverter;
    private final MenuTasteRepository menuTasteRepository;
    private final TasteService tasteService;
    private final AccountEntityService accountEntityService;

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

    public List<MenuFindResponse> findAllMenu() {
        return menuRepository.findAllFetch().stream().map(menuConverter::toMenuFindResponse)
            .toList();
    }

    public SearchMyMenuResponse searchMyMenu(Long accountId, SearchMyMenuRequest request) {
        SearchCond searchCond = buildSearchCond(accountId, request);
        List<Menu> menus = getFilteredList(searchCond);
        return new SearchMyMenuResponse(
                getPagingList(request.offset(), request.limit(), menus));
    }

    private SearchCond buildSearchCond(Long accountId, SearchMyMenuRequest request) {
        Account account = accountEntityService.findById(accountId);
        List<Taste> findTasteByIds = tasteService.findTasteByIds(request.tasteIdList());
        MenuSortCond sortCond = MenuSortCond.find(request.sort());
        return new SearchCond(account, request.keyword(), sortCond, findTasteByIds);
    }

    private List<MenuFindResponse> getPagingList(int offset, int limit, List<Menu> menus) {
        if (isOveOffsetThanSize(offset, menus.size())) {
            return null;
        }

        int end = getEndIndex(offset, limit, menus);
        return menus.subList(offset, end).stream().map(menuConverter::toMenuFindResponse)
                .toList();
    }

    private List<Menu> getFilteredList(SearchCond searchCond) {
        List<Menu> menus = menuRepository.search(searchCond);

        if (isNotEmptyTastes(searchCond.tastes())) {
            return menus.stream()
                    .filter(menu -> menu.getMenuTasteSet().stream()
                            .map(MenuTaste::getTaste).collect(Collectors.toSet())
                            .equals(new HashSet<>(searchCond.tastes()))).toList();
        }

        return menus;
    }

    private static boolean isNotEmptyTastes(List<Taste> findTasteByIds) {
        return !findTasteByIds.isEmpty();
    }

    private boolean isOveOffsetThanSize(int offset, int size) {
        return offset >= size;
    }

    private int getEndIndex(int offset, int limit, List<Menu> menus) {
        return Math.min(offset + limit, menus.size());
    }
}
