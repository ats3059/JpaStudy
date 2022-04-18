package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for(Order o : all){
            o.getMember().getName();
            o.getDelivery().getAddress();
            for(OrderItem oi : o.getOrderItems()){
                oi.getItem().getName();
            }
        }
        return all;

    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream().map(OrderDto::new).collect(Collectors.toList());
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        //연관관계에 해당하는 모든 엔티티들을 전부 join fetch 사용하여 끌고옴
        //중복성이 적을때 사용 페이징 불가
        // 일대다 관계에서는 페이징을 사용하면 안된다.
        //중복성 문제도 있지만 DB에서 데이터를 전부 퍼올려서 메모리에서 페이징을 하기 때문이다.
        // 중복성 문제는 distinct 사용하면 JPA에서 primary key기준으로 중복을 제거한다.
        // ★★★★ 동등성 보장
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream().map(OrderDto::new).collect(Collectors.toList());
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_1(){
        // BatchSize 사용하는 방법
        // XToOne 관계든 OneToX 관계든 미리 해당 엔티티 안의 연관관계에 해당하는 엔티티들을 in 쿼리로 퍼올린다.
        // BatchSize 만큼 퍼올리는거임
        //좋은점 : 페이징이 가능하다 , 데이터의 중복성이 높고 데이터의 양이 많을 때 성능을 조금 더 올려줄 가능성이 있다.
        // 사이즈의 수는 걍 실무가서 확인해보자...

        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream().map(OrderDto::new).collect(Collectors.toList());
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimizaiton();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6(){
        return orderQueryRepository.findAllByDto_flat();
    }



    @Getter
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o) {
            orderId = o.getId();
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            name = o.getMember().getName();
            address = o.getDelivery().getAddress();
            orderItems = o.getOrderItems().stream().map(OrderItemDto::new).collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto{

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem item) {
            itemName = item.getItem().getName();
            orderPrice = item.getOrderPrice();
            count = item.getCount();
        }
    }


}
