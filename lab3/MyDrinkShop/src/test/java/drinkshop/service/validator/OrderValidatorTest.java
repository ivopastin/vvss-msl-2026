package drinkshop.service.validator;


import drinkshop.domain.*;
import drinkshop.domain.Order;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderValidator - White-Box Tests (WBT)")
class OrderValidatorTest {

    private OrderValidator validator;
    //
    // produse helper reutilizabile
    private static final Product PROD_VALID   = new Product(1, "Cola", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);
    private static final Product PROD_INVALID = new Product(0, "Cola", 5.0, CategorieBautura.JUICE, TipBautura.BASIC); // id=0 -> invalid

    @BeforeEach
    void setUp() {
        validator = new OrderValidator();
    }

    // =========================================================================
    // STATEMENT COVERAGE (SC)
    // =========================================================================

    @Nested
    @DisplayName("SC - Statement Coverage")
    @Tag("SC")
    class StatementCoverage {

        @Test
        @DisplayName("TC1_SC - Comanda valida: toate instructiunile de succes executate")
        void tc1_sc_valid() {
            Order order = new Order(1, List.of(new OrderItem(PROD_VALID, 2)), 10.0);
            assertDoesNotThrow(() -> validator.validate(order));
        }

        @Test
        @DisplayName("TC2_SC - Comanda invalida: toate instructiunile de eroare executate")
        void tc2_sc_invalid() {
            Order order = new Order(0, List.of(new OrderItem(PROD_INVALID, 0)), -1.0);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("ID comanda invalid!"));
        }
    }

    // =========================================================================
    // DECISION / CONDITION COVERAGE (DC, CC, DCC)
    // =========================================================================

    @Nested
    @DisplayName("DC/CC/DCC - Decision and Condition Coverage")
    @Tag("DC")
    class DecisionConditionCoverage {

        @Test
        @DisplayName("TC3_DC - id <= 0 (D1=TRUE) -> exceptie ID")
        void tc3_dc_id_invalid() {
            Order order = new Order(0, List.of(new OrderItem(PROD_VALID, 1)), 5.0);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("ID comanda invalid!"));
        }

        @Test
        @DisplayName("TC4_DC - items = null (D2=TRUE, null) -> exceptie items")
        void tc4_dc_items_null() {
            Order order = new Order(1, List.of(), 5.0);
            order.setItems(null);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("Comanda fara produse!"));
        }

        @Test
        @DisplayName("TC5_DC - items = [] (D2=TRUE, isEmpty) -> exceptie items")
        void tc5_dc_items_empty() {
            Order order = new Order(1, List.of(), 5.0);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("Comanda fara produse!"));
        }

        @Test
        @DisplayName("TC6_DC - totalPrice < 0 (D5=TRUE) -> exceptie total")
        void tc6_dc_total_negativ() {
            Order order = new Order(1, List.of(new OrderItem(PROD_VALID, 1)), -1.0);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("Total invalid!"));
        }

        @Test
        @DisplayName("TC7_DC - toate deciziile FALSE -> fara exceptie")
        void tc7_dc_toate_false() {
            Order order = new Order(1, List.of(new OrderItem(PROD_VALID, 1)), 5.0);
            assertDoesNotThrow(() -> validator.validate(order));
        }
    }

    // =========================================================================
    // MULTIPLE CONDITION COVERAGE (MCC)
    // conditia compusa: order.getItems() == null || order.getItems().isEmpty()
    // =========================================================================

    @Nested
    @DisplayName("MCC - Multiple Condition Coverage")
    @Tag("MCC")
    class MultipleConditionCoverage {

        @Test
        @DisplayName("TC8_MCC - items=null (null=TRUE, scurt-circuit) -> exceptie")
        void tc8_mcc_null_true() {
            Order order = new Order(1, List.of(), 5.0);
            order.setItems(null);
            assertThrows(ValidationException.class, () -> validator.validate(order));
        }

        @Test
        @DisplayName("TC9_MCC - items!=null, isEmpty=TRUE -> exceptie")
        void tc9_mcc_null_false_empty_true() {
            Order order = new Order(1, List.of(), 5.0);
            assertThrows(ValidationException.class, () -> validator.validate(order));
        }

        @Test
        @DisplayName("TC10_MCC - items!=null, isEmpty=FALSE -> fara exceptie")
        void tc10_mcc_null_false_empty_false() {
            Order order = new Order(1, List.of(new OrderItem(PROD_VALID, 1)), 5.0);
            assertDoesNotThrow(() -> validator.validate(order));
        }
    }

    // =========================================================================
    // ALL PATH COVERAGE (APC) — cele 7 drumuri independente din CFG
    // =========================================================================

    @Nested
    @DisplayName("APC - All Path Coverage")
    @Tag("APC")
    class AllPathCoverage {

        @Test
        @DisplayName("TC11_APC - P1: id>0, items goala, total>=0 -> exceptie (items empty)")
        void tc11_apc_p1() {
            Order order = new Order(1, List.of(), 5.0);
            assertThrows(ValidationException.class, () -> validator.validate(order));
        }

        @Test
        @DisplayName("TC12_APC - P2: id<=0, items goala -> exceptie ID + items")
        void tc12_apc_p2() {
            Order order = new Order(0, List.of(), 5.0);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("ID comanda invalid!"));
            assertTrue(ex.getMessage().contains("Comanda fara produse!"));
        }

        @Test
        @DisplayName("TC13_APC - P3: id>0, items=null -> exceptie items")
        void tc13_apc_p3() {
            Order order = new Order(1, List.of(), 5.0);
            order.setItems(null);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("Comanda fara produse!"));
        }

        @Test
        @DisplayName("TC14_APC - P4: id>0, 1 item valid, total>=0 -> fara exceptie")
        void tc14_apc_p4() {
            Order order = new Order(1, List.of(new OrderItem(PROD_VALID, 1)), 5.0);
            assertDoesNotThrow(() -> validator.validate(order));
        }

        @Test
        @DisplayName("TC15_APC - P5: id>0, 1 item invalid (catch) -> exceptie item")
        void tc15_apc_p5() {
            Order order = new Order(1, List.of(new OrderItem(PROD_INVALID, 0)), 5.0);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("Product ID invalid!"));
        }

        @Test
        @DisplayName("TC16_APC - P6: id>0, items goala, total<0 -> exceptie total")
        void tc16_apc_p6() {
            Order order = new Order(1, List.of(), -1.0);
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> validator.validate(order));
            assertTrue(ex.getMessage().contains("Total invalid!"));
        }

        @Test
        @DisplayName("TC17_APC - P7: id>0, 2 items valide, total>=0 -> fara exceptie")
        void tc17_apc_p7() {
            Order order = new Order(1,
                    List.of(new OrderItem(PROD_VALID, 1), new OrderItem(PROD_VALID, 2)),
                    15.0);
            assertDoesNotThrow(() -> validator.validate(order));
        }
    }

    // =========================================================================
    // SIMPLE LOOP COVERAGE (LC)
    // bucla: for (OrderItem item : order.getItems())
    // =========================================================================

    @Nested
    @DisplayName("LC - Simple Loop Coverage")
    @Tag("LC")
    class LoopCoverage {

        @Test
        @DisplayName("TC18_LC - 0 iteratii: items=[] -> exceptie (items empty)")
        void tc18_lc_zero_iteratii() {
            Order order = new Order(1, List.of(), 5.0);
            assertThrows(ValidationException.class, () -> validator.validate(order));
        }

        @Test
        @DisplayName("TC19_LC - 1 iteratie: items=[1 item valid] -> fara exceptie")
        void tc19_lc_o_iteratie() {
            Order order = new Order(1, List.of(new OrderItem(PROD_VALID, 1)), 5.0);
            assertDoesNotThrow(() -> validator.validate(order));
        }

        @Test
        @DisplayName("TC20_LC - 2 iteratii: items=[2 items valide] -> fara exceptie")
        void tc20_lc_doua_iteratii() {
            Order order = new Order(1,
                    List.of(new OrderItem(PROD_VALID, 1), new OrderItem(PROD_VALID, 3)),
                    20.0);
            assertDoesNotThrow(() -> validator.validate(order));
        }

        @Test
        @DisplayName("TC21_LC - N iteratii: items=[5 items valide] -> fara exceptie")
        void tc21_lc_n_iteratii() {
            Order order = new Order(1,
                    List.of(
                            new OrderItem(PROD_VALID, 1),
                            new OrderItem(PROD_VALID, 2),
                            new OrderItem(PROD_VALID, 3),
                            new OrderItem(PROD_VALID, 4),
                            new OrderItem(PROD_VALID, 5)
                    ),
                    75.0);
            assertDoesNotThrow(() -> validator.validate(order));
        }
    }
}
