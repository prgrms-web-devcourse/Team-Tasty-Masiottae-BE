package com.tasty.masiottae.likemenu.domain;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.menu.domain.Menu;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeMenuId implements Serializable {

    private Account account;

    private Menu menu;

}
