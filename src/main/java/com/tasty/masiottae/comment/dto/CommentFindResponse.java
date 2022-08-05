package com.tasty.masiottae.comment.dto;

import com.tasty.masiottae.account.dto.AccountFindResponse;
import java.time.LocalDateTime;

public record CommentFindResponse(Long id, Long menuId, AccountFindResponse author,
                                  String comment, LocalDateTime createdAt,
                                  LocalDateTime updatedAt) {

}
