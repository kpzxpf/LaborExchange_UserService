package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.AddFavoriteRequest;
import com.vlz.laborexchange_userservice.dto.FavoriteDto;
import com.vlz.laborexchange_userservice.entity.Favorite;
import com.vlz.laborexchange_userservice.entity.FavoriteItemType;
import com.vlz.laborexchange_userservice.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Transactional
    public FavoriteDto add(Long userId, AddFavoriteRequest request) {
        try {
            Favorite saved = favoriteRepository.save(
                    Favorite.builder()
                            .userId(userId)
                            .itemId(request.getItemId())
                            .itemType(request.getItemType())
                            .build()
            );

            log.info("Favorite added: userId={} itemId={} type={}", userId, request.getItemId(), request.getItemType());

            return toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already added to favorites");
        }
    }

    @Transactional
    public void remove(Long userId, Long itemId, FavoriteItemType itemType) {
        if (!favoriteRepository.existsByUserIdAndItemIdAndItemType(userId, itemId, itemType)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found");
        }

        favoriteRepository.deleteByUserIdAndItemIdAndItemType(userId, itemId, itemType);

        log.info("Favorite removed: userId={} itemId={} type={}", userId, itemId, itemType);
    }

    @Transactional(readOnly = true)
    public List<FavoriteDto> getByUserAndType(Long userId, FavoriteItemType itemType) {
        return favoriteRepository.findByUserIdAndItemType(userId, itemType)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long itemId, FavoriteItemType itemType) {
        return favoriteRepository.existsByUserIdAndItemIdAndItemType(userId, itemId, itemType);
    }

    private FavoriteDto toDto(Favorite favorite) {
        return FavoriteDto.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .itemId(favorite.getItemId())
                .itemType(favorite.getItemType())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
