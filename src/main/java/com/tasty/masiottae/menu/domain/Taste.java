package com.tasty.masiottae.menu.domain;

import com.tasty.masiottae.common.base.BaseTimeEntity;
import javax.persistence.Column;
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
public class Taste extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "taste_name", unique = true, nullable = false)
    private String tasteName;

    @Column(name = "taste_color", nullable = false)
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
