package com.tasty.masiottae.likemenu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.config.S3TestConfig;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.likemenu.repository.LikeMenuRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.MenuFindResponse;
import com.tasty.masiottae.menu.dto.MenuSaveResponse;
import com.tasty.masiottae.menu.dto.MenuSaveUpdateRequest;
import com.tasty.masiottae.menu.repository.MenuRepository;
import com.tasty.masiottae.menu.repository.TasteRepository;
import com.tasty.masiottae.menu.service.MenuService;
import com.tasty.masiottae.option.dto.OptionSaveRequest;
import io.findify.s3mock.S3Mock;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(S3TestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
class LikeMenuServiceTest {

    @Autowired
    LikeMenuService likeMenuService;

    @Autowired
    LikeMenuRepository likeMenuRepository;

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

    Account account;
    Franchise franchise;
    Long menuId;

    @BeforeEach
    void init() {
        account = Account.createAccount("test@gmail.com", "password", "nickname", "imageUrl");

        franchise = Franchise.createFranchise("starbucks", "logo");

        accountRepository.save(account);
        franchiseRepository.save(franchise);

        // Given
        List<OptionSaveRequest> optionSaveRequests = List.of(new OptionSaveRequest("옵션1", "설명1"),
                new OptionSaveRequest("옵션2", "설명2"), new OptionSaveRequest("옵션3", "설명3"));

        List<Taste> tastes = List.of(tasteRepository.save(Taste.createTaste("매운맛", "####")),
                tasteRepository.save(Taste.createTaste("단맛", "####")),
                tasteRepository.save(Taste.createTaste("짠맛", "####")));

        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.png", "img/png",
                "Hello".getBytes());

        MenuSaveUpdateRequest request = new MenuSaveUpdateRequest(account.getId(),
                franchise.getId(), "커스텀 이름", "맛있습니다", "원래 메뉴 이름", 15000, optionSaveRequests,
                List.of(tastes.get(0).getId(), tastes.get(1).getId(), tastes.get(2).getId()));

        // When
        MenuSaveResponse menuSaveResponse = menuService.createMenu(request, multipartFile);

        // Then
        Menu menu = menuRepository.findById(menuSaveResponse.menuId()).get();
        menuId = menu.getId();

    }

    @AfterEach
    void after() {
        likeMenuRepository.deleteAll();
        menuRepository.deleteAll();
        accountRepository.deleteAll();
        tasteRepository.deleteAll();
        franchiseRepository.deleteAll();
    }
    @Test
    @DisplayName("좋아요가 안된 메뉴를 좋아요로 바꿀 수 있다.")
    void changeLike() {
        likeMenuService.changeLike(account, menuId);
        MenuFindResponse oneMenu = menuService.findOneMenu(menuId);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<MenuFindResponse> likeMenuPage = likeMenuService.getPageLikeMenuByAccount(account,
                pageRequest);

        assertAll(() -> assertThat(oneMenu.likes()).isEqualTo(1),
                () -> assertThat(likeMenuPage).hasSize(1));
    }

    @Test
    @DisplayName("좋아요가 된 메뉴를 좋아요 취소 할 수 있다.")
    void cancleLike() {
        likeMenuService.changeLike(account, menuId);
        likeMenuService.changeLike(account, menuId);
        MenuFindResponse oneMenu = menuService.findOneMenu(menuId);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<MenuFindResponse> likeMenuPage = likeMenuService.getPageLikeMenuByAccount(account,
                pageRequest);

        assertAll(() -> assertThat(oneMenu.likes()).isEqualTo(0),
                () -> assertThat(likeMenuPage).hasSize(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 50, 100})
    @DisplayName("n명의 유저가 동시에 좋아요를 누를시 메뉴의 좋아요 카운트는 n이어야 한다.")
    void multiLikeAddTest() throws InterruptedException {
        int count = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        List<Account> accountList = initAccountList(count);

        for (Account account : accountList) {
            executorService.execute(() -> {
                likeMenuService.changeLike(account, menuId);
                latch.countDown();
            });
        }
        latch.await();
        executorService.shutdown();

        Menu menu = menuRepository.findById(menuId).get();
        Integer likesCount = menu.getLikesCount();

        assertThat(likesCount).isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 50, 100})
    @DisplayName("좋아요가 되어 있는 n명의 유저가 동시에 좋아요를 누를시 메뉴의 좋아요 카운트는 0이어야 한다.")
    void multiLikeRemoveTest() throws InterruptedException {
        int count = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch createLatch = new CountDownLatch(count);
        List<Account> accountList = initAccountList(count);

        for (Account account : accountList) {
            executorService.execute(() -> {
                likeMenuService.changeLike(account, menuId);
                createLatch.countDown();
            });
        }
        createLatch.await();
        executorService.shutdown();

        executorService = Executors.newFixedThreadPool(count);
        CountDownLatch deleteLatch = new CountDownLatch(count);

        for (Account account : accountList) {
            executorService.execute(() -> {
                likeMenuService.changeLike(account, menuId);
                deleteLatch.countDown();
            });
        }
        deleteLatch.await();
        executorService.shutdown();

        Menu menu = menuRepository.findById(menuId).get();
        Integer likesCount = menu.getLikesCount();

        assertThat(likesCount).isEqualTo(0);
    }


    private List<Account> initAccountList(int count) {

        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            accountList.add(accountRepository.save(
                    Account.createAccount("test@gmail.com" + i, "password" + i, "nickname" + i,
                            "imageUrl" + i)));
        }

        return accountList;
    }
}