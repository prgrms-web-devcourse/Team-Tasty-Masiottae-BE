package com.tasty.masiottae.menu;

import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.Taste;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuSearchList {

    private List<Menu> menus;

    public MenuSearchList filterByTaste(List<Taste> tastes) {
        if (isNotEmptyTastes(tastes)) {
            menus = menus.stream()
                    .filter(menu -> menu.getMenuTasteList().stream()
                            .map(MenuTaste::getTaste).collect(Collectors.toSet())
                            .containsAll(tastes)).toList();
        }

        return this;
    }

    public MenuSearchList paging(int offset, int limit) {
        int lastIndex = getLastIndex(offset, limit, menus.size());

        // menus.subList 에서 IndexOutOfBoundsException 예외가 던져졌을 때 MenuService에서 잡히지 않는 문제로 인한 임시 예외처리 코드
        if (offset < 0 || lastIndex > menus.size() || lastIndex < offset) {
            throw new IllegalArgumentException();
        }
        menus = menus.subList(offset, lastIndex);
        return this;
    }

    public List<Menu> getMenus() {
        return Collections.unmodifiableList(menus);
    }

    private boolean isNotEmptyTastes(List<Taste> findTasteByIds) {
        return !findTasteByIds.isEmpty();
    }

    private int getLastIndex(int offset, int limit, int size) {
        return Math.min(offset + limit, size);
    }
}
