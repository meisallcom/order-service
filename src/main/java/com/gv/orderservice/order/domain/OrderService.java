package com.gv.orderservice.order.domain;

import com.gv.orderservice.book.Book;
import com.gv.orderservice.book.BookClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final BookClient bookClient;
    private final OrderRepository orderRepository;
    public OrderService(
            BookClient bookClient, OrderRepository orderRepository
    ){
        this.bookClient = bookClient;
        this.orderRepository = orderRepository;
    }

    public Flux<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(String isbn, int quantity){
        return bookClient.getBookByIsbn(isbn)
                .map(book -> buildAcceptOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity)).flatMap(orderRepository::save);
            //return Mono.just(buildRejectedOrder(isbn,quantity)).flatMap(orderRepository::save);
    }

    public static Order buildAcceptOrder(Book book, int quantity) {
        return Order.of(book.isbn(),book.title()+" - "+book.author(),book.price(),quantity,OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(
            String bookIsbn, int quantity
    ) {
        return Order.of(bookIsbn,null, null, quantity, OrderStatus.REJECTED);
    }
}
