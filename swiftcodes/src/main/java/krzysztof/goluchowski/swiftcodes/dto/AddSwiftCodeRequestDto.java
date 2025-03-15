package krzysztof.goluchowski.swiftcodes.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSwiftCodeRequestDto {

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotBlank(message = "Bank name cannot be empty")
    private String bankName;

    @NotBlank(message = "Country ISO2 cannot be empty")
    @Size(min = 2, max = 2, message = "Country ISO2 must be exactly 2 letters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Country ISO2 must contain only letters")
    private String countryISO2;

    @NotBlank(message = "Country name cannot be empty")
    private String countryName;

    private boolean isHeadquarter;

    @NotBlank(message = "Swift code cannot be empty")
    @Size(min = 8, max = 11, message = "Swift code must be between 8 and 11 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Swift code can only contain letters and numbers")
    private String swiftCode;
}
