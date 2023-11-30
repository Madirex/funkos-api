package com.madirex.funkosspringrest.rest.entities.order.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Clase OrderLine
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrderLine {
    @Min(value = 1, message = "La cantidad del producto no puede ser negativa o cero")
    @Builder.Default
    private Integer quantity = 1;

    @NotNull(message = "El ID del producto no puede ser nulo")
    private String productId;

    @Min(value = 0, message = "El precio del producto no puede ser negativo")
    @Builder.Default
    @NotNull(message = "El precio del producto no puede ser nulo")
    private Double productPrice = 0.0;

    @Builder.Default
    @NotNull(message = "El total no puede ser nulo")
    private Double total = 0.0;

    /**
     * Setter quantity
     *
     * @param quantity Cantidad
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.total = this.quantity * this.productPrice;
    }

    /**
     * Setter productPrice
     *
     * @param productPrice Precio del producto
     */
    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
        this.total = this.quantity * this.productPrice;
    }

    /**
     * Setter total
     *
     * @param total Total
     */
    public void setTotal(Double total) {
        this.total = total;
    }
}
