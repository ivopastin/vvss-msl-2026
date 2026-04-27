package drinkshop.service.validator;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductValidator - Teste ECP si BVA pentru 'nume' si 'pret'")
class ProductValidatorTest {

    private ProductValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProductValidator();
    }

    @Test
    @DisplayName("TC1_ECP - Valid: nume non-blank, pret > 0 → succes")
    void tc1_valid_numeNonBlank_pretPositiv() {
        Product p = new Product(1, "Cola", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);
        assertDoesNotThrow(() -> validator.validate(p));
    }

    @Test
    @DisplayName("TC2_ECP - Non-valid: nume null → ValidationException")
    void tc2_invalid_numeNull() {
        Product p = new Product(1, null, 5.0, CategorieBautura.JUICE, TipBautura.BASIC);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(p));
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
    }

    @Test
    @DisplayName("TC3_ECP - Non-valid: nume blank → ValidationException")
    void tc3_invalid_numeBlank() {
        Product p = new Product(1, "", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(p));
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
    }

    @Test
    @DisplayName("TC4_ECP - Non-valid: pret = 0 → ValidationException")
    void tc4_invalid_pretZero() {
        Product p = new Product(1, "Cola", 0.0, CategorieBautura.JUICE, TipBautura.BASIC);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(p));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
    }

    @Test
    @DisplayName("TC5_ECP - Non-valid: pret < 0 → ValidationException")
    void tc5_invalid_pretNegativ() {
        Product p = new Product(1, "Cola", -3.0, CategorieBautura.JUICE, TipBautura.BASIC);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(p));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
    }

    @Test
    @DisplayName("TC1_BVA - Non-valid: pret = 0.0 (exact pe granita) → ValidationException")
    void tc1_bva_pret_exact_zero() {
        Product p = new Product(1, "Cola", 0.0, CategorieBautura.JUICE, TipBautura.BASIC);
        assertThrows(ValidationException.class, () -> validator.validate(p));
    }

    @Test
    @DisplayName("TC2_BVA - Non-valid: pret = -0.01 (sub granita) → ValidationException")
    void tc2_bva_pret_sub_zero() {
        Product p = new Product(1, "Cola", -0.01, CategorieBautura.JUICE, TipBautura.BASIC);
        assertThrows(ValidationException.class, () -> validator.validate(p));
    }

    @Test
    @DisplayName("TC3_BVA - Valid: pret = 0.01 (imediat peste granita) → succes")
    void tc3_bva_pret_peste_zero() {
        Product p = new Product(1, "Cola", 0.01, CategorieBautura.JUICE, TipBautura.BASIC);
        assertDoesNotThrow(() -> validator.validate(p));
    }

    @Test
    @DisplayName("TC4_BVA - Valid: pret = Double.MAX_VALUE → succes")
    void tc4_bva_pret_maxim() {
        Product p = new Product(1, "Cola", Double.MAX_VALUE, CategorieBautura.JUICE, TipBautura.BASIC);
        assertDoesNotThrow(() -> validator.validate(p));
    }

    @Test
    @DisplayName("TC5_BVA - Non-valid: nume = \"\" (lungime 0) → ValidationException")
    void tc5_bva_nume_empty() {
        Product p = new Product(1, "", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);
        assertThrows(ValidationException.class, () -> validator.validate(p));
    }

    @Test
    @DisplayName("TC6_BVA - Non-valid: nume = \" \" (spatiu) → ValidationException")
    void tc6_bva_nume_spatiu() {
        Product p = new Product(1, " ", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);
        assertThrows(ValidationException.class, () -> validator.validate(p));
    }

    @Test
    @DisplayName("TC7_BVA - Valid: nume = \"A\" (lungime 1) → succes")
    void tc7_bva_nume_un_caracter() {
        Product p = new Product(1, "A", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);
        assertDoesNotThrow(() -> validator.validate(p));
    }
}
