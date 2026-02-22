package com.secondproj.revconnect.service;

import com.secondproj.revconnect.model.Follow;
import com.secondproj.revconnect.model.User;
import com.secondproj.revconnect.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    public void follow(User follower, User following) {

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            return;
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        followRepository.save(follow);
    }

    public void unfollow(User follower, User following) {

        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(followRepository::delete);
    }

    public long getFollowersCount(User user) {
        return followRepository.countByFollowing(user);
    }

    public long getFollowingCount(User user) {
        return followRepository.countByFollower(user);
    }

    public List<Follow> getFollowers(User user) {
        return followRepository.findByFollowing(user);
    }

    public List<Follow> getFollowing(User user) {
        return followRepository.findByFollower(user);
    }

    public boolean isFollowing(User follower, User following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
}