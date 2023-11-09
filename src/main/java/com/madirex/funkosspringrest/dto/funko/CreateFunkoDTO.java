package com.madirex.funkosspringrest.dto.funko;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * Class CreateFunkoDTO
 */
@Getter
@Builder
public class CreateFunkoDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    @Min(value = 0, message = "El precio no puede estar en negativo")
    @NotNull(message = "price no puede ser nulo")
    private Double price;
    @Min(value = 0, message = "La cantidad no puede estar en negativo")
    @NotNull(message = "quantity no puede ser nulo")
    private Integer quantity;
    @NotBlank(message = "La imagen no puede estar vacía")
    private String image;
    @NotNull(message = "La categoría no puede estar vacía")
    private Long categoryId;
}