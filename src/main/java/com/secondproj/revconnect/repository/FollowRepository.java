
package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Follow;
import com.secondproj.revconnect.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollowing(User user);   // followers

    List<Follow> findByFollower(User user);    // following

    long countByFollowing(User user);

    long countByFollower(User user);

    @Transactional
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Transactional
    void deleteByFollowerId(Long followerId);

    @Transactional
    void deleteByFollowingId(Long followingId);
    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId")
    List<User> findFollowersByUserId(Long userId);

    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId")
    List<User> findFollowingByUserId(Long userId);
}