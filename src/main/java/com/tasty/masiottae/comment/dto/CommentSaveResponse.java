package com.tasty.masiottae.comment.dto;

import java.time.LocalDateTime;

public record CommentSaveResponse(Long menuId, Long commentId, String comment) {

}
