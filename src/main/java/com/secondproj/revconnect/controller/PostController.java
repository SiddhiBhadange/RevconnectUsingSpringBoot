package com.secondproj.revconnect.controller;

import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // CREATE POST
    @PostMapping
    public Post createPost(
            @AuthenticationPrincipal User user,
            @RequestParam String content,
            @RequestParam(required = false) String hashtags
    ) {
        return postService.createPost(user, content, hashtags);
    }

    // VIEW MY POSTS
    @GetMapping("/me")
    public List<Post> myPosts(@AuthenticationPrincipal User user) {
        return postService.getMyPosts(user);
    }

    // DELETE POST
    @DeleteMapping("/{postId}")
    public String deletePost(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId
    ) {
        postService.deletePost(postId, user);
        return "Post deleted successfully";
    }
}