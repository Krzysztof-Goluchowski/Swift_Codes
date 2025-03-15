package krzysztof.goluchowski.swiftcodes.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import krzysztof.goluchowski.swiftcodes.dto.*;
import krzysztof.goluchowski.swiftcodes.model.SwiftCode;
import krzysztof.goluchowski.swiftcodes.repository.SwiftCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {
    private final SwiftCodeRepository repository;

    public void importSwiftCodesFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();
            rows.removeFirst(); // Skip cvs header

            for (String[] row : rows) {
                if (row.length < 8) continue;

                SwiftCode swiftCodeObj = getSwiftCode(row);

                if (repository.existsById(swiftCodeObj.getSwiftCode())) continue;

                repository.save(swiftCodeObj);
            }

            System.out.println("Import zakończony!");

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private static SwiftCode getSwiftCode(String[] row) {
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

    public Optional<HeadQuarterWithBranchesDto> getHeadquarterWithBranches(String swiftCode) {
        Optional<SwiftCode> headquarter = repository.findById(swiftCode);

        if (headquarter.isEmpty()) {
            return Optional.empty();
        }

        List<BranchDto> branches = repository.findBySwiftCodeStartingWith(swiftCode.substring(0, 8))
                .stream()
                .filter(branch -> !branch.getSwiftCode().equals(swiftCode))
                .map(branch -> new BranchDto(
                        branch.getAddress(),
                        branch.getBankName(),
                        branch.getCountryISO2(),
                        branch.getCountryName(),
                        branch.isHeadquarter(),
                        branch.getSwiftCode()
                ))
                .collect(Collectors.toList());

        return Optional.of(new HeadQuarterWithBranchesDto(
                headquarter.get().getAddress(),
                headquarter.get().getBankName(),
                headquarter.get().getCountryISO2(),
                headquarter.get().getCountryName(),
                headquarter.get().isHeadquarter(),
                headquarter.get().getSwiftCode(),
                branches
        ));
    }

    public Optional<BranchDto> getBranch(String swiftCode) {
        return repository.findById(swiftCode)
                .map(branch -> new BranchDto(
                        branch.getAddress(),
                        branch.getBankName(),
                        branch.getCountryISO2(),
                        branch.getCountryName(),
                        branch.isHeadquarter(),
                        branch.getSwiftCode()
                ));
    }

    public Optional<CountrySwiftCodesDto> getSwiftCodesByCountry(String countryISO2) {
        List<SwiftCode> swiftCodes = repository.findByCountryISO2IgnoreCase(countryISO2);

        if (swiftCodes.isEmpty()) {
            return Optional.empty();
        }

        String countryName = swiftCodes.get(0).getCountryName();

        List<SwiftCodeDto> swiftCodeDtos = swiftCodes.stream()
                .map(code -> new SwiftCodeDto(
                        code.getAddress(),
                        code.getBankName(),
                        code.getCountryISO2(),
                        code.isHeadquarter(),
                        code.getSwiftCode()
                ))
                .collect(Collectors.toList());

        return Optional.of(new CountrySwiftCodesDto(countryISO2, countryName, swiftCodeDtos));
    }

    public boolean addSwiftCode(AddSwiftCodeRequestDto request) {
        if (repository.existsById(request.getSwiftCode())) {
            return false;
        }

        SwiftCode newSwiftCode = new SwiftCode(
                request.getSwiftCode(),
                request.isHeadquarter() ? null : request.getSwiftCode().substring(8),
                request.isHeadquarter(),
                request.getBankName(),
                request.getAddress(),
                null,
                request.getCountryISO2(),
                request.getCountryName(),
                null
        );

        repository.save(newSwiftCode);
        return true;
    }

    public boolean deleteSwiftCode(String swiftCode) {
        if(!repository.existsById(swiftCode)) {
            return false;
        }

        repository.deleteById(swiftCode);
        return true;
    }
}
