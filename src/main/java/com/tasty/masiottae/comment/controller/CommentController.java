package com.tasty.masiottae.comment.controller;

import com.tasty.masiottae.comment.dto.CommentFindResponse;
import com.tasty.masiottae.comment.dto.CommentSaveRequest;
import com.tasty.masiottae.comment.dto.CommentSaveResponse;
import com.tasty.masiottae.comment.service.CommentService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 한 상세 메뉴의 댓글 목록 가져오기
    @GetMapping("/menu/{menuId}/comments")
    public ResponseEntity<List<CommentFindResponse>> getAllComment(
        @PathVariable("menuId") Long menuId) {
        return ResponseEntity.ok().body(commentService.findAllCommentOfOneMenu(menuId));
    }

    // 하나의 메뉴에 댓글 작성하기
    @PostMapping("/comments")
    public ResponseEntity<CommentSaveResponse> saveComment(
        @Valid @RequestBody CommentSaveRequest commentSaveRequest) {
        commentService.createComment(commentSaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.createComment(commentSaveRequest));
    }
}
