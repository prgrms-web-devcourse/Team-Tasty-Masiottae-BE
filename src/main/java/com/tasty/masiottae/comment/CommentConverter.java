package com.tasty.masiottae.comment;

import com.tasty.masiottae.account.converter.AccountConverter;
import com.tasty.masiottae.comment.domain.Comment;
import com.tasty.masiottae.comment.dto.CommentFindResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentConverter {

    private final AccountConverter accountConverter;

    public CommentFindResponse toCommentFindResponse(Comment comment) {
        return new CommentFindResponse(
            comment.getId(),
            comment.getMenu().getId(),
            accountConverter.toAccountFindResponse(comment.getAccount()),
            comment.getContent()
        );
    }
}
