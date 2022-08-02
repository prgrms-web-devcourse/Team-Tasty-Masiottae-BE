package com.tasty.masiottae.franchise.domain;

import com.tasty.masiottae.common.base.BaseTimeEntity;
import com.tasty.masiottae.menu.domain.Menu;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "franchise")
@Entity
public class Franchise extends BaseTimeEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String logoUrl;

    @OneToMany(mappedBy = "franchise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menuList = new ArrayList<>();

    @Builder
    private Franchise(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }

    public static Franchise createFranchise(String name, String logoUrl) {
        return Franchise.builder()
            .name(name)
            .logoUrl(logoUrl)
            .build();
    }

    public void updateLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
