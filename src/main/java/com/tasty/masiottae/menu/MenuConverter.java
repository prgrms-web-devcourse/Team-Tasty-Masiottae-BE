package com.tasty.masiottae.menu;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindOneResponse;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuUpdateRequest;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.menu.service.TasteService;
import com.tasty.masiottae.option.OptionConverter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuConverter {

    private final OptionConverter optionConverter;
    private final TasteService tasteService;

    public List<MenuFindResponse> toMenuFindResponseList(List<Menu> menus) {
        return menus.stream().map(this::toMenuFindResponse).toList();
    }

    public Menu toMenu(Account account, MenuSaveRequest request, String menuImageUrl) {
        Franchise franchise = Franchise.createIdFranchise(request.franchiseId());
        Menu menu = Menu.createMenu(
            request.originalTitle(),
            request.title(),
            menuImageUrl,
            request.expectedPrice(),
            account,
            franchise,
            request.content()
        );

        tasteService.findTasteByIds(request.tasteIdList())
            .forEach(t -> menu.addMenuTaste(MenuTaste.createMenuTaste(menu, t)));

        request.optionList().stream().map(optionConverter::toOption).forEach(menu::addOption);

        return menu;
    }

    public Menu toMenu(Long menuId, MenuUpdateRequest request, Account account,
        String menuImageUrl) {
        Franchise franchise = Franchise.createIdFranchise(request.franchiseId());

        Menu menu = Menu.createMenu(
            menuId,
            request.originalTitle(),
            request.title(),
            menuImageUrl,
            request.expectedPrice(),
            account,
            franchise,
            request.content()
        );
        request.optionList().stream().map(optionConverter::toOption).forEach(menu::addOption);

        request.tasteIdList().stream()
            .map(Taste::createTaste)
            .map(taste -> MenuTaste.createMenuTaste(menu, taste))
            .forEach(menu::addMenuTaste);

        return menu;
    }

    public MenuSaveResponse toMenuSaveResponse(Menu menu) {
        return new MenuSaveResponse(menu.getId());
    }

    public MenuFindResponse toMenuFindResponse(Menu menu) {
        return new MenuFindResponse(
            menu.getId(),
            new FranchiseFindResponse(menu.getFranchise().getId(),
                menu.getFranchise().getLogoUrl(),
                menu.getFranchise().getName()),
            menu.getPictureUrl(), menu.getCustomMenuName(), menu.getRealMenuName(),
            new AccountFindResponse(menu.getAccount().getId(), menu.getAccount().getImage(),
                menu.getAccount().getNickName(), menu.getAccount().getEmail(),
                menu.getAccount().getSnsAccount(), menu.getAccount().getCreatedAt(),
                menu.getAccount().getMenuList().size()),
            menu.getDescription(), menu.getLikesCount(),
            menu.getCommentCount(),
            menu.getExpectedPrice(),
            menu.getOptionList().stream()
                .map(option -> new OptionConverter().toOptionFindResponse(option)).collect(
                    Collectors.toList()),
            menu.getMenuTasteList().stream()
                .map(menuTaste -> new TasteFindResponse(menuTaste.getTaste().getId(),
                    menuTaste.getTaste().getTasteName(),
                    menuTaste.getTaste().getTasteColor()))
                .collect(
                    Collectors.toList()),
            menu.getCreatedAt(), menu.getUpdatedAt());
    }

    public MenuFindOneResponse toMenuFindOneResponse(Menu menu, Account account) {
        return new MenuFindOneResponse(
            menu.getId(),
            new FranchiseFindResponse(menu.getFranchise().getId(),
                menu.getFranchise().getLogoUrl(),
                menu.getFranchise().getName()),
            menu.getPictureUrl(), menu.getCustomMenuName(), menu.getRealMenuName(),
            new AccountFindResponse(menu.getAccount().getId(), menu.getAccount().getImage(),
                menu.getAccount().getNickName(), menu.getAccount().getEmail(),
                menu.getAccount().getSnsAccount(), menu.getAccount().getCreatedAt(),
                menu.getAccount().getMenuList().size()),
            menu.getDescription(), menu.getLikesCount(),
            menu.getCommentCount(),
            menu.getExpectedPrice(),
            menu.getOptionList().stream()
                .map(option -> new OptionConverter().toOptionFindResponse(option)).collect(
                    Collectors.toList()),
            menu.getMenuTasteList().stream()
                .map(menuTaste -> new TasteFindResponse(menuTaste.getTaste().getId(),
                    menuTaste.getTaste().getTasteName(),
                    menuTaste.getTaste().getTasteColor()))
                .collect(
                    Collectors.toList()),
            menu.getCreatedAt(), menu.getUpdatedAt(),
            !menu.getLikeMenuList().stream()
                .filter(likeMenu -> likeMenu.getAccount().getId().equals(account.getId())).collect(
                    Collectors.toSet()).isEmpty());
    }
}
