package com.innowise.orderservice.service;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.exception.OrderStatusException;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.model.dto.CustomerDto;
import com.innowise.orderservice.model.dto.OrderItemResponseDto;
import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.dto.OrderResponseDto;
import com.innowise.orderservice.model.dto.mapper.OrderItemMapperImpl;
import com.innowise.orderservice.model.dto.mapper.OrderMapperImpl;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.enums.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.impl.OrderServiceImpl;
import com.innowise.orderservice.util.DtoBuilder;
import com.innowise.orderservice.util.TestConstant;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OrderServiceImpl.class, OrderMapperImpl.class, OrderItemMapperImpl.class})
class OrderServiceTest {
    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @Autowired
    private OrderService orderService;

    private static Order testOrder;
    private static CustomerDto testCustomer;

    @BeforeAll
    static void beforeAll() {
        testOrder = DtoBuilder.buildOrder();
        testOrder.setOrderItems(List.of(DtoBuilder.buildOrderItem()));

        testCustomer = DtoBuilder.buildCorrectCustomer();
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void getOrderByIdWhenUserIsOwnerTest() {
        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(testOrder));
        when(userServiceClient.getUserById(TestConstant.LONG_ID)).thenReturn(testCustomer);

        OrderResponseDto resultDto = orderService.getOrderById(TestConstant.LONG_ID, TestConstant.LONG_ID);

        assertOrderResponseDtoFields(resultDto, testCustomer);

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
        verify(userServiceClient, times(1)).getUserById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void getOrderByIdWhenUserIsNotOwnerTest() {
        Order otherUserOrder = Order.builder()
                .userId(2L)
                .build();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(otherUserOrder));

        assertThatThrownBy(() -> orderService.getOrderById(TestConstant.LONG_ID, TestConstant.LONG_ID))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You don't have permission to access this order");

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void getOrderByIdWhenUserIsAdminTest() {
        Long adminId = 2L;

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(testOrder));
        when(userServiceClient.getUserById(adminId)).thenReturn(testCustomer);

        OrderResponseDto resultDto = orderService.getOrderById(TestConstant.LONG_ID, adminId);

        assertOrderResponseDtoFields(resultDto, testCustomer);

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
        verify(userServiceClient, times(1)).getUserById(adminId);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void getOrderByIdWhenUserServiceUnavailableTest() {
        CustomerDto failedCustomer = DtoBuilder.buildFailedCustomer();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(testOrder));
        when(userServiceClient.getUserById(TestConstant.LONG_ID)).thenThrow(FeignException.class);

        OrderResponseDto resultDto = orderService.getOrderById(TestConstant.LONG_ID, TestConstant.LONG_ID);

        assertOrderResponseDtoFields(resultDto, failedCustomer);

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
        verify(userServiceClient, times(1)).getUserById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void getOrderByIdWhenOrderDoesNotExistTest() {
        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(TestConstant.LONG_ID, TestConstant.LONG_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found")
                .hasMessageContaining(String.valueOf(TestConstant.LONG_ID));

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void getOrdersByIdsWhenOrderExistsTest() {
        when(orderRepository.findOrdersByIdIn(TestConstant.LONG_IDS)).thenReturn(List.of(testOrder));
        when(userServiceClient.getUsersByIds(TestConstant.LONG_IDS)).thenReturn(List.of(testCustomer));

        List<OrderResponseDto> resultList = orderService.getOrdersByIds(TestConstant.LONG_IDS);

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        OrderResponseDto resultDto = resultList.get(0);

        assertOrderResponseDtoFields(resultDto, testCustomer);

        verify(orderRepository, times(1)).findOrdersByIdIn(TestConstant.LONG_IDS);
        verify(userServiceClient, times(1)).getUsersByIds(TestConstant.LONG_IDS);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void getOrdersByIdsWhenOrdersDoesNotExistTest() {
        when(orderRepository.findOrdersByIdIn(TestConstant.LONG_IDS)).thenReturn(Collections.emptyList());

        List<OrderResponseDto> resultList = orderService.getOrdersByIds(TestConstant.LONG_IDS);

        assertThat(resultList).isNotNull().isEmpty();

        verify(orderRepository, times(1)).findOrdersByIdIn(TestConstant.LONG_IDS);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void getOrdersByIdsWhenUserServiceUnavailableTest() {
        CustomerDto failedCustomer = DtoBuilder.buildFailedCustomer();

        when(orderRepository.findOrdersByIdIn(TestConstant.LONG_IDS)).thenReturn(List.of(testOrder));
        when(userServiceClient.getUsersByIds(TestConstant.LONG_IDS)).thenThrow(FeignException.class);

        List<OrderResponseDto> resultList = orderService.getOrdersByIds(TestConstant.LONG_IDS);

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        OrderResponseDto resultDto = resultList.get(0);

        assertOrderResponseDtoFields(resultDto, failedCustomer);

        verify(orderRepository, times(1)).findOrdersByIdIn(TestConstant.LONG_IDS);
        verify(userServiceClient, times(1)).getUsersByIds(TestConstant.LONG_IDS);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void getOrdersByStatusesWhenOrderExistsTest() {
        List<OrderStatus> requestStatuses = List.of(OrderStatus.NEW);

        when(orderRepository.findOrdersByStatusIn(requestStatuses)).thenReturn(List.of(testOrder));
        when(userServiceClient.getUsersByIds(TestConstant.LONG_IDS)).thenReturn(List.of(testCustomer));

        List<OrderResponseDto> resultList = orderService.getOrdersByStatuses(requestStatuses);

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        OrderResponseDto resultDto = resultList.get(0);

        assertOrderResponseDtoFields(resultDto, testCustomer);

        verify(orderRepository, times(1)).findOrdersByStatusIn(requestStatuses);
        verify(userServiceClient, times(1)).getUsersByIds(TestConstant.LONG_IDS);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void getOrdersByStatusesWhenOrdersDoesNotExistTest() {
        List<OrderStatus> requestStatuses = List.of(OrderStatus.COMPLETED);

        when(orderRepository.findOrdersByStatusIn(requestStatuses)).thenReturn(Collections.emptyList());

        List<OrderResponseDto> resultList = orderService.getOrdersByStatuses(requestStatuses);

        assertThat(resultList).isNotNull().isEmpty();

        verify(orderRepository, times(1)).findOrdersByStatusIn(requestStatuses);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void getOrdersByStatusesWhenUserServiceUnavailableTest() {
        List<OrderStatus> requestStatuses = List.of(OrderStatus.PROCESSING);
        CustomerDto failedCustomer = DtoBuilder.buildFailedCustomer();

        when(orderRepository.findOrdersByStatusIn(requestStatuses)).thenReturn(List.of(testOrder));
        when(userServiceClient.getUsersByIds(TestConstant.LONG_IDS)).thenThrow(FeignException.class);

        List<OrderResponseDto> resultList = orderService.getOrdersByStatuses(requestStatuses);

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        OrderResponseDto resultDto = resultList.get(0);

        assertOrderResponseDtoFields(resultDto, failedCustomer);

        verify(orderRepository, times(1)).findOrdersByStatusIn(requestStatuses);
        verify(userServiceClient, times(1)).getUsersByIds(TestConstant.LONG_IDS);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void createOrderSuccessfulTest() {
        OrderRequestDto requestDto = DtoBuilder.buildOrderRequestDto();
        Item itemInDb = DtoBuilder.buildItem();

        when(itemRepository.findItemById(TestConstant.INTEGER_ID)).thenReturn(Optional.of(itemInDb));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(userServiceClient.getUserById(TestConstant.LONG_ID)).thenReturn(testCustomer);

        OrderResponseDto resultDto = orderService.createOrder(TestConstant.LONG_ID, requestDto);

        assertOrderResponseDtoFields(resultDto, testCustomer);

        verify(itemRepository, times(1)).findItemById(TestConstant.INTEGER_ID);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(userServiceClient, times(1)).getUserById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void createOrderWhenItemNotFoundTest() {
        OrderRequestDto requestDto = DtoBuilder.buildOrderRequestDto();

        when(itemRepository.findItemById(TestConstant.INTEGER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(TestConstant.LONG_ID, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found")
                .hasMessageContaining(String.valueOf(TestConstant.INTEGER_ID));

        verify(itemRepository, times(1)).findItemById(TestConstant.INTEGER_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void updateOrderWithNewItemTest() {
        Order orderInDb = Order.builder()
                .userId(TestConstant.LONG_ID)
                .orderItems(new ArrayList<>())
                .build();
        orderInDb.getOrderItems().add(OrderItem.builder().build());
        Item itemInDb = DtoBuilder.buildItem();
        OrderRequestDto requestDto = DtoBuilder.buildOrderRequestDto();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(orderInDb));
        when(itemRepository.findItemById(TestConstant.INTEGER_ID)).thenReturn(Optional.of(itemInDb));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(userServiceClient.getUserById(TestConstant.LONG_ID)).thenReturn(testCustomer);

        OrderResponseDto resultDto = orderService.updateOrder(TestConstant.LONG_ID, requestDto, TestConstant.LONG_ID);

        assertOrderResponseDtoFields(resultDto, testCustomer);

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
        verify(itemRepository, times(1)).findItemById(TestConstant.INTEGER_ID);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(userServiceClient, times(1)).getUserById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void updateOrderWhenOrderDoesNotExistTest() {
        OrderRequestDto requestDto = DtoBuilder.buildOrderRequestDto();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(TestConstant.LONG_ID, requestDto, TestConstant.LONG_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found")
                .hasMessageContaining(String.valueOf(TestConstant.LONG_ID));

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void updateOrderWhenUserIsNotOwnerTest() {
        Order otherUserOrder = Order.builder()
                .userId(2L)
                .build();
        OrderRequestDto requestDto = DtoBuilder.buildOrderRequestDto();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(otherUserOrder));

        assertThatThrownBy(() -> orderService.updateOrder(TestConstant.LONG_ID, requestDto, TestConstant.LONG_ID))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You don't have permission to access this order");

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void updateOrderWhenItemNotFoundTest() {
        Order orderInDb = Order.builder()
                .userId(TestConstant.LONG_ID)
                .orderItems(new ArrayList<>())
                .build();
        orderInDb.getOrderItems().add(OrderItem.builder().build());
        OrderRequestDto requestDto = DtoBuilder.buildOrderRequestDto();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(orderInDb));
        when(itemRepository.findItemById(TestConstant.INTEGER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(TestConstant.LONG_ID, requestDto, TestConstant.LONG_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found")
                .hasMessageContaining(String.valueOf(TestConstant.INTEGER_ID));

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
        verify(itemRepository, times(1)).findItemById(TestConstant.INTEGER_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void deleteOrderAsUserWhenUserIsOwnerTest() {
        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(testOrder));

        orderService.cancelOrDeleteOrder(TestConstant.LONG_ID, TestConstant.LONG_ID);

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
        verify(orderRepository, times(1)).cancelOrderAsUser(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void deleteOrderAsUserWhenUserIsNotOwnerTest() {
        Order otherUserOrder = Order.builder()
                .userId(2L)
                .build();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(otherUserOrder));

        assertThatThrownBy(() -> orderService.cancelOrDeleteOrder(TestConstant.LONG_ID, TestConstant.LONG_ID))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You don't have permission to access this order");

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void deleteOrderAsUserWhenOrderIsAlreadyCancelledTest() {
        Order cancelledOrder = Order.builder()
                .userId(TestConstant.LONG_ID)
                .status(OrderStatus.CANCELLED)
                .build();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(cancelledOrder));

        assertThatThrownBy(() -> orderService.cancelOrDeleteOrder(TestConstant.LONG_ID, TestConstant.LONG_ID))
                .isInstanceOf(OrderStatusException.class)
                .hasMessageMatching("Order with id \\d+ is already cancelled")
                .hasMessageContaining(String.valueOf(TestConstant.LONG_ID));

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void deleteOrderAsUserWhenOrderIsCompletedTest() {
        Order completedOrder = Order.builder()
                .userId(TestConstant.LONG_ID)
                .status(OrderStatus.COMPLETED)
                .build();

        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(completedOrder));

        assertThatThrownBy(() -> orderService.cancelOrDeleteOrder(TestConstant.LONG_ID, TestConstant.LONG_ID))
                .isInstanceOf(OrderStatusException.class)
                .hasMessageContaining("Cannot cancel completed order with id")
                .hasMessageContaining(String.valueOf(TestConstant.LONG_ID));

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_USER)
    void deleteOrderWhenOrderDoesNotExistTest() {
        when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrDeleteOrder(TestConstant.LONG_ID, TestConstant.LONG_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found")
                .hasMessageContaining(String.valueOf(TestConstant.LONG_ID));

        verify(orderRepository, times(1)).findOrderById(TestConstant.LONG_ID);
    }

    @Test
    @WithMockUser(roles = TestConstant.ROLE_ADMIN)
    void deleteOrdersAsAdminSuccessfulTest() {
        Long adminId = 2L;

        Order cancelledOrder = Order.builder()
                .userId(TestConstant.LONG_ID)
                .status(OrderStatus.CANCELLED)
                .build();
        Order completedOrder = Order.builder()
                .userId(TestConstant.LONG_ID)
                .status(OrderStatus.COMPLETED)
                .build();
        List<Order> ordersToDelete = List.of(testOrder, cancelledOrder, completedOrder);

        for (Order order : ordersToDelete) {
            when(orderRepository.findOrderById(TestConstant.LONG_ID)).thenReturn(Optional.of(order));

            orderService.cancelOrDeleteOrder(TestConstant.LONG_ID, adminId);
        }

        verify(orderRepository, times(3)).findOrderById(TestConstant.LONG_ID);
        verify(orderRepository, times(3)).deleteOrderAsAdmin(TestConstant.LONG_ID);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(orderRepository, itemRepository, userServiceClient);
    }

    private void assertOrderResponseDtoFields(OrderResponseDto responseDto, CustomerDto expectedCustomer) {
        assertAll(
                () -> assertThat(responseDto).isNotNull(),
                () -> assertThat(responseDto.getCustomer()).isNotNull().isEqualTo(expectedCustomer),
                () -> assertThat(responseDto.getStatus()).isNotNull().isEqualTo(OrderStatus.NEW)
        );

        List<OrderItemResponseDto> receivedItems = responseDto.getItems();

        assertAll(
                () -> assertThat(receivedItems).isNotNull().isNotEmpty().hasSize(1),
                () -> assertThat(receivedItems.get(0).getPrice()).isNotNull().isEqualTo(BigDecimal.TEN),
                () -> assertThat(receivedItems.get(0).getQuantity()).isNotNull().isEqualTo(TestConstant.ITEM_QUANTITY)
        );

        BigDecimal expectedTotalPrice = receivedItems.get(0).getPrice()
                .multiply(BigDecimal.valueOf(receivedItems.get(0).getQuantity()));

        assertThat(responseDto.getTotalPrice()).isNotNull().isEqualTo(expectedTotalPrice);
    }
}