package krzysztof.goluchowski.swiftcodes.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import krzysztof.goluchowski.swiftcodes.model.SwiftCode;
import krzysztof.goluchowski.swiftcodes.repository.SwiftCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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
}
