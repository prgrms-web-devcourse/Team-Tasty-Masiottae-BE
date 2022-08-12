package com.tasty.masiottae.menu.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.config.QuerydslConfig;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.SearchCond;
import com.tasty.masiottae.menu.enums.MenuSortCond;
import com.tasty.masiottae.menu.enums.SearchType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QuerydslConfig.class, MenuRepositoryImpl.class})
class MenuRepositoryTest {

    @Autowired
    MenuRepository menuRepository;
    @Autowired
    FranchiseRepository franchiseRepository;
    @Autowired
    TasteRepository tasteRepository;
    @Autowired
    AccountRepository accountRepository;

    List<Taste> tastes;
    Account account;
    Menu menu1;
    Menu menu2;
    Menu menu3;
    Franchise franchise;

    @BeforeEach
    void init() {
        franchise = franchiseRepository.save(
                Franchise.createFranchise("스타벅스", "aaa.com"));
        account = accountRepository.save(
                Account.createAccount("test@gmail.com", "passwrod", "nickanme", "aaa.com"));

        tastes = List.of(
                tasteRepository.save(Taste.createTaste("매운맛", "####")),
                tasteRepository.save(Taste.createTaste("단맛", "####")),
                tasteRepository.save(Taste.createTaste("짠맛", "####"))
        );

        menu1 = menuRepository.save(
                Menu.createMenu("원래 이름", "커스텀 이름", "aaa.com", 15000, account, franchise, "설명"));
        menu2 = menuRepository.save(
                Menu.createMenu("원래 이름", "커스텀 이름", "aaa.com", 15000, account, franchise, "설명"));
        menu3 = menuRepository.save(
                Menu.createMenu("원래 이름", "커스텀 이름", "aaa.com", 15000, account, franchise, "설명"));

        menu1.addMenuTaste(MenuTaste.createMenuTaste(menu1, tastes.get(1)));

        menu2.addMenuTaste(MenuTaste.createMenuTaste(menu2, tastes.get(1)));
        menu2.addMenuTaste(MenuTaste.createMenuTaste(menu2, tastes.get(2)));

        menu3.addMenuTaste(MenuTaste.createMenuTaste(menu3, tastes.get(0)));
    }

    @Test
    @DisplayName("메뉴를 검색한다.")
    void searchTest() {
        // When
        List<Menu> search = menuRepository.search(
                new SearchCond(SearchType.ALL_MENU, account, "커스텀", MenuSortCond.RECENT, franchise,
                        Collections.singletonList(tastes.get(0))));

        // Then
        assertAll(
                () -> assertThat(search.size()).isEqualTo(1),
                () -> assertThat(search.get(0).getId()).isEqualTo(menu3.getId())
        );
    }


}