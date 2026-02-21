package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.dto.PostRequestDTO;
import com.secondproj.revconnect.dto.PostResponseDTO;
import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.CommentRepository;
import com.secondproj.revconnect.repository.LikeRepository;
import com.secondproj.revconnect.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;


@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    // ✅ CREATE POST
    @PostMapping
    public PostResponseDTO createPost(
            @AuthenticationPrincipal User user,
            @RequestBody PostRequestDTO dto
    ) {
        Post post = postService.createPost(user, dto.getContent(), dto.getHashtags());
        return mapToPostDTO(post);
    }

    // ✅ GET ALL POSTS (Feed)
    @GetMapping
    public List<PostResponseDTO> getAllPosts() {

        return postService.getAllPosts()
                .stream()
                .map(this::mapToPostDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    // ✅ GET MY POSTS
    @GetMapping("/me")
    public List<PostResponseDTO> myPosts(@AuthenticationPrincipal User user) {

        return postService.getMyPosts(user)
                .stream()
                .map(this::mapToPostDTO)
                .toList();
    }

    // ✅ DELETE POST
    @DeleteMapping("/{postId}")
    public String deletePost(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId
    ) {
        postService.deletePost(postId, user);
        return "Post deleted successfully";
    }

    // 🔥 DTO Mapper (must be inside class)
    private PostResponseDTO mapToPostDTO(Post post) {

        long likeCount = likeRepository.countByPost(post);
        long commentCount = commentRepository.findByPost(post).size();

        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setHashtags(post.getHashtags());
        dto.setPinned(post.isPinned());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUserId(post.getUser().getId());
        dto.setUsername(post.getUser().getUsername());
        dto.setLikeCount(likeCount);
        dto.setCommentCount(commentCount);

        return dto;
    }
    @GetMapping("/user/{userId}")
    public List<PostResponseDTO> getUserPosts(@PathVariable Long userId) {
        return postService.getPostsByUserId(userId)
                .stream()
                .map(this::mapToPostDTO)
                .toList();
    }
}