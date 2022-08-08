package com.tasty.masiottae.comment.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_ACCOUNT;
import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_COMMENT;
import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_MENU;
import static com.tasty.masiottae.common.exception.ErrorMessage.NO_COMMENT_CONTENT;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.comment.CommentConverter;
import com.tasty.masiottae.comment.domain.Comment;
import com.tasty.masiottae.comment.dto.CommentFindResponse;
import com.tasty.masiottae.comment.dto.CommentSaveRequest;
import com.tasty.masiottae.comment.dto.CommentSaveResponse;
import com.tasty.masiottae.comment.dto.CommentUpdateRequest;
import com.tasty.masiottae.comment.repository.CommentRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.repository.MenuRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MenuRepository menuRepository;
    private final AccountRepository accountRepository;
    private final CommentConverter commentConverter;

    @Transactional
    public CommentSaveResponse createComment(CommentSaveRequest request) {
        Menu menu = menuRepository.findById(request.menuId())
            .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MENU.getMessage()));
        Account account = accountRepository.findById(request.userId())
            .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_ACCOUNT.getMessage()));

        checkContentIsNotEmpty(request);

        Comment comment = Comment.createComment(account, menu, request.comment());
        Comment savedComment = commentRepository.save(comment);

        return new CommentSaveResponse(request.menuId(), savedComment.getId(),
            savedComment.getContent());
    }

    public Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(
                NOT_FOUND_COMMENT.getMessage()));
    }

    public List<CommentFindResponse> findAllCommentOfOneMenu(Long menuId) {
        List<Comment> commentsOfOneMenu = commentRepository.findAllByMenuId(menuId);
        return commentsOfOneMenu.stream()
            .map(commentConverter::toCommentFindResponse)
            .collect(Collectors.toList());
    }

    private void checkContentIsNotEmpty(CommentSaveRequest request) {
        if (!StringUtils.hasText(request.comment())) {
            throw new IllegalArgumentException(NO_COMMENT_CONTENT.getMessage());
        }
    }

    @Transactional
    public void updateComment(Long commentId, CommentUpdateRequest request) {
        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(
                NOT_FOUND_COMMENT.getMessage()));
        findComment.changeContent(request.comment());
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
