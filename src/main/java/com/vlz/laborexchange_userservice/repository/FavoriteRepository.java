package com.vlz.laborexchange_userservice.repository;

import com.vlz.laborexchange_userservice.entity.Favorite;
import com.vlz.laborexchange_userservice.entity.FavoriteItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserIdAndItemType(Long userId, FavoriteItemType itemType);

    Optional<Favorite> findByUserIdAndItemIdAndItemType(Long userId, Long itemId, FavoriteItemType itemType);

    boolean existsByUserIdAndItemIdAndItemType(Long userId, Long itemId, FavoriteItemType itemType);

    void deleteByUserIdAndItemIdAndItemType(Long userId, Long itemId, FavoriteItemType itemType);
}
