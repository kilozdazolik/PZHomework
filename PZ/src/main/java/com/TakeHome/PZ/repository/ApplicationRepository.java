package com.TakeHome.PZ.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TakeHome.PZ.models.Application;

public interface ApplicationRepository extends JpaRepository<Application, String> {
    List<Application> findByUserId(UUID userId);
}