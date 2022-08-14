package com.tasty.masiottae.menu.domain;

import java.io.Serializable;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class MenuTasteId implements Serializable {
    private Long menu;
    private Long taste;
}
