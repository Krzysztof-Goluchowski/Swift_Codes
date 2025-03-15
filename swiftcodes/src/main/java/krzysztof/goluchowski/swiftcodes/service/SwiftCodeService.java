package krzysztof.goluchowski.swiftcodes.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import krzysztof.goluchowski.swiftcodes.dto.*;
import krzysztof.goluchowski.swiftcodes.mapper.SwiftCodeMapper;
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

                SwiftCode swiftCodeObj = SwiftCodeMapper.rowToSwiftCode(row);

                if (repository.existsById(swiftCodeObj.getSwiftCode())) continue;

                repository.save(swiftCodeObj);
            }

            System.out.println("Import zakończony!");

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public Optional<HeadquarterWithBranchesDto> getHeadquarterWithBranches(String swiftCode) {
        Optional<SwiftCode> headquarter = repository.findById(swiftCode);

        if (headquarter.isEmpty()) {
            return Optional.empty();
        }

        List<SwiftCode> branches = repository.findBySwiftCodeStartingWith(swiftCode.substring(0, 8))
                .stream()
                .filter(branch -> !branch.getSwiftCode().equals(swiftCode))
                .collect(Collectors.toList());

        return Optional.of(SwiftCodeMapper.toHeadquarterDto(headquarter.get(), branches));
    }

    public Optional<BranchDto> getBranch(String swiftCode) {
        return repository.findById(swiftCode)
                .map(SwiftCodeMapper::toBranchDto);
    }

    public Optional<CountrySwiftCodesDto> getSwiftCodesByCountry(String countryISO2) {
        List<SwiftCode> swiftCodes = repository.findByCountryISO2IgnoreCase(countryISO2);

        if (swiftCodes.isEmpty()) {
            return Optional.empty();
        }

        String countryName = swiftCodes.get(0).getCountryName();

        List<SwiftCodeDto> swiftCodeDtos = swiftCodes.stream()
                .map(SwiftCodeMapper::toSwiftCodeDto)
                .collect(Collectors.toList());

        return Optional.of(new CountrySwiftCodesDto(countryISO2, countryName, swiftCodeDtos));
    }

    public boolean addSwiftCode(AddSwiftCodeRequestDto request) {
        if (repository.existsById(request.getSwiftCode().toUpperCase())) {
            return false;
        }

        SwiftCode newSwiftCode = SwiftCodeMapper.requestToSwiftCode(request);

        String timeZone = getTimeZoneForTown(newSwiftCode.getTownName());
        newSwiftCode.setTimeZone(timeZone);

        repository.save(newSwiftCode);
        return true;
    }

    private String getTimeZoneForTown(String townName) {
        if (townName == null) {
            return null;
        }

        List<SwiftCode> swiftCodes = repository.findByTownName(townName);

        for (SwiftCode swiftCode : swiftCodes) {
            if (swiftCode.getTimeZone() != null) {
                return swiftCode.getTimeZone();
            }
        }

        return null;
    }

    public boolean deleteSwiftCode(String swiftCode) {
        if(!repository.existsById(swiftCode)) {
            return false;
        }

        repository.deleteById(swiftCode);
        return true;
    }
}
