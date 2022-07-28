package com.tasty.masiottae.menu.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "taste")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Taste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tasteName;
    private String tasteColor;

    @Builder
    private Taste(String tasteName, String tasteColor) {
        this.tasteName = tasteName;
        this.tasteColor = tasteColor;
    }

    public static Taste createTaste(String tasteName, String tasteColor) {
        return new Taste(tasteName, tasteColor);
    }
}
