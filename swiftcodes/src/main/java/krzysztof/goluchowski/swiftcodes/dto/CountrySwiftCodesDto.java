package krzysztof.goluchowski.swiftcodes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CountrySwiftCodesDto {
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeDto> swiftCodes;
}
