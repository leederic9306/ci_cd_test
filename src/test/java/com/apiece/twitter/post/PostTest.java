package com.apiece.twitter.post;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void incrementCommentCount() {
        Post post = Post.builder().build();
        post.incrementCommentCount();

        assertThat(post.getCommentCount()).isEqualTo(1);
    }
}