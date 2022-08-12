package com.tasty.masiottae.menu.repository;

import static com.tasty.masiottae.menu.domain.QMenuTaste.menuTaste;

import com.amazonaws.util.CollectionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.SearchCond;
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
    public List<Menu> search(SearchCond searchCond) {
        return queryFactory.select(menuTaste.menu)
                .distinct()
                .from(menuTaste)
                .where(searchExpression(searchCond))
                .orderBy(sortCond(searchCond.menuSortCond()))
                .fetch();
    }

    private BooleanExpression searchExpression(SearchCond searchCond) {
        return switch (searchCond.searchType()) {
            case ALL_MENU -> allSearchCond(searchCond);
            case LIKE_MENU -> likeSearchCond(searchCond);
            case MY_MENU -> mySearchCond(searchCond);
        };
    }

    private BooleanExpression allSearchCond(SearchCond searchCond) {
        return containKeyword(searchCond.keyword()).and(tasteIn(searchCond.tastes()))
                .and(franchiseEq(searchCond.franchise()));
    }

    private BooleanExpression mySearchCond(SearchCond searchCond) {
        return containKeyword(searchCond.keyword()).and(tasteIn(searchCond.tastes()))
                .and(accountEq(searchCond.account()));
    }

    private BooleanExpression likeSearchCond(SearchCond searchCond) {
        return containKeyword(searchCond.keyword()).and(tasteIn(searchCond.tastes()))
                .and(likeMenuAccountContain(searchCond.account()));
    }

    private BooleanExpression likeMenuAccountContain(Account account) {
        return menuTaste.menu.likeMenuList.any().account.eq(account);
    }

    private BooleanExpression franchiseEq(Franchise franchise) {
        return Objects.isNull(franchise) ? null : menuTaste.menu.franchise.eq(franchise);
    }

    private BooleanExpression accountEq(Account account) {
        return Objects.isNull(account) ? null : menuTaste.menu.account.eq(account);
    }

    private OrderSpecifier sortCond(MenuSortCond menuSortCond) {
        return switch (menuSortCond) {
            case RECENT -> menuTaste.menu.createdAt.desc();
            case LIKE -> menuTaste.menu.likesCount.desc();
            case COMMENT -> menuTaste.menu.commentCount.desc();
        };
    }

    public BooleanExpression tasteIn(List<Taste> tastes) {
        return CollectionUtils.isNullOrEmpty(tastes) ? null : menuTaste.taste.in(tastes);
    }

    public BooleanExpression containKeyword(String keyword) {
        return Objects.isNull(keyword) ? null : menuTaste.menu.customMenuName.contains(keyword);
    }
}
