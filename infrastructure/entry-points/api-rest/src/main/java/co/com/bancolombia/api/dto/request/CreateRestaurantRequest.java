package co.com.bancolombia.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantRequest {

    @NotBlank(message = "El nombre del restaurante es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Pattern(
            regexp = "^(?!^[0-9]+$).*[a-zA-Z].*$",
            message = "El nombre debe contener al menos una letra, no puede ser solo números"
    )
    private String name;

    @NotBlank(message = "El NIT es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El NIT debe contener únicamente números")
    private String nit;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^\\+?[0-9]{1,13}$",
            message = "El teléfono debe ser numérico, máximo 13 caracteres y puede contener el símbolo +"
    )
    @Size(max = 13, message = "El teléfono no puede exceder 13 caracteres")
    private String phone;

    private String urlLogo;

    @NotNull(message = "El ID del propietario es obligatorio")
    private Long ownerId;
}
