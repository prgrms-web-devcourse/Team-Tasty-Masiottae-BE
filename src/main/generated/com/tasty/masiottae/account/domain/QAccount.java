package com.tasty.masiottae.account.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccount is a Querydsl query type for Account
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccount extends EntityPathBase<Account> {

    private static final long serialVersionUID = -1645430951L;

    public static final QAccount account = new QAccount("account");

    public final ListPath<com.tasty.masiottae.comment.domain.Comment, com.tasty.masiottae.comment.domain.QComment> commentList = this.<com.tasty.masiottae.comment.domain.Comment, com.tasty.masiottae.comment.domain.QComment>createList("commentList", com.tasty.masiottae.comment.domain.Comment.class, com.tasty.masiottae.comment.domain.QComment.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.tasty.masiottae.likemenu.domain.LikeMenu, com.tasty.masiottae.likemenu.domain.QLikeMenu> likeMenuList = this.<com.tasty.masiottae.likemenu.domain.LikeMenu, com.tasty.masiottae.likemenu.domain.QLikeMenu>createList("likeMenuList", com.tasty.masiottae.likemenu.domain.LikeMenu.class, com.tasty.masiottae.likemenu.domain.QLikeMenu.class, PathInits.DIRECT2);

    public final ListPath<com.tasty.masiottae.menu.domain.Menu, com.tasty.masiottae.menu.domain.QMenu> menuList = this.<com.tasty.masiottae.menu.domain.Menu, com.tasty.masiottae.menu.domain.QMenu>createList("menuList", com.tasty.masiottae.menu.domain.Menu.class, com.tasty.masiottae.menu.domain.QMenu.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final StringPath snsAccount = createString("snsAccount");

    public QAccount(String variable) {
        super(Account.class, forVariable(variable));
    }

    public QAccount(Path<? extends Account> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccount(PathMetadata metadata) {
        super(Account.class, metadata);
    }

}

