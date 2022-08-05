package com.tasty.masiottae.menu.dto;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.enums.MenuSortCond;
import java.util.List;

public record SearchCond(Account account, String keyword, MenuSortCond menuSortCond,
                         List<Taste> tastes) {

}
