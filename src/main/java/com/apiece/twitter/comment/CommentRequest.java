package com.apiece.twitter.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(min = 1, max = 100, message = "댓글 내용은 1자 이상 100자 이하로 작성해야 합니다.")
        String content
) {
}
