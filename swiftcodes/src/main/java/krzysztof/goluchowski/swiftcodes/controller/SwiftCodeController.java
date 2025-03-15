package krzysztof.goluchowski.swiftcodes.controller;

import krzysztof.goluchowski.swiftcodes.service.SwiftCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swift")
@RequiredArgsConstructor
public class SwiftCodeController {
    private final SwiftCodeService service;

    @PostMapping("/import")
    public String importCsv() {
        service.importSwiftCodesFromCsv("src/main/resources/swift_codes.csv");
        return "Import zakończony!";
    }
}
