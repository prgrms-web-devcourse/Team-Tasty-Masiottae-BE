package com.tasty.masiottae.menu.repository;

import static com.tasty.masiottae.menu.domain.QMenuTaste.menuTaste;

import com.amazonaws.util.CollectionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.enums.MenuSortCond;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Menu> search(Account account, String keyword, MenuSortCond sort, List<Taste> tastes) {
        return queryFactory.select(menuTaste.menu)
                .distinct()
                .from(menuTaste)
                .where(containKeyword(keyword), tasteIn(tastes), accountEq(account))
                .orderBy(sortCond(sort))
                .fetch();
    }

    private BooleanExpression accountEq(Account account) {
        return Objects.isNull(account) ? null : menuTaste.menu.account.eq(account);
    }

    private OrderSpecifier sortCond(MenuSortCond menuSortCond) {
        return switch (menuSortCond) {
            case RECENT -> menuTaste.menu.createdAt.desc();
            case LIKE -> menuTaste.menu.likesCount.desc();
            case COMMENT -> null;
        };
    }

    public BooleanExpression tasteIn(List<Taste> tastes) {
        return CollectionUtils.isNullOrEmpty(tastes) ? null : menuTaste.taste.in(tastes);
    }

    public BooleanExpression containKeyword(String keyword) {
        return Objects.isNull(keyword) ? null : menuTaste.menu.customMenuName.contains(keyword);
    }
}
