package com.madirex.funkosspringrest.rest.category.dto;


import com.madirex.funkosspringrest.rest.category.models.Category;
import lombok.Builder;
import lombok.Getter;

/**
 * Class PatchFunkoDTO
 */
@Getter
@Builder
public class PatchCategoryDTO {
    private Category.Type type;

    private Boolean active;
}