package com.tasty.masiottae.menu.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(MenuTasteId.class)
@Table(name = "menu_taste")
@Entity
public class MenuTaste {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taste_id", nullable = false)
    private Taste taste;

    public static MenuTaste createMenuTaste(Menu menu, Taste taste) {
        return new MenuTaste(menu, taste);
    }
}
