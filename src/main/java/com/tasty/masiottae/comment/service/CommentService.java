package com.tasty.masiottae.comment.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_ACCOUNT;
import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_MENU;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.comment.CommentConverter;
import com.tasty.masiottae.comment.domain.Comment;
import com.tasty.masiottae.comment.dto.CommentFindResponse;
import com.tasty.masiottae.comment.dto.CommentSaveRequest;
import com.tasty.masiottae.comment.dto.CommentSaveResponse;
import com.tasty.masiottae.comment.repository.CommentRepository;
import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.repository.MenuRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MenuRepository menuRepository;
    private final AccountRepository accountRepository;
    private final CommentConverter commentConverter;

    public CommentSaveResponse createComment(Long menuId, CommentSaveRequest request) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MENU.getMessage()));
        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_ACCOUNT.getMessage()));

        Comment comment = Comment.createComment(account, menu, request.content());
        Comment savedComment = commentRepository.save(comment);

        menu.addComment(savedComment);
        account.addComment(savedComment);
        return new CommentSaveResponse(menuId, savedComment.getId());
    }

    public List<CommentFindResponse> findAllCommentOfOneMenu(Long menuId) {
        List<Comment> commentsOfOneMenu = commentRepository.findAllByMenuId(menuId);
        return commentsOfOneMenu.stream()
            .map(commentConverter::toCommentFindResponse)
            .collect(Collectors.toList());
    }
}
