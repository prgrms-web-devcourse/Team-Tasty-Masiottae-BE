package com.tasty.masiottae.menu;

import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.option.OptionConverter;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MenuConverter {

    public MenuFindResponse toMenuFindResponse(Menu menu) {
        return new MenuFindResponse(menu.getId(), menu.getFranchise().getName(),
                menu.getPictureUrl(), menu.getCustomMenuName(),
                menu.getRealMenuName(), menu.getAccount().getNickname(),
                menu.getDescription(), menu.getLikesCount(),
                menu.getExpectedPrice(), menu.getOptionList().stream()
                .map(option -> new OptionConverter().toOptionFindResponse(option))
                .collect(Collectors.toList()),
                menu.getTastes().stream()
                        .map(taste -> new TasteConverter().toTasteFindResponse(taste))
                        .collect(Collectors.toList()));
    }
}
