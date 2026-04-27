package drinkshop.ut.service;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.OrderService;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderServiceMockitoTest {

    private Order order;                           // E = mock
    private Validator<Order> orderValidator;       // V = mock
    private Repository<Integer, Order> orderRepo;  // R = mock
    private Repository<Integer, Product> productRepo; // mock (necesar constructorului)
    private OrderService orderService;             // S = obiectul testat

    @BeforeEach
    void setUp() {
        // cream mock-uri pentru TOATE dependentele
        order       = mock(Order.class);
        orderValidator = mock(Validator.class);
        orderRepo   = mock(Repository.class);
        productRepo = mock(Repository.class);
        // cream obiectul testat cu mock-urile injectate
        orderService = new OrderService(orderRepo, productRepo, orderValidator);
    }

    @AfterEach
    void tearDown() {
        orderService  = null;
        orderRepo     = null;
        productRepo   = null;
        orderValidator = null;
        order         = null;
    }

    // --- TEST 1: getAllOrders() cu assert + verify ---
    @Test
    @org.junit.jupiter.api.Order(1)
    void testGetAllValid() {
        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);

        // WHEN: repo returneaza o lista de 2 comenzi
        when(orderRepo.findAll()).thenReturn(Arrays.asList(order1, order2));

        // ASSERT: verificam rezultatul
        assert 2 == orderService.getAllOrders().size();

        // VERIFY: repo.findAll() apelat o data; validatorul NU e apelat la getAll
        verify(orderRepo, times(1)).findAll();
        verify(orderValidator, never()).validate(order1);
    }

    // --- TEST 2: addOrder() cu comanda invalida --- assert + verify ---
    @Test
    @org.junit.jupiter.api.Order(2)
    void testAddOrderInvalid() {
        // simulam o comanda invalida: id <= 0
        when(order.getId()).thenReturn(-1);
        doThrow(new ValidationException("ID comanda invalid!\n"))
                .when(orderValidator).validate(order);

        // ASSERT: se arunca ValidationException
        try {
            orderService.addOrder(order);
        } catch (Exception e) {
            assert e.getClass().equals(ValidationException.class);
        }

        // VERIFY: validatorul a fost apelat; repo.save() NU a fost apelat
        verify(orderValidator, times(1)).validate(order);
        verify(orderRepo, never()).save(any());
    }

    // --- TEST 3: addOrder() cu comanda valida --- fail + verify ---
    @Test
    @org.junit.jupiter.api.Order(3)
    void testAddOrderValid() {
        // simulam o comanda valida
        doNothing().when(orderValidator).validate(order);
        when(orderRepo.save(order)).thenReturn(order);

        // daca se arunca exceptie, testul pica cu fail
        try {
            orderService.addOrder(order);
        } catch (Exception e) {
            fail("Should not throw exception for valid order");
        }

        // VERIFY: validatorul SI repo.save() au fost apelate cate o data
        verify(orderValidator, times(1)).validate(order);
        verify(orderRepo, times(1)).save(order);
    }

    // --- TEST 4: deleteOrder() --- verify ---
    @Test
    @org.junit.jupiter.api.Order(4)
    void testDeleteOrder() {
        doNothing().when(orderRepo).delete(1);

        orderService.deleteOrder(1);

        // VERIFY: repo.delete() apelat o data cu id=1
        verify(orderRepo, times(1)).delete(1);
        verify(orderValidator, never()).validate(any());
    }
}