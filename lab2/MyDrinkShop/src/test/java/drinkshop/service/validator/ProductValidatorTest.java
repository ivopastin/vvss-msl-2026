package drinkshop.service.validator;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductValidator - Teste ECP si BVA pentru 'nume' si 'pret'")
class ProductValidatorTest {

    private ProductValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProductValidator();
    }

    // =========================================================================
    // TESTE ECP (Equivalence Class Partitioning)
    // =========================================================================

    @Nested
    @DisplayName("ECP Tests")
    @Tag("ECP")
    class ECPTests {

        @Test
        @DisplayName("TC1_ECP - Valid: nume non-blank, pret > 0 → succes")
        void tc1_valid_numeNonBlank_pretPositiv() {
            // Arrange
            Product p = new Product(1, "Cola", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            assertDoesNotThrow(() -> validator.validate(p));
        }

        @Test
        @DisplayName("TC2_ECP - Non-valid: nume null → ValidationException")
        void tc2_invalid_numeNull() {
            // Arrange
            Product p = new Product(1, null, 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(p));
            assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
        }

        @Test
        @DisplayName("TC3_ECP - Non-valid: nume blank (\"\") → ValidationException")
        void tc3_invalid_numeBlank() {
            // Arrange
            Product p = new Product(1, "", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(p));
            assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
        }

        @Test
        @DisplayName("TC4_ECP - Non-valid: pret = 0 → ValidationException")
        void tc4_invalid_pretZero() {
            // Arrange
            Product p = new Product(1, "Cola", 0.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(p));
            assertTrue(ex.getMessage().contains("Pret invalid!"));
        }

        @Test
        @DisplayName("TC5_ECP - Non-valid: pret < 0 → ValidationException")
        void tc5_invalid_pretNegativ() {
            // Arrange
            Product p = new Product(1, "Cola", -3.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(p));
            assertTrue(ex.getMessage().contains("Pret invalid!"));
        }
    }

    // =========================================================================
    // TESTE BVA (Boundary Value Analysis)
    // =========================================================================

    @Nested
    @DisplayName("BVA Tests - parametrul 'pret' (limita: pret > 0)")
    @Tag("BVA")
    class BVATestsPret {

        @Test
        @DisplayName("TC1_BVA - Non-valid: pret = 0.0 (exact pe granita) → ValidationException")
        void tc1_bva_pret_exact_zero() {
            // Arrange
            Product p = new Product(1, "Cola", 0.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            assertThrows(ValidationException.class, () -> validator.validate(p));
        }

        @Test
        @DisplayName("TC2_BVA - Non-valid: pret = -0.01 (sub granita) → ValidationException")
        void tc2_bva_pret_sub_zero() {
            // Arrange
            Product p = new Product(1, "Cola", -0.01, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            assertThrows(ValidationException.class, () -> validator.validate(p));
        }

        @Test
        @DisplayName("TC3_BVA - Valid: pret = 0.01 (imediat peste granita) → succes")
        void tc3_bva_pret_peste_zero() {
            // Arrange
            Product p = new Product(1, "Cola", 0.01, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            assertDoesNotThrow(() -> validator.validate(p));
        }

        @Test
        @DisplayName("TC4_BVA - Valid: pret = Double.MAX_VALUE (valoare maxima) → succes")
        void tc4_bva_pret_maxim() {
            // Arrange
            Product p = new Product(1, "Cola", Double.MAX_VALUE, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            assertDoesNotThrow(() -> validator.validate(p));
        }
    }

    @Nested
    @DisplayName("BVA Tests - parametrul 'nume' (limita: nu poate fi blank)")
    @Tag("BVA")
    class BVATestsNume {

        @Test
        @DisplayName("TC5_BVA - Non-valid: nume = \"\" (lungime 0) → ValidationException")
        void tc5_bva_nume_empty() {
            // Arrange
            Product p = new Product(1, "", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            assertThrows(ValidationException.class, () -> validator.validate(p));
        }

        @Test
        @DisplayName("TC6_BVA - Non-valid: nume = \" \" (spatiu, isBlank=true) → ValidationException")
        void tc6_bva_nume_spatiu() {
            // Arrange
            Product p = new Product(1, " ", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            assertThrows(ValidationException.class, () -> validator.validate(p));
        }

        @Test
        @DisplayName("TC7_BVA - Valid: nume = \"A\" (lungime 1, non-blank) → succes")
        void tc7_bva_nume_un_caracter() {
            // Arrange
            Product p = new Product(1, "A", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

            // Act & Assert
            assertDoesNotThrow(() -> validator.validate(p));
        }
    }

    // =========================================================================
    // TEST PARAMETRIZAT BONUS - demonstreaza @ParameterizedTest + @CsvSource
    // =========================================================================

    @ParameterizedTest(name = "Test parametrizat #{index}: nume=''{0}'', pret={1}, valid={2}")
    @CsvSource({
            "Cola,   5.0,  true",
            "Cola,   0.0,  false",
            "Cola,  -1.0,  false",
            "A,      0.01, true"
    })
    @Tag("ECP")
    @DisplayName("ECP parametrizat - combinatii nume/pret")
    void ecp_parametrizat(String nume, double pret, boolean valid) {
        // Arrange
        Product p = new Product(1, nume.trim(), pret, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act & Assert
        if (valid)
            assertDoesNotThrow(() -> validator.validate(p));
        else
            assertThrows(ValidationException.class, () -> validator.validate(p));
    }
}