package com.tasty.masiottae.menu;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.common.exception.ErrorMessage;
import com.tasty.masiottae.common.exception.custom.NotFoundException;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
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

    public Menu toMenu(MenuSaveUpdateRequest request, String menuImageUrl) {
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

        request.optionList().stream().map(optionConverter::toOption).forEach(menu::addOption);

        request.tasteIdList().stream()
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
}
