package org.izouir.crypto_tracker_bot.repository;

import org.izouir.crypto_tracker_bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
