package com.secondproj.revconnect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String content;

    private String hashtags;
    private boolean pinned;
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int likeCount = 0;

    // =========================
    // RELATIONSHIPS
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Like> likes = new ArrayList<>();

    // =========================
    // CONSTRUCTORS
    // =========================

    public Post() {}

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getHashtags() { return hashtags; }

    public void setHashtags(String hashtags) { this.hashtags = hashtags; }

    public boolean isPinned() { return pinned; }

    public void setPinned(boolean pinned) { this.pinned = pinned; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getLikeCount() { return likeCount; }

    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public List<Comment> getComments() { return comments; }

    public List<Like> getLikes() { return likes; }
}