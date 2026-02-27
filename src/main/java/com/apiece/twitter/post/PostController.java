package com.apiece.twitter.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostRepository postRepository;

    @GetMapping
    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postRepository.findById(id).orElseThrow();
    }

//    @GetMapping("/search")
//    public Page<Post> searchPosts(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "3") int size
//
//    ) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
//        return postRepository.findAll(pageable);
//    }

    @GetMapping("/search")
    public Slice<Post> searchPosts(
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(defaultValue = "3") int size

    ) {
        int page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (lastPostId == null) {
            return postRepository.findSlicesBy(pageable);
        } else {
            return postRepository.findSlicesByIdLessThan(lastPostId, pageable);
        }
    }

    //여기서 ResponseEntity를 사용하지 않고 @ResponseStatus(HttpStatus.CREATED)로 대체할 수 있다.
    //ResponseEntity는 HTTP 응답의 상태 코드, 헤더, 본문 등을 포함하는 객체로, 더 세밀한 제어가 필요할 때 사용된다.
    //하지만 단순히 상태 코드만 설정하고 싶을 때는 @ResponseStatus 어노테이션을 사용하는 것이 더 간단하다.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@RequestBody Post post) {
        Post newPost = Post.builder()
                .content(post.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        postRepository.save(newPost);
        return newPost;
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post postRequest) {
        return postRepository.findById(id)
                .map(post -> {
                    post.updateContent(postRequest.getContent());
                    return postRepository.save(post);
                }).orElseThrow();

    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
    }
}
