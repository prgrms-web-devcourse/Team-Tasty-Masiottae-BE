package com.tasty.masiottae.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.repository.MenuRepository;
import com.tasty.masiottae.menu.repository.TasteRepository;
import com.tasty.masiottae.option.domain.Option;
import com.tasty.masiottae.option.dto.OptionSaveRequest;
import io.findify.s3mock.S3Mock;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MenuServiceTest {

    @Autowired
    MenuRepository menuRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    FranchiseRepository franchiseRepository;
    @Autowired

    TasteRepository tasteRepository;
    @Autowired
    MenuService menuService;
    @Autowired
    S3Mock s3Mock;

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
    @DisplayName("메뉴를 생성한다.")
    void menuSaveTest() {
        // Given
        List<OptionSaveRequest> optionSaveRequests = List.of(
                new OptionSaveRequest("옵션1", "설명1"),
                new OptionSaveRequest("옵션2", "설명2"),
                new OptionSaveRequest("옵션3", "설명3")
        );

        List<Taste> tastes = List.of(
                tasteRepository.save(Taste.createTaste("매운맛", "####")),
                tasteRepository.save(Taste.createTaste("단맛", "####")),
                tasteRepository.save(Taste.createTaste("짠맛", "####"))
        );

        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.png", "img/png",
                "Hello".getBytes());

        MenuSaveRequest request = new MenuSaveRequest(
                account.getId(),
                franchise.getId(),
                "커스텀 이름",
                "맛있습니다",
                "원래 메뉴 이름",
                15000,
                optionSaveRequests,
                List.of(tastes.get(0).getId(), tastes.get(1).getId(), tastes.get(2).getId())
        );

        // When
        MenuSaveResponse response = menuService.createMenu(request, multipartFile);

        // Then
        Menu findMenu = menuRepository.findById(response.menuId()).get();

        assertAll(
                () -> assertThat(findMenu.getPictureUrl()).startsWith(
                        "http://localhost:8001/masiottae-image-bucket/menu/"),
                () -> assertThat(findMenu.getAccount().getId()).isEqualTo(request.userId()),
                () -> assertThat(findMenu.getCustomMenuName())
                        .isEqualTo(request.title()),
                () -> assertThat(findMenu.getDescription())
                        .isEqualTo(request.content()),
                () -> assertThat(findMenu.getRealMenuName()).isEqualTo(request.originalTitle()),
                () -> assertThat(findMenu.getExpectedPrice()).isEqualTo(request.expectedPrice()),
                () -> assertThat(
                        findMenu.getOptionList().stream().map(Option::getId)).containsAll(
                        request.tastes()),
                () -> assertThat(
                        findMenu.getMenuTastes().stream().map(taste -> taste.getTaste().getId())
                                .toList().containsAll(request.tastes()))
        );
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
