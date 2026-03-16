package com.TakeHome.PZ.controllers;

import com.TakeHome.PZ.dto.FamilyMemberRequestDTO;
import com.TakeHome.PZ.models.Family;
import com.TakeHome.PZ.services.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/families")
public class FamilyController {
	private final FamilyService familyService;

	@Autowired
	public FamilyController(FamilyService familyService) {
		this.familyService = familyService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Family> getFamilyById(@PathVariable UUID id) {
		return familyService.findById(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Family> createFamily(@RequestParam String name) {
		Family created = familyService.saveFamily(name);
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFamily(@PathVariable UUID id) {
		familyService.deleteFamily(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{familyId}/members")
	public ResponseEntity<Family> addUserToFamily(@PathVariable UUID familyId, @RequestBody FamilyMemberRequestDTO request) {
		Family updatedFamily = familyService.addUserToFamily(familyId, request.getUserId());
		return new ResponseEntity<>(updatedFamily, HttpStatus.CREATED);
	}

	@DeleteMapping("/{familyId}/members/{userId}")
	public ResponseEntity<Void> removeUserFromFamily(@PathVariable UUID familyId, @PathVariable UUID userId) {
		familyService.removeUserFromFamily(familyId, userId);
		return ResponseEntity.noContent().build();
	}
}
