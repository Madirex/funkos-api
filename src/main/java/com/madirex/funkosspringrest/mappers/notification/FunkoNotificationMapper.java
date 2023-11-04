package com.madirex.funkosspringrest.mappers.notification;

import com.madirex.funkosspringrest.dto.funko.GetFunkoDTO;
import com.madirex.funkosspringrest.dto.notification.FunkoNotificationResponse;
import org.springframework.stereotype.Component;

/**
 * FunkoNotificationMapper
 */
@Component
public class FunkoNotificationMapper {
    /**
     * Método que convierte un GetFunkoDTO a un FunkoNotificationResponse
     *
     * @param funko GetFunkoDTO
     * @return FunkoNotificationResponse
     */
    public FunkoNotificationResponse toFunkoNotificationDto(GetFunkoDTO funko) {
        return new FunkoNotificationResponse(
                funko.getId().toString(),
                funko.getName(),
                funko.getPrice(),
                funko.getQuantity(),
                funko.getImage(),
                funko.getCategory().toString(),
                funko.getCreatedAt().toString(),
                funko.getUpdatedAt().toString()
        );
    }
}