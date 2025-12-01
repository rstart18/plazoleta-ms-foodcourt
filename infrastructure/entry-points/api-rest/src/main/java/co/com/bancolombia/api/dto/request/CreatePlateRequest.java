package co.com.bancolombia.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlateRequest {

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String description;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Size(max = 200, message = "La URL de la imagen no puede exceder 200 caracteres")
    @Pattern(
            regexp = "^https?://.*\\.(jpg|jpeg|png|gif|webp)$",
            message = "La URL debe ser válida y terminar en .jpg, .jpeg, .png, .gif o .webp"
    )
    private String urlImage;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no puede exceder 100 caracteres")
    private String category;

    @NotNull(message = "El ID del restaurante es obligatorio")
    @Positive(message = "El ID del restaurante debe ser un número positivo")
    private Long restaurantId;

    private Boolean active = true;
}

