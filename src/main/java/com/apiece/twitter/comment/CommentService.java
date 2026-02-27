package com.apiece.twitter.comment;

import com.apiece.twitter.post.Post;
import com.apiece.twitter.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentResponse createComment(Long postId, CommentRequest request){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        // Comment 엔티티 생성 시에는 엔티티매니저가 관리하는 상태가 아니므로,
        // post.incrementCommentCount()를 호출하기 전에 commentRepository.save(comment)를 먼저 호출해야 합니다.
        Comment comment = Comment.builder()
                .content(request.content())
                .post(post)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        post.incrementCommentCount();
        postRepository.save(post);
        return CommentResponse.from(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAllComments(Long postId){
        return commentRepository.findByPostIdOrderByIdDesc(postId)
                .stream()
                .map(comment -> {
                    Post post = comment.getPost();
                    log.info("댓글 ID: {}, 게시글 ID: {}, 게시글 내용: {}", comment.getId(),
                            post.getId(), post.getContent());
                    return CommentResponse.from(comment);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponse getComment(Long postId, Long commentId){
         Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));
        return CommentResponse.from(comment);
    }

//    public CommentResponse updateComment(Long postId, Long commentId, CommentRequest request){
//        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
//                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));
//
//        comment.updateContent(request.content());
//        commentRepository.save(comment);
//
//        return CommentResponse.from(comment);
//    }

    public CommentResponse updateComment(Long postId, Long commentId, CommentRequest request){
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));

        comment.updateContent(request.content());
        // @Transactional 어노테이션이 적용된 메서드 내에서는,
        // 엔티티의 상태가 변경되면 트랜잭션이 커밋될 때 자동으로 변경 사항이 데이터베이스에 반영됩니다.
        //  commentRepository.save(comment);

        return CommentResponse.from(comment);
    }

    public void deleteComment(Long postId, Long commentId){
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));

        Post post = comment.getPost();

        post.decrementCommentCount();

        postRepository.save(post);
        commentRepository.delete(comment);

    }
}
