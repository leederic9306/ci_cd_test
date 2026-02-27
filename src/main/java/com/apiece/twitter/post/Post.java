package com.apiece.twitter.post;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "posts")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateContent(String content) {
        this.content = content;
    }
    public void incrementCommentCount() {
        this.commentCount++;
    }
        public void decrementCommentCount() {
            if (this.commentCount > 0) {
                this.commentCount--;
            }
        }
}

