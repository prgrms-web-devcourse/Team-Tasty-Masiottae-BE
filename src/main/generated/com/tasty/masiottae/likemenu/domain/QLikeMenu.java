package com.tasty.masiottae.likemenu.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikeMenu is a Querydsl query type for LikeMenu
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikeMenu extends EntityPathBase<LikeMenu> {

    private static final long serialVersionUID = -1257792387L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikeMenu likeMenu = new QLikeMenu("likeMenu");

    public final com.tasty.masiottae.account.domain.QAccount account;

    public final com.tasty.masiottae.menu.domain.QMenu menu;

    public QLikeMenu(String variable) {
        this(LikeMenu.class, forVariable(variable), INITS);
    }

    public QLikeMenu(Path<? extends LikeMenu> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikeMenu(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikeMenu(PathMetadata metadata, PathInits inits) {
        this(LikeMenu.class, metadata, inits);
    }

    public QLikeMenu(Class<? extends LikeMenu> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new com.tasty.masiottae.account.domain.QAccount(forProperty("account")) : null;
        this.menu = inits.isInitialized("menu") ? new com.tasty.masiottae.menu.domain.QMenu(forProperty("menu"), inits.get("menu")) : null;
    }

}

