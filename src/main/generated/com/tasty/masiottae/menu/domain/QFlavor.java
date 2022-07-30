package com.tasty.masiottae.menu.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFlavor is a Querydsl query type for Flavor
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QFlavor extends BeanPath<Flavor> {

    private static final long serialVersionUID = 742959598L;

    public static final QFlavor flavor = new QFlavor("flavor");

    public final StringPath flavorName = createString("flavorName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QFlavor(String variable) {
        super(Flavor.class, forVariable(variable));
    }

    public QFlavor(Path<? extends Flavor> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFlavor(PathMetadata metadata) {
        super(Flavor.class, metadata);
    }

}

