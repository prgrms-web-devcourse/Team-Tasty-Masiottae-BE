package com.tasty.masiottae.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.menu.MenuConverter;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({MenuService.class, MenuConverter.class})
class MenuServiceTest {

    @Autowired
    MenuRepository menuRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    FranchiseRepository franchiseRepository;
    @Autowired
    MenuService menuService;

    private Account account;
    private Franchise franchise;

    @BeforeEach
    void init() {
        account = Account.createAccount("test@gmail.com", "password", "nickname");

        franchise = Franchise.createFranchise("starbucks", "logo");

        accountRepository.save(account);
        franchiseRepository.save(franchise);

    }

    @Test
    @DisplayName("메뉴 단건 조회")
    void testFindOneMenu() {
        // given
        Menu menu = Menu.createMenu("실제이름", "고객이지은이름", "url", 5000, account, franchise, "설명");
        Menu savedMenu = menuRepository.save(menu);

        // when
        MenuFindResponse findMenu = menuService.findOneMenu(savedMenu.getId());

        // then
        assertAll(
                () -> assertThat(findMenu.title()).isEqualTo("고객이지은이름"),
                () -> assertThat(findMenu.originalTitle()).isEqualTo("실제이름"),
                () -> assertThat(findMenu.franchise()).isEqualTo("starbucks"),
                () -> assertThat(findMenu.author()).isEqualTo("nickname"),
                () -> assertThat(findMenu.expectedPrice()).isEqualTo(5000)
        );
    }
}
