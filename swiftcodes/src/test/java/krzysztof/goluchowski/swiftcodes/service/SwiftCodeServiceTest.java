package krzysztof.goluchowski.swiftcodes.service;

import krzysztof.goluchowski.swiftcodes.dto.AddSwiftCodeRequestDto;
import krzysztof.goluchowski.swiftcodes.dto.HeadquarterWithBranchesDto;
import krzysztof.goluchowski.swiftcodes.model.SwiftCode;
import krzysztof.goluchowski.swiftcodes.repository.SwiftCodeRepository;
import krzysztof.goluchowski.swiftcodes.service.SwiftCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwiftCodeServiceTest {

    @Mock
    private SwiftCodeRepository repository;

    @InjectMocks
    private SwiftCodeService service;

    @Test
    void shouldReturnHeadquarterWithBranchesWhenExists() {
        // Given
        String swiftCode = "TESTBANKXXX";
        SwiftCode headquarter = new SwiftCode("TESTBANKXXX", "TESTBANK", true, "Bank HQ", "PL", "Poland", "Warsaw", "PL", "Europe/Warsaw");
        SwiftCode branch = new SwiftCode("TESTBANK123", "TESTBANK", false, "Branch", "PL", "Poland", "Krakow", "PL", "Europe/Warsaw");

        when(repository.findById(swiftCode)).thenReturn(Optional.of(headquarter));
        when(repository.findBySwiftCodeStartingWith("TESTBANK")).thenReturn(List.of(headquarter, branch));

        // When
        Optional<HeadquarterWithBranchesDto> result = service.getHeadquarterWithBranches(swiftCode);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSwiftCode()).isEqualTo(swiftCode);
        assertThat(result.get().getBranches()).hasSize(1);
    }

    @Test
    void shouldReturnEmptyWhenHeadquarterNotFound() {
        // Given
        String swiftCode = "UNKNOWNXXX";
        when(repository.findById(swiftCode)).thenReturn(Optional.empty());

        // When
        Optional<HeadquarterWithBranchesDto> result = service.getHeadquarterWithBranches(swiftCode);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldAddSwiftCodeWhenNotExists() {
        // Given
        AddSwiftCodeRequestDto request = new AddSwiftCodeRequestDto("Some address, Town, something", "Bank", "PL", "Poland", true, "TESTBANK123");
        when(repository.existsById("TESTBANK123")).thenReturn(false);

        // When
        boolean result = service.addSwiftCode(request);

        // Then
        assertThat(result).isTrue();
        verify(repository, times(1)).save(any(SwiftCode.class));
    }

    @Test
    void shouldNotAddSwiftCodeWhenAlreadyExists() {
        // Given
        AddSwiftCodeRequestDto request = new AddSwiftCodeRequestDto("Some address, Town, something", "Bank", "PL", "Poland", true, "TESTBANK123");
        when(repository.existsById("TESTBANK123")).thenReturn(true);

        // When
        boolean result = service.addSwiftCode(request);

        // Then
        assertThat(result).isFalse();
        verify(repository, never()).save(any(SwiftCode.class));
    }
}
