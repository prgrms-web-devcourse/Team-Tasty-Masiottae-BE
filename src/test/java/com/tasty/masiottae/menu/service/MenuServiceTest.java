package com.tasty.masiottae.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.common.exception.custom.ForbiddenException;
import com.tasty.masiottae.config.S3TestConfig;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindOneResponse;
import com.tasty.masiottae.menu.dto.MenuSaveRequest;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuUpdateRequest;
import com.tasty.masiottae.menu.dto.SearchMenuRequest;
import com.tasty.masiottae.menu.dto.SearchMenuResponse;
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
    private Menu findMenu;
    private MenuSaveResponse menuSaveResponse;
    private List<Taste> tastes;
    private List<OptionSaveRequest> optionSaveRequests;

    @BeforeEach
    void initMenuSave() {
        account = accountRepository.save(
                Account.createAccount("test@gmail.com", "password", "nickname", "imageUrl"));
        franchise = franchiseRepository.save(Franchise.createFranchise("starbucks", "logo"));

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

        MenuSaveRequest request = new MenuSaveRequest(
                franchise.getId(),
                "커스텀 이름",
                "맛있습니다",
                "원래 메뉴 이름",
                15000,
                optionSaveRequests,
                List.of(tastes.get(0).getId(), tastes.get(1).getId(), tastes.get(2).getId())
        );

        // When
        menuSaveResponse = menuService.createMenu(account, request, multipartFile);

        // Then
        findMenu = menuRepository.findById(menuSaveResponse.menuId()).get();

        assertAll(
                () -> assertThat(findMenu.getPictureUrl()).startsWith(
                        "http://localhost:8001/masiottae-image-bucket/menu/"),
                () -> assertThat(findMenu.getAccount().getId()).isEqualTo(account.getId()),
                () -> assertThat(findMenu.getCustomMenuName())
                        .isEqualTo(request.title()),
                () -> assertThat(findMenu.getDescription())
                        .isEqualTo(request.content()),
                () -> assertThat(findMenu.getRealMenuName()).isEqualTo(request.originalTitle()),
                () -> assertThat(findMenu.getExpectedPrice()).isEqualTo(request.expectedPrice()),
                () -> assertThat(
                        findMenu.getOptionList().stream().map(Option::getOptionName)).containsAll(
                        optionSaveRequests.stream().map(OptionSaveRequest::name).toList()),
                () -> assertThat(
                        findMenu.getMenuTasteList().stream().map(taste -> taste.getTaste().getId())
                                .toList().containsAll(request.tasteIdList()))
        );
    }

    @Test
    @DisplayName("메뉴 단건 조회")
    void testFindOneMenu() {

        // when
        MenuFindOneResponse findMenu = menuService.findOneMenu(menuSaveResponse.menuId(), account);

        // then
        assertThat(findMenu).isNotNull();
    }

    @Test
    @DisplayName("메뉴 수정(이미지 수정 o)")
    void testUpdateMenu() {
        String originPictureUrl = menuService.findByFetchEntity(menuSaveResponse.menuId()).getPictureUrl();
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

        MenuUpdateRequest request = new MenuUpdateRequest(
                franchise.getId(),
                "커스텀 이름 변경",
                "맛없습니다",
                "수정 메뉴 이름",
                25000,
                optionSaveRequests,
                List.of(tastes.get(0).getId(), tastes.get(1).getId(), tastes.get(2).getId()),
                true
        );

        MockMultipartFile multipartFile = new MockMultipartFile("updateFile", "image.png", "img/png",
                "update".getBytes());
        menuService.updateMenu(menuSaveResponse.menuId(), request, multipartFile, account);
        Menu findMenu = menuService.findByFetchEntity(menuSaveResponse.menuId());
        assertAll(
                () -> assertThat(findMenu.getPictureUrl()).startsWith(
                        "http://localhost:8001/masiottae-image-bucket/menu/"),
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

                        findMenu.getMenuTasteList().stream().map(taste -> taste.getTaste().getId())
                                .toList().containsAll(request.tasteIdList())),
                () -> assertThat(findMenu.getPictureUrl()).isNotEqualTo(originPictureUrl)
        );

    }

    @Test
    @DisplayName("메뉴 수정(이미지 수정 x)")
    void testUpdateMenuNoImage() {
        String originPictureUrl = menuService.findByFetchEntity(menuSaveResponse.menuId()).getPictureUrl();
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

        MenuUpdateRequest request = new MenuUpdateRequest(
                franchise.getId(),
                "커스텀 이름 변경",
                "맛없습니다",
                "수정 메뉴 이름",
                25000,
                optionSaveRequests,
                List.of(tastes.get(0).getId(), tastes.get(1).getId(), tastes.get(2).getId()),
                false
        );

        menuService.updateMenu(menuSaveResponse.menuId(), request, null, account);
        Menu findMenu = menuService.findByFetchEntity(menuSaveResponse.menuId());
        assertAll(
                () -> assertThat(findMenu.getPictureUrl()).startsWith(
                        "http://localhost:8001/masiottae-image-bucket/menu/"),
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
                        findMenu.getMenuTasteList().stream().map(taste -> taste.getTaste().getId())
                                .toList().containsAll(request.tasteIdList())),
                () -> assertThat(findMenu.getPictureUrl()).isEqualTo(originPictureUrl)
        );

    }

    @Test
    @DisplayName("메뉴 수정(이미지가 있다가 제거.)")
    void testUpdateMenuImageNull() {
        String originPictureUrl = menuService.findByFetchEntity(menuSaveResponse.menuId()).getPictureUrl();
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

        MenuUpdateRequest request = new MenuUpdateRequest(
                franchise.getId(),
                "커스텀 이름 변경",
                "맛없습니다",
                "수정 메뉴 이름",
                25000,
                optionSaveRequests,
                List.of(tastes.get(0).getId(), tastes.get(1).getId(), tastes.get(2).getId()),
                true
        );

        menuService.updateMenu(menuSaveResponse.menuId(), request, null, account);
        Menu findMenu = menuService.findByFetchEntity(menuSaveResponse.menuId());
        assertAll(
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
                        findMenu.getMenuTasteList().stream().map(taste -> taste.getTaste().getId())
                                .toList().containsAll(request.tasteIdList())),
                () -> assertThat(findMenu.getPictureUrl()).isEqualTo(null)
        );

    }

    @Test
    @DisplayName("메뉴 삭제(성공)")
    void testDelete() {
        menuService.delete(account, menuSaveResponse.menuId());
        assertThrows(EntityNotFoundException.class, () -> menuService.findOneMenu(menuSaveResponse.menuId(), account));
    }

    @Test
    @DisplayName("메뉴 삭제(실패)")
    void testDeleteFail() {
        Account newAccount = Account.createAccount("new@gmail.com", "password", "new", "imageUrl2");
        accountRepository.save(newAccount);
        assertThrows(ForbiddenException.class, () -> menuService.delete(newAccount, menuSaveResponse.menuId()));
    }

    @Test
    @DisplayName("나만의 메뉴판을 조회한다.")
    void searchMyMenuTest() {
        // Given
        saveMoreMenus();
        SearchMenuRequest request = new SearchMenuRequest(0, 3, "커스텀",
                MenuSortCond.RECENT.getUrlValue(), null, tastes.stream().map(Taste::getId).toList());

        // When
        SearchMenuResponse responses = menuService.searchMyMenu(account, request);

        // Then
        assertThat(responses.menu().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("메뉴를 검색한다.")
    void searchMenuTest() {
        // Given
        saveMoreMenus();
        SearchMenuRequest request = new SearchMenuRequest(0, 1, "이름", "recent", franchise.getId(),
                tastes.stream().map(Taste::getId).toList());

        // When
        SearchMenuResponse responses = menuService.searchAllMenu(request);

        // Then
        assertThat(responses.menu().size()).isEqualTo(1);
    }

    private void saveMoreMenus() {
        menuService.createMenu(account, new MenuSaveRequest(
                        franchise.getId(),
                        "커스텀 이름",
                        "맛있습니다",
                        "원래 메뉴 이름",
                        15000,
                        optionSaveRequests,
                        List.of(tastes.get(1).getId(), tastes.get(2).getId())),
                new MockMultipartFile("image", "image.png", "img/png",
                        "image".getBytes()));

        menuService.createMenu(account, new MenuSaveRequest(
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
