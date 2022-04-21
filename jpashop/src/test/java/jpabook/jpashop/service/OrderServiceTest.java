package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.excption.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
@Rollback(value = false)
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000,10);
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER,getOrder.getStatus());
        assertEquals(1,getOrder.getOrderItems().size());
        assertEquals(10000*orderCount, getOrder.getTotalPrice());
        assertEquals(10 - orderCount , book.getStockQuantity());
    }

    @Test
    public void 주문취소() throws Exception{
        //given
        Member member = createMember();
        Book item = createBook("시골 JPA", 10000,10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(),item.getId(),orderCount);

        //when
        Order order = orderRepository.findOne(orderId);
        order.cancel();




        //then
        assertEquals(OrderStatus.CANCEL,order.getStatus());
        assertEquals(10,item.getStockQuantity());



    }

    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000,10);
        int orderCount = 11;

        //when
        try{
            orderService.order(member.getId(),book.getId(),orderCount);
        }catch (NotEnoughStockException e){
            return;
        }

        //then
        fail("123123");

    }


    @Test
    public void 배치테스트(){

        Member member = createMember("userA", "서울", "1", "1111");
        em.persist(member);

        Book book1 = createBook("JPA1 BOOK", 10000, 100);
        em.persist(book1);

        Book book2 = createBook("JPA2 BOOK", 10000, 100);
        em.persist(book2);

        OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
        OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

        Delivery delivery = createDelivery(member);
        Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
        em.persist(order);

        Order order1 = dbInit2();

        em.flush();
        em.clear();


        List<Order> orders = orderRepository.findAllSome(Arrays.asList(order.getId(),order1.getId()));
        System.out.println("=-============size ================================  " + orders.size());
        for (Order od : orders) {
            od.getOrderItems().forEach(oi -> System.out.println(oi.getOrderPrice()));
        }

    }

    public Order dbInit2(){
        Member member = createMember("userB", "진주", "2", "2222");
        em.persist(member);

        Book book1 = createBook("SPRING1 BOOK", 20000, 200);
        em.persist(book1);

        Book book2 = createBook("SPRING2 BOOK", 40000, 300);
        em.persist(book2);

        OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
        OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

        Delivery delivery = createDelivery(member);
        Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
        em.persist(order);
        return order;
    }


    private Delivery createDelivery(Member member) {
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        return delivery;
    }


    private Member createMember(String name, String city, String street, String zipcode) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
        return member;
    }


    private Book createBook(String name, int orderPrice, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(orderPrice);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);
        return member;
    }





}