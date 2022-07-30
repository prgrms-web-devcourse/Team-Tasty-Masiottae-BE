package com.tasty.masiottae.menu.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMenu is a Querydsl query type for Menu
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMenu extends EntityPathBase<Menu> {

    private static final long serialVersionUID = 1699297455L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMenu menu = new QMenu("menu");

    public final com.tasty.masiottae.account.domain.QAccount account;

    public final ListPath<com.tasty.masiottae.comment.domain.Comment, com.tasty.masiottae.comment.domain.QComment> comments = this.<com.tasty.masiottae.comment.domain.Comment, com.tasty.masiottae.comment.domain.QComment>createList("comments", com.tasty.masiottae.comment.domain.Comment.class, com.tasty.masiottae.comment.domain.QComment.class, PathInits.DIRECT2);

    public final StringPath customMenuName = createString("customMenuName");

    public final NumberPath<Integer> expectedPrice = createNumber("expectedPrice", Integer.class);

    public final SetPath<Flavor, QFlavor> flavors = this.<Flavor, QFlavor>createSet("flavors", Flavor.class, QFlavor.class, PathInits.DIRECT2);

    public final com.tasty.masiottae.franchise.domain.QFranchise franchise;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.tasty.masiottae.likemenu.domain.LikeMenu, com.tasty.masiottae.likemenu.domain.QLikeMenu> likeMenuList = this.<com.tasty.masiottae.likemenu.domain.LikeMenu, com.tasty.masiottae.likemenu.domain.QLikeMenu>createList("likeMenuList", com.tasty.masiottae.likemenu.domain.LikeMenu.class, com.tasty.masiottae.likemenu.domain.QLikeMenu.class, PathInits.DIRECT2);

    public final NumberPath<Integer> likesCount = createNumber("likesCount", Integer.class);

    public final ListPath<com.tasty.masiottae.option.domain.Option, com.tasty.masiottae.option.domain.QOption> optionList = this.<com.tasty.masiottae.option.domain.Option, com.tasty.masiottae.option.domain.QOption>createList("optionList", com.tasty.masiottae.option.domain.Option.class, com.tasty.masiottae.option.domain.QOption.class, PathInits.DIRECT2);

    public final StringPath pictureUrl = createString("pictureUrl");

    public final StringPath realMenuName = createString("realMenuName");

    public QMenu(String variable) {
        this(Menu.class, forVariable(variable), INITS);
    }

    public QMenu(Path<? extends Menu> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMenu(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMenu(PathMetadata metadata, PathInits inits) {
        this(Menu.class, metadata, inits);
    }

    public QMenu(Class<? extends Menu> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new com.tasty.masiottae.account.domain.QAccount(forProperty("account")) : null;
        this.franchise = inits.isInitialized("franchise") ? new com.tasty.masiottae.franchise.domain.QFranchise(forProperty("franchise")) : null;
    }

}

