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
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderServiceLevel1RepoIntTest {

    private Order order;                              // E = mock
    private OrderValidator orderValidator;            // V = REAL (deja integrat)
    private Repository<Integer, Order> orderRepo;     // R = REAL (integrat acum)
    private Repository<Integer, Product> productRepo; // REAL (necesar pt FileOrderRepository)
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        order          = mock(Order.class);
        orderValidator = new OrderValidator();
        productRepo    = new FileProductRepository("data/products.txt");
        orderRepo      = new FileOrderRepository("data/orders.txt", productRepo); // <-- REAL
        orderService   = new OrderService(orderRepo, productRepo, orderValidator);
    }

    // --- TEST 1: addOrder() cu comanda valida ---
    @Test
    @org.junit.jupiter.api.Order(1)
    void testAddOrderValid_withRealRepo() {
        Product prodMock = mock(Product.class);
        when(prodMock.getId()).thenReturn(1);

        OrderItem itemMock = mock(OrderItem.class);
        when(itemMock.getProduct()).thenReturn(prodMock);
        when(itemMock.getQuantity()).thenReturn(2);

        when(order.getId()).thenReturn(100);
        when(order.getItems()).thenReturn(List.of(itemMock));
        when(order.getTotalPrice()).thenReturn(10.0);

        int sizeInainte = orderService.getAllOrders().size();

        try {
            orderService.addOrder(order);
        } catch (Exception e) {
            fail("Should not throw exception for valid order: " + e.getMessage());
        }

        // VERIFY: order mock a fost apelat de validatorul si repo-ul real
        verify(order, atLeastOnce()).getId();
        verify(order, atLeastOnce()).getItems();
    }

    // --- TEST 2: addOrder() cu comanda invalida ---
    @Test
    @org.junit.jupiter.api.Order(2)
    void testAddOrderInvalid_withRealRepo() {
        when(order.getId()).thenReturn(0);
        when(order.getItems()).thenReturn(List.of());

        Assertions.assertThrows(ValidationException.class, () -> {
            orderService.addOrder(order);
        });

        verify(order, atLeastOnce()).getId();
    }
}
