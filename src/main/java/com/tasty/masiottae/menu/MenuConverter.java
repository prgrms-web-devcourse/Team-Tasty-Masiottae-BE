package com.tasty.masiottae.menu;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.common.exception.ErrorMessage;
import com.tasty.masiottae.common.exception.custom.NotFoundException;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuTasteFindResponse;
import com.tasty.masiottae.menu.repository.TasteRepository;
import com.tasty.masiottae.option.OptionConverter;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuConverter {

    private final AccountRepository accountRepository;
    private final FranchiseRepository franchiseRepository;
    private final TasteRepository tasteRepository;
    private final OptionConverter optionConverter;

    public Menu toMenu(MenuSaveRequest request, String menuImageUrl) {
        Account account = findOneAccount(request.userId());
        Franchise franchise = findOneFranchise(request.franchiseId());

        Menu menu = Menu.createMenu(
                request.originalTitle(),
                request.title(),
                menuImageUrl,
                request.expectedPrice(),
                account,
                franchise,
                request.content()
        );

        request.options().stream().map(optionConverter::toOption).forEach(menu::addOption);

        request.tastes().stream()
                .map(this::findOneTaste)
                .map(taste -> MenuTaste.createMenuTaste(menu, taste))
                .forEach(menu::addMenuTaste);

        return menu;
    }

    private Account findOneAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorMessage.NOT_FOUND_ACCOUNT.getMessage()));
    }

    private Franchise findOneFranchise(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .orElseThrow(
                        () -> new NotFoundException(
                                ErrorMessage.NOT_FOUND_FRANCHISE.getMessage()));
    }

    private Taste findOneTaste(Long tasteId) {
        return tasteRepository.findById(tasteId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.NOT_FOUND_TASTE.getMessage()));
    }

    public MenuSaveResponse toMenuSaveResponse(Menu menu) {
        return new MenuSaveResponse(menu.getId());
    }

    public MenuFindResponse toMenuFindResponse(Menu menu) {
        return new MenuFindResponse(
                menu.getId(), menu.getFranchise().getName(),
                menu.getPictureUrl(), menu.getCustomMenuName(), menu.getRealMenuName(),
                menu.getAccount().getNickname(),
                menu.getDescription(), menu.getLikesCount(),
                menu.getExpectedPrice(),
                menu.getOptionList().stream()
                        .map(option -> new OptionConverter().toOptionFindResponse(option)).collect(
                                Collectors.toList()),
                menu.getMenuTastes().stream().map(menuTaste ->
                        new MenuTasteFindResponse(menuTaste.getMenu().getId(),
                                menuTaste.getTaste()
                                        .getId())).collect(Collectors.toSet()));

    }
}
