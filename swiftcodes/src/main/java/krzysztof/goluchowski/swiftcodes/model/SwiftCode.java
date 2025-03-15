package krzysztof.goluchowski.swiftcodes.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "swift_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwiftCode {
    @Id
    private String swiftCode;
    private String branchCode;
    private boolean isHeadquarter;
    private String bankName;
    private String address;
    private String townName;
    private String countryISO2;
    private String countryName;
    private String timeZone;
}
