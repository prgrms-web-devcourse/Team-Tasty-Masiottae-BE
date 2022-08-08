package com.tasty.masiottae.menu.dto;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.enums.MenuSortCond;
import java.util.List;

public record SearchCond(Account account, String keyword, MenuSortCond menuSortCond,
                         Franchise franchise,
                         List<Taste> tastes) {

}
