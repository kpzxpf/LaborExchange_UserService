package com.vlz.laborexchange_userservice.dto;

import com.vlz.laborexchange_userservice.entity.FavoriteItemType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddFavoriteRequest {
    @NotNull
    private Long itemId;
    @NotNull
    private FavoriteItemType itemType;
}
