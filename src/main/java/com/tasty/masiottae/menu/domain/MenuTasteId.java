package com.tasty.masiottae.menu.domain;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
public class MenuTasteId implements Serializable {
    private Long menu;
    private Long taste;
}
