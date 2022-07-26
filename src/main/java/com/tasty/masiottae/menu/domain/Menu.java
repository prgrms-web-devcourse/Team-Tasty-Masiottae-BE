package com.tasty.masiottae.menu.domain;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.comment.domain.Comment;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.likemenu.domain.LikeMenu;
import com.tasty.masiottae.option.domain.Option;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_menu_name", nullable = false)
    private String realMenuName;

    @Column(name = "custom_menu_name", nullable = false)
    private String customMenuName;

    @Column(name = "picture_url", nullable = false)
    private String pictureUrl;

    @Column(name = "expected_price", nullable = false)
    private Integer expectedPrice;

    @Column(name = "likes_count")
    private Integer likesCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeMenu> likeMenuList = new ArrayList<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> optionList = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "flavor_tag",
            joinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "id"))
    private Set<Flavor> flavors = new HashSet<>();

    @Builder
    private Menu(String realMenuName, String customMenuName, String pictureUrl,
            Integer expectedPrice, Account account, Franchise franchise) {
        this.realMenuName = realMenuName;
        this.customMenuName = customMenuName;
        this.pictureUrl = pictureUrl;
        this.expectedPrice = expectedPrice;
        this.likesCount = 0;
        this.account = account;
        this.franchise = franchise;
    }

    public static Menu createMenu(String realMenuName, String customMenuName, String pictureUrl,
            Integer expectedPrice, Account account, Franchise franchise) {
        return Menu.builder()
                .realMenuName(realMenuName)
                .customMenuName(customMenuName)
                .pictureUrl(pictureUrl)
                .expectedPrice(expectedPrice)
                .account(account)
                .franchise(franchise).build();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addFlavor(Flavor flavor) {
        flavors.add(flavor);
    }
}
