package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileOrderRepository;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.OrderService;
import drinkshop.service.validator.OrderValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderServiceIntTest {

    private OrderValidator orderValidator;            // V = REAL
    private Repository<Integer, Order> orderRepo;     // R = REAL
    private Repository<Integer, Product> productRepo; // REAL
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderValidator = new OrderValidator();
        productRepo    = new FileProductRepository("data/products.txt");
        orderRepo      = new FileOrderRepository("data/orders.txt", productRepo);
        orderService   = new OrderService(orderRepo, productRepo, orderValidator);
    }

    // --- TEST 1: addOrder() cu comanda valida (Order REAL) ---
    @Test
    @org.junit.jupiter.api.Order(1)
    void testAddOrderValid_withRealOrder() {
        // construim un produs real din repo
        Product prod = productRepo.findAll().get(0);
        OrderItem item = new OrderItem(prod, 2);
        Order order = new Order(999, List.of(item), prod.getPret() * 2);

        try {
            orderService.addOrder(order);
        } catch (Exception e) {
            fail("Should not throw exception for valid order: " + e.getMessage());
        }

        assert orderService.getAllOrders().stream()
                .anyMatch(o -> o.getId() == 999);
    }

    // --- TEST 2: addOrder() cu comanda invalida (Order REAL, id <= 0) ---
    @Test
    @org.junit.jupiter.api.Order(2)
    void testAddOrderInvalid_withRealOrder() {
        Order order = new Order(0); // id = 0, lista goala → invalid

        Assertions.assertThrows(ValidationException.class, () -> {
            orderService.addOrder(order);
        });
    }
}
