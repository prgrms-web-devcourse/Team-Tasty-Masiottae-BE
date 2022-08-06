package com.tasty.masiottae.comment.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CommentSaveRequest(@NotNull(message = "유저의 Id는 필수입니다.") Long userId,
                                 @NotNull(message = "메뉴 Id는 필수입니다.") Long menuId,
                                 @NotBlank(message = "댓글 내용을 입력해주세요.") String comment) {

}
