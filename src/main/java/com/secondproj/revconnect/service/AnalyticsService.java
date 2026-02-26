package com.secondproj.revconnect.service;

import com.secondproj.revconnect.dto.AnalyticsDTO;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    @Autowired private PostRepository postRepository;
    @Autowired private FollowRepository followRepository;
    @Autowired private ConnectionRepository connectionRepository;
    @Autowired private NotificationRepository notificationRepository;

    public AnalyticsDTO getUserAnalytics(User user) {

        long totalPosts = postRepository.countByUser(user);
        long followers = followRepository.countByFollowing(user);
        long following = followRepository.countByFollower(user);
        long connections = connectionRepository
                .countByReceiverIdAndStatus(user.getId(), "ACCEPTED");
        long totalNotifications = notificationRepository.countByUser(user);
        long unreadNotifications = notificationRepository.countByUserAndReadFalse(user);

        return new AnalyticsDTO(
                totalPosts,
                followers,
                following,
                connections,
                totalNotifications,
                unreadNotifications
        );
    }
}