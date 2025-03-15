package krzysztof.goluchowski.swiftcodes.controller;

import krzysztof.goluchowski.swiftcodes.dto.BranchDto;
import krzysztof.goluchowski.swiftcodes.dto.HeadQuarterWithBranchesDto;
import krzysztof.goluchowski.swiftcodes.service.SwiftCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
            Optional<HeadQuarterWithBranchesDto> headQuarterWithBranches = service.getHeadquarterWithBranches(swiftCode);
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
}
