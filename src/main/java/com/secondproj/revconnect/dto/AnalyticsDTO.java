package com.secondproj.revconnect.dto;

public class AnalyticsDTO {

    private long totalPosts;
    private long followers;
    private long following;
    private long connections;
    private long totalNotifications;
    private long unreadNotifications;

    public AnalyticsDTO(long totalPosts,
                        long followers,
                        long following,
                        long connections,
                        long totalNotifications,
                        long unreadNotifications) {

        this.totalPosts = totalPosts;
        this.followers = followers;
        this.following = following;
        this.connections = connections;
        this.totalNotifications = totalNotifications;
        this.unreadNotifications = unreadNotifications;
    }

    public long getTotalPosts() { return totalPosts; }
    public long getFollowers() { return followers; }
    public long getFollowing() { return following; }
    public long getConnections() { return connections; }
    public long getTotalNotifications() { return totalNotifications; }
    public long getUnreadNotifications() { return unreadNotifications; }
}