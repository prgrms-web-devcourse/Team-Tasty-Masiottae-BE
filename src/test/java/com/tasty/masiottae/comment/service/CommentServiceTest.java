package com.tasty.masiottae.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tasty.masiottae.account.converter.AccountConverter;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.comment.CommentConverter;
import com.tasty.masiottae.comment.domain.Comment;
import com.tasty.masiottae.comment.dto.CommentFindResponse;
import com.tasty.masiottae.comment.dto.CommentSaveRequest;
import com.tasty.masiottae.comment.dto.CommentSaveResponse;
import com.tasty.masiottae.comment.repository.CommentRepository;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.repository.MenuRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({CommentService.class, CommentConverter.class, AccountConverter.class})
class CommentServiceTest {

    @Autowired
    MenuRepository menuRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    FranchiseRepository franchiseRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentService commentService;

    @PersistenceContext
    EntityManager entityManager;
    Menu menu;
    Franchise franchise;
    Account account;

    @BeforeEach
    void setup() {
        franchise = Franchise.createFranchise("franchise", "franchiseLogo");
        account = Account.createAccount("test@gmail.com", "password", "nickname", "profile.png");
        menu = Menu.createMenu("realName", "customMenuName", "picture",
            5000, account, franchise, "description");
        accountRepository.save(account);
        franchiseRepository.save(franchise);
        menuRepository.save(menu);
    }

    @Test
    @DisplayName("댓글 작성")
    void testCreateComment() {
        // given
        Long menuId = 1L;
        CommentSaveRequest request = new CommentSaveRequest(account.getId(), "이것은 댓글이다.");

        // when
        CommentSaveResponse comment = commentService.createComment(menuId, request);

        entityManager.flush();
        entityManager.clear();
        // then
        Comment findComment = commentRepository.findByIdFetch(comment.commentId()).get();
        assertAll(
            () -> assertThat(findComment.getAccount().getId()).isEqualTo(1L),
            () -> assertThat(findComment.getMenu().getId()).isEqualTo(1L),
            () -> assertThat(findComment.getMenu().getRealMenuName()).isEqualTo("realName"),
            () -> assertThat(findComment.getAccount().getEmail()).isEqualTo("test@gmail.com"),
            () -> assertThat(findComment.getContent()).isEqualTo("이것은 댓글이다.")
        );
    }

    @Test
    @DisplayName("하나의 메뉴에 있는 전체 댓글 조회")
    void testFindAllCommentOfOneMenu() {
        // given
        Long menuId = 1L;
        List<Comment> comments = IntStream.range(1, 11).mapToObj(
            i -> Comment.createComment(account, menu, "댓글내용" + i)
        ).collect(Collectors.toList());
        commentRepository.saveAll(comments);

        // when
        List<CommentFindResponse> allCommentOfOneMenu = commentService.findAllCommentOfOneMenu(
            menuId);

        // then
        assertThat(allCommentOfOneMenu).hasSize(10);
    }
}
