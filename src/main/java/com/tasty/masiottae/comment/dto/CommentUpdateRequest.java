package com.tasty.masiottae.comment.dto;

import javax.validation.constraints.NotBlank;

public record CommentUpdateRequest(@NotBlank String comment) {

}
