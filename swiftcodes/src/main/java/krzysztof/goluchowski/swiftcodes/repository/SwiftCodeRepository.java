package krzysztof.goluchowski.swiftcodes.repository;

import krzysztof.goluchowski.swiftcodes.model.SwiftCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SwiftCodeRepository extends MongoRepository<SwiftCode, String> {
    List<SwiftCode> findByCountryISO2(String countryISO2);
    List<SwiftCode> findBySwiftCodeStartingWith(String branchPrefix);
}
