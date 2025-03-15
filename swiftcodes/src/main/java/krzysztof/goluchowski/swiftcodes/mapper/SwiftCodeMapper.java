package krzysztof.goluchowski.swiftcodes.mapper;

import krzysztof.goluchowski.swiftcodes.dto.AddSwiftCodeRequestDto;
import krzysztof.goluchowski.swiftcodes.dto.BranchDto;
import krzysztof.goluchowski.swiftcodes.dto.HeadquarterWithBranchesDto;
import krzysztof.goluchowski.swiftcodes.dto.SwiftCodeDto;
import krzysztof.goluchowski.swiftcodes.model.SwiftCode;
import krzysztof.goluchowski.swiftcodes.service.SwiftCodeService;

import java.util.List;
import java.util.stream.Collectors;

public class SwiftCodeMapper {

    public static BranchDto toBranchDto(SwiftCode swiftCode) {
        return new BranchDto(
                swiftCode.getAddress(),
                swiftCode.getBankName(),
                swiftCode.getCountryISO2(),
                swiftCode.getCountryName(),
                swiftCode.isHeadquarter(),
                swiftCode.getSwiftCode()
        );
    }

    public static HeadquarterWithBranchesDto toHeadquarterDto(SwiftCode headquarter, List<SwiftCode> branches) {
        List<BranchDto> branchDtos = branches.stream()
                .map(SwiftCodeMapper::toBranchDto)
                .collect(Collectors.toList());

        return new HeadquarterWithBranchesDto(
                headquarter.getAddress(),
                headquarter.getBankName(),
                headquarter.getCountryISO2(),
                headquarter.getCountryName(),
                headquarter.isHeadquarter(),
                headquarter.getSwiftCode(),
                branchDtos
        );
    }

    public static SwiftCodeDto toSwiftCodeDto(SwiftCode swiftCode) {
        return new SwiftCodeDto(
                swiftCode.getAddress(),
                swiftCode.getBankName(),
                swiftCode.getCountryISO2(),
                swiftCode.isHeadquarter(),
                swiftCode.getSwiftCode()
        );
    }

    public static SwiftCode rowToSwiftCode(String[] row) {
        String swiftCode = row[1].trim().toUpperCase();
        boolean isHeadquarter = swiftCode.endsWith("XXX");
        String branchCode = isHeadquarter ? null : swiftCode.substring(8);

        return new SwiftCode(
                swiftCode,
                branchCode,
                isHeadquarter,
                row[3].trim(),
                row[4].trim(),
                row[5].trim(),
                row[0].trim().toUpperCase(),
                row[6].trim().toUpperCase(),
                row[7].trim()
        );
    }

    public static SwiftCode requestToSwiftCode(AddSwiftCodeRequestDto request) {
        String townName = extractTownName(request.getAddress());

        return new SwiftCode(
                request.getSwiftCode(),
                request.isHeadquarter() ? null : request.getSwiftCode().substring(8),
                request.isHeadquarter(),
                request.getBankName(),
                request.getAddress(),
                townName,
                request.getCountryISO2(),
                request.getCountryName(),
                null
        );
    }

    private static String extractTownName(String address) {
        if (address == null || address.isEmpty()) {
            return null;
        }

        String[] addressParts = address.split(",");

        if (address.length() > 1) {
            return addressParts[1].trim();
        }

        return null;
    }
}
