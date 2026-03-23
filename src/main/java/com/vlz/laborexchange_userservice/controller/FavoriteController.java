package com.vlz.laborexchange_userservice.controller;

import com.vlz.laborexchange_userservice.dto.AddFavoriteRequest;
import com.vlz.laborexchange_userservice.dto.FavoriteDto;
import com.vlz.laborexchange_userservice.entity.FavoriteItemType;
import com.vlz.laborexchange_userservice.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Favorites", description = "Bookmark vacancies and resumes")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "Add to favorites")
    @PostMapping
    public ResponseEntity<FavoriteDto> add(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid AddFavoriteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.add(userId, request));
    }

    @Operation(summary = "Remove from favorites")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long itemId,
            @RequestParam FavoriteItemType itemType) {
        favoriteService.remove(userId, itemId, itemType);
    }

    @Operation(summary = "Get favorites by type")
    @GetMapping
    public List<FavoriteDto> getByType(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam FavoriteItemType itemType) {
        return favoriteService.getByUserAndType(userId, itemType);
    }

    @Operation(summary = "Check if item is favorited")
    @GetMapping("/check")
    public boolean isFavorite(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long itemId,
            @RequestParam FavoriteItemType itemType) {
        return favoriteService.isFavorite(userId, itemId, itemType);
    }
}
