package krzysztof.goluchowski.swiftcodes.controller;

import krzysztof.goluchowski.swiftcodes.dto.AddSwiftCodeRequestDto;
import krzysztof.goluchowski.swiftcodes.dto.BranchDto;
import krzysztof.goluchowski.swiftcodes.dto.CountrySwiftCodesDto;
import krzysztof.goluchowski.swiftcodes.dto.HeadquarterWithBranchesDto;
import krzysztof.goluchowski.swiftcodes.service.SwiftCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("v1/swift-codes")
@RequiredArgsConstructor
public class SwiftCodeController {
    private final SwiftCodeService service;

    @GetMapping("/{swiftCode}")
    public ResponseEntity<?> getSwiftCodeDetails(@PathVariable String swiftCode) {
        boolean isHeadquarter = swiftCode.endsWith("XXX");

        if (isHeadquarter) {
            Optional<HeadquarterWithBranchesDto> headQuarterWithBranches = service.getHeadquarterWithBranches(swiftCode);
            return headQuarterWithBranches
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            Optional<BranchDto> branchDto = service.getBranch(swiftCode);
            return branchDto
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<?> getSwiftCodesByCountry(@PathVariable String countryISO2code) {
        Optional<CountrySwiftCodesDto> response = service.getSwiftCodesByCountry(countryISO2code);
        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addSwiftCode(@RequestBody AddSwiftCodeRequestDto request) {
        boolean isAdded = service.addSwiftCode(request);

        if (isAdded) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "SWIFT code added successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "SWIFT code already exists"));
        }
    }

    @DeleteMapping("/{swiftCode}")
    public ResponseEntity<Map<String, String>> deleteSwiftCode(@PathVariable String swiftCode) {
        boolean isDeleted = service.deleteSwiftCode(swiftCode);

        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "SWIFT code deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "SWIFT code not found"));
        }
    }
}
