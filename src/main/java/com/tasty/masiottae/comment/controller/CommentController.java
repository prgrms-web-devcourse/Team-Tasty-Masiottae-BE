package com.tasty.masiottae.comment.controller;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.comment.dto.CommentFindResponse;
import com.tasty.masiottae.comment.dto.CommentSaveRequest;
import com.tasty.masiottae.comment.dto.CommentSaveResponse;
import com.tasty.masiottae.comment.dto.CommentUpdateRequest;
import com.tasty.masiottae.comment.service.CommentService;
import com.tasty.masiottae.security.annotation.LoginAccount;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/menu/{menuId}/comments")
    public ResponseEntity<List<CommentFindResponse>> getAllComment(
        @PathVariable("menuId") Long menuId) {
        return ResponseEntity.ok().body(commentService.findAllCommentOfOneMenu(menuId));
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentSaveResponse> saveComment(
        @Valid @RequestBody CommentSaveRequest commentSaveRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.createComment(commentSaveRequest));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
        @PathVariable("commentId") Long commentId, @LoginAccount Account account,
        @Valid @RequestBody CommentUpdateRequest request) {
        commentService.updateComment(commentId, account, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> removeComment(
        @PathVariable("commentId") Long commentId, @LoginAccount Account account) {
        commentService.deleteComment(account, commentId);
        return ResponseEntity.noContent().build();
    }
}
