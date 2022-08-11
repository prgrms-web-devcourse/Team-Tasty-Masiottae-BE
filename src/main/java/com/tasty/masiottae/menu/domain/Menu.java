package com.tasty.masiottae.menu.domain;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.comment.domain.Comment;
import com.tasty.masiottae.common.base.BaseTimeEntity;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.likemenu.domain.LikeMenu;
import com.tasty.masiottae.option.domain.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
public class Menu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_menu_name", nullable = false)
    private String realMenuName;

    @Column(name = "custom_menu_name", nullable = false)
    private String customMenuName;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "expected_price", nullable = false)
    private Integer expectedPrice;

    @Column(name = "likes_count")
    private Integer likesCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    private List<LikeMenu> likeMenuList = new ArrayList<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> optionList = new ArrayList<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuTaste> menuTasteList = new ArrayList<>();

    @Builder
    private Menu(Long id, String realMenuName, String customMenuName, String pictureUrl,
        Integer expectedPrice, Account account, Franchise franchise, String description) {
        this.id = id;
        this.realMenuName = realMenuName;
        this.customMenuName = customMenuName;
        this.pictureUrl = pictureUrl;
        this.expectedPrice = expectedPrice;
        this.likesCount = 0;
        this.commentCount = 0;
        this.account = account;
        this.franchise = franchise;
        this.description = description;
    }

    public static Menu createMenu(String realMenuName, String customMenuName, String pictureUrl,
        Integer expectedPrice, Account account, Franchise franchise, String description) {
        return Menu.builder()
            .realMenuName(realMenuName)
            .customMenuName(customMenuName)
            .pictureUrl(pictureUrl)
            .expectedPrice(expectedPrice)
            .account(account)
            .franchise(franchise)
            .description(description)
            .build();
    }

    public static Menu createMenu(Long menuId, String realMenuName, String customMenuName,
        String pictureUrl,
        Integer expectedPrice, Account account, Franchise franchise, String description) {
        return Menu.builder()
            .id(menuId)
            .realMenuName(realMenuName)
            .customMenuName(customMenuName)
            .pictureUrl(pictureUrl)
            .expectedPrice(expectedPrice)
            .account(account)
            .franchise(franchise)
            .description(description)
            .build();
    }

    public void addComment(Comment comment) {
        commentCount++;
        commentList.add(comment);
    }

    public void removeComment(Comment comment) {
        commentCount--;
        commentList.remove(comment);
    }

    public void addMenuTaste(MenuTaste menuTaste) {
        menuTasteList.add(menuTaste);
    }

    public void addOption(Option option) {
        optionList.add(option);
        option.setMenu(this);
    }

    public void update(Menu menu, Set<MenuTaste> menuTasteSet) {
        this.optionList.clear();
        this.menuTasteList.clear();
        this.realMenuName = menu.realMenuName;
        this.customMenuName = menu.customMenuName;
        this.description = menu.description;
        this.pictureUrl = menu.pictureUrl;
        this.expectedPrice = menu.expectedPrice;
        menu.optionList.forEach(option -> this.optionList.add(
            Option.createOption(option.getOptionName(), option.getDescription())));
        menu.menuTasteList.addAll(menuTasteSet);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int addLike() {
        return ++this.likesCount;
    }

    public int removeLike() {
        return --this.likesCount;
    }

}
