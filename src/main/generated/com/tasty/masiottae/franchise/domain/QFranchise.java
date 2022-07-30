package com.tasty.masiottae.franchise.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFranchise is a Querydsl query type for Franchise
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFranchise extends EntityPathBase<Franchise> {

    private static final long serialVersionUID = 2008033561L;

    public static final QFranchise franchise = new QFranchise("franchise");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath logoUrl = createString("logoUrl");

    public final ListPath<com.tasty.masiottae.menu.domain.Menu, com.tasty.masiottae.menu.domain.QMenu> menuList = this.<com.tasty.masiottae.menu.domain.Menu, com.tasty.masiottae.menu.domain.QMenu>createList("menuList", com.tasty.masiottae.menu.domain.Menu.class, com.tasty.masiottae.menu.domain.QMenu.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public QFranchise(String variable) {
        super(Franchise.class, forVariable(variable));
    }

    public QFranchise(Path<? extends Franchise> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFranchise(PathMetadata metadata) {
        super(Franchise.class, metadata);
    }

}

