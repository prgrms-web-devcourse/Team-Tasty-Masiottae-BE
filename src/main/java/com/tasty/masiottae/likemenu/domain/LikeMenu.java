package com.tasty.masiottae.likemenu.domain;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.menu.domain.Menu;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(LikeMenuId.class)
@Table(name = "likes_menu")
@Entity
public class LikeMenu {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    public LikeMenu(Account account, Menu menu) {
        if (Objects.isNull(account) || Objects.isNull(menu)) {
            throw new RuntimeException("유저와 메뉴가 null 입니다.");
        }

        setAccount(account);
        setMenu(menu);
    }

    private void setAccount(Account account) {
        if (Objects.nonNull(this.account)) {
            this.account.getLikeMenuList().remove(this);
        }
        this.account = account;
        this.account.getLikeMenuList().add(this);
    }

    private void setMenu(Menu menu) {
        if (Objects.nonNull(this.menu)) {
            this.menu.getLikeMenuList().remove(this);
        }
        this.menu = menu;
        this.menu.getLikeMenuList().add(this);
    }

}
