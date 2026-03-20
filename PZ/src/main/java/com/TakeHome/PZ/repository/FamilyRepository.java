package com.TakeHome.PZ.repository;

import com.TakeHome.PZ.models.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FamilyRepository extends JpaRepository<Family, UUID> {
}