package com.tasty.masiottae.menu.domain;

import java.io.Serializable;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class MenuTasteId implements Serializable {

    private Menu menu;
    private Taste taste;
}
