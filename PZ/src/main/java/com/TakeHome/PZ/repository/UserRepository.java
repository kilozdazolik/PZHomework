package com.TakeHome.PZ.repository;

import com.TakeHome.PZ.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByNameIgnoreCase(String name);
	long countByFamilyId(UUID familyId);
}