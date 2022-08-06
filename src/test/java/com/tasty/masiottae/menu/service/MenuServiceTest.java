package com.tasty.masiottae.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.config.S3TestConfig;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.dto.SearchMyMenuRequest;
import com.tasty.masiottae.menu.dto.SearchMyMenuResponse;
import com.tasty.masiottae.menu.enums.MenuSortCond;
import com.tasty.masiottae.menu.repository.MenuRepository;
import com.tasty.masiottae.menu.repository.TasteRepository;
import com.tasty.masiottae.option.domain.Option;
import com.tasty.masiottae.option.dto.OptionSaveRequest;
import io.findify.s3mock.S3Mock;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(S3TestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
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
    private MenuSaveResponse menuSaveResponse;
    private List<Taste> tastes;
    private List<OptionSaveRequest> optionSaveRequests;

    @BeforeEach
    void initMenuSave() {
        account = Account.createAccount("test@gmail.com", "password", "nickname", "imageUrl");

        franchise = Franchise.createFranchise("starbucks", "logo");

        accountRepository.save(account);
        franchiseRepository.save(franchise);

        // Given
        optionSaveRequests = List.of(
                new OptionSaveRequest("옵션1", "설명1"),
                new OptionSaveRequest("옵션2", "설명2"),
                new OptionSaveRequest("옵션3", "설명3")
        );

        tastes = List.of(
                tasteRepository.save(Taste.createTaste("매운맛", "####")),
                tasteRepository.save(Taste.createTaste("단맛", "####")),
                tasteRepository.save(Taste.createTaste("짠맛", "####"))
        );

        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.png", "img/png",
                "Hello".getBytes());

        MenuSaveUpdateRequest request = new MenuSaveUpdateRequest(
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
        menuSaveResponse = menuService.createMenu(request, multipartFile);

        // Then
        Menu findMenu = menuRepository.findById(menuSaveResponse.menuId()).get();

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
                        request.tasteIdList()),
                () -> assertThat(
                        findMenu.getMenuTasteSet().stream().map(taste -> taste.getTaste().getId())
                                .toList().containsAll(request.tasteIdList()))
        );
    }


    @Test
    @DisplayName("메뉴 단건 조회")
    void testFindOneMenu() {

        // when
        MenuFindResponse findMenu = menuService.findOneMenu(menuSaveResponse.menuId());

        // then
        assertThat(findMenu).isNotNull();
    }

    @Test
    @DisplayName("메뉴 수정")
    void testUpdateMenu() {
        // Given
        List<OptionSaveRequest> optionSaveRequests = List.of(
                new OptionSaveRequest("옵션1", "설명1"),
                new OptionSaveRequest("옵션2", "설명2"),
                new OptionSaveRequest("옵션3", "설명3")
        );

        List<Taste> tastes = List.of(
                tasteRepository.save(Taste.createTaste("단짠", "####")),
                tasteRepository.save(Taste.createTaste("쓴맛", "####")),
                tasteRepository.save(Taste.createTaste("신맛", "####"))
        );

        MenuSaveUpdateRequest request = new MenuSaveUpdateRequest(
                account.getId(),
                franchise.getId(),
                "커스텀 이름 변경",
                "맛없습니다",
                "수정 메뉴 이름",
                25000,
                optionSaveRequests,
                List.of(tastes.get(0).getId(), tastes.get(1).getId(), tastes.get(2).getId())
        );

        MockMultipartFile multipartFile = new MockMultipartFile("updateFile", "image.png", "img/png",
                "update".getBytes());
        menuService.updateMenu(menuSaveResponse.menuId(), request, multipartFile);
        Menu findMenu = menuService.findByFetchEntity(menuSaveResponse.menuId());
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
                        request.tasteIdList()),
                () -> assertThat(
                        findMenu.getMenuTasteSet().stream().map(taste -> taste.getTaste().getId())
                                .toList().containsAll(request.tasteIdList()))
        );

    }

    @Test
    @DisplayName("메뉴 삭제")
    void testDelete() {
        menuService.delete(menuSaveResponse.menuId());
        assertThrows(EntityNotFoundException.class, () -> menuService.findOneMenu(menuSaveResponse.menuId()));
    }

    @Test
    @DisplayName("나만의 메뉴판을 조회한다.")
    void searchMyMenuTest() {
        // Given
        saveMoreMenus();
        SearchMyMenuRequest request = new SearchMyMenuRequest(0, 3, "커스텀",
                MenuSortCond.RECENT.getUrlValue(), tastes.stream().map(Taste::getId).toList());

        // When
        SearchMyMenuResponse responses = menuService.searchMyMenu(account.getId(), request);

        // Then
        assertThat(responses.menu().size()).isEqualTo(1);
    }

    private void saveMoreMenus() {
        menuService.createMenu(new MenuSaveUpdateRequest(
                        account.getId(),
                        franchise.getId(),
                        "커스텀 이름",
                        "맛있습니다",
                        "원래 메뉴 이름",
                        15000,
                        optionSaveRequests,
                        List.of(tastes.get(1).getId(), tastes.get(2).getId())),
                new MockMultipartFile("image", "image.png", "img/png",
                        "image".getBytes()));

        menuService.createMenu(new MenuSaveUpdateRequest(
                        account.getId(),
                        franchise.getId(),
                        "커스텀 이름",
                        "맛있습니다",
                        "원래 메뉴 이름",
                        15000,
                        optionSaveRequests,
                        List.of(tastes.get(2).getId())),
                new MockMultipartFile("image", "image.png", "img/png",
                        "image".getBytes()));
    }
}
