package com.vlz.laborexchange_userservice.dto;

import com.vlz.laborexchange_userservice.entity.FavoriteItemType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteDto {
    private Long id;
    private Long userId;
    private Long itemId;
    private FavoriteItemType itemType;
    private LocalDateTime createdAt;
}
