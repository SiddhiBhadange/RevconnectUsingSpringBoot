
package com.secondproj.revconnect.repository;

import com.secondproj.revconnect.model.Follow;
import com.secondproj.revconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollowing(User user);   // followers

    List<Follow> findByFollower(User user);    // following

    long countByFollowing(User user);

    long countByFollower(User user);
}