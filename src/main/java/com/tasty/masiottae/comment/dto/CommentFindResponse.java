package com.tasty.masiottae.comment.dto;

import com.tasty.masiottae.account.dto.AccountFindResponse;

public record CommentFindResponse(Long id, Long menuId, AccountFindResponse author,
                                  String comment) {

}
