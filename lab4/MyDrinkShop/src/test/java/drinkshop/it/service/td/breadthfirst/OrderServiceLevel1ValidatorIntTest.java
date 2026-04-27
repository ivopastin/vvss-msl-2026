
package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileOrderRepository;
import drinkshop.service.OrderService;
import drinkshop.service.validator.OrderValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderServiceLevel1ValidatorIntTest {

    private Order order;                              // E = mock
    private OrderValidator orderValidator;            // V = REAL (integrat)
    private Repository<Integer, Order> orderRepo;     // R = mock
    private Repository<Integer, Product> productRepo; // mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        order         = mock(Order.class);
        orderValidator = new OrderValidator();        // <-- REAL, nu mai e mock
        orderRepo     = mock(Repository.class);
        productRepo   = mock(Repository.class);
        orderService  = new OrderService(orderRepo, productRepo, orderValidator);
    }

    // --- TEST 1: addOrder() cu comanda valida ---
    @Test
    @org.junit.jupiter.api.Order(1)
    void testAddOrderValid_withRealValidator() {
        // simulam Order(id=1, items=[item valid], total=10.0)
        // OrderValidator REAL va apela: order.getId(), order.getItems(), order.getTotalPrice()
        Product prodMock = mock(Product.class);
        when(prodMock.getId()).thenReturn(1);

        OrderItem itemMock = mock(OrderItem.class);
        when(itemMock.getProduct()).thenReturn(prodMock);
        when(itemMock.getQuantity()).thenReturn(2);

        when(order.getId()).thenReturn(1);
        when(order.getItems()).thenReturn(List.of(itemMock));
        when(order.getTotalPrice()).thenReturn(10.0);
        when(orderRepo.save(order)).thenReturn(order);

        try {
            orderService.addOrder(order);
        } catch (Exception e) {
            fail("Should not throw exception for valid order: " + e.getMessage());
        }

        // VERIFY: repo.save() apelat o data; order.getId() apelat de validatorul real
        verify(orderRepo, times(1)).save(order);
        verify(order, atLeastOnce()).getId();
    }

    // --- TEST 2: addOrder() cu comanda invalida (id <= 0) ---
    @Test
    @org.junit.jupiter.api.Order(2)
    void testAddOrderInvalid_withRealValidator() {
        // simulam Order(id=0, ...) — validatorul real va arunca exceptie
        when(order.getId()).thenReturn(0);
        when(order.getItems()).thenReturn(List.of());

        Assertions.assertThrows(ValidationException.class, () -> {
            orderService.addOrder(order);
        });

        // VERIFY: repo.save() NU a fost apelat
        verify(orderRepo, never()).save(any());
        verify(order, atLeastOnce()).getId();
    }
}
