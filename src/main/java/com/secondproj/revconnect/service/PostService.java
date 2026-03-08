package com.secondproj.revconnect.service;

import com.secondproj.revconnect.model.Post;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.CommentRepository;
import com.secondproj.revconnect.repository.LikeRepository;
import com.secondproj.revconnect.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private CommentRepository  commentRepository;

    public Post createPost(User user, String content, String hashtags) {

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setHashtags(hashtags);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public List<Post> getMyPosts(User user) {
        return postRepository.findByUser(user);
    }

    @Transactional
    public void deletePost(Long postId, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can delete only your post");
        }

        // 🔥 DELETE CHILDREN FIRST
        likeRepository.deleteByPost(post);
        commentRepository.deleteByPost(post);

        // THEN DELETE POST
        postRepository.delete(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.getPostsByUserId(userId);
    }
}