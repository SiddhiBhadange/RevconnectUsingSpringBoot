package com.secondproj.revconnect.dto;

public class PostRequestDTO {

    private String content;
    private String hashtags;

    public PostRequestDTO() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }
}