package com.tasty.masiottae.option.domain;

import com.tasty.masiottae.menu.domain.Menu;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Builder
    private Option(@NotNull String optionName, String description) {
        this.optionName = optionName;
        this.description = description;
    }

    public static Option createOption(String optionName, String description) {
        return Option.builder()
                .optionName(optionName)
                .description(description)
                .build();
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
