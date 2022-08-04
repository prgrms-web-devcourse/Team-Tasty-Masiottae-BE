package com.tasty.masiottae.menu.repository;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.enums.MenuSortCond;
import java.util.List;

public interface MenuRepositoryCustom {

    List<Menu> search(Account account, String keyword, MenuSortCond sort, List<Taste> tastes);
}
