package com.madirex.funkosspringrest.rest.entities.order.services;

import com.madirex.funkosspringrest.rest.entities.funko.exceptions.FunkoNotFoundException;
import com.madirex.funkosspringrest.rest.entities.funko.repository.FunkoRepository;
import com.madirex.funkosspringrest.rest.entities.order.exceptions.*;
import com.madirex.funkosspringrest.rest.entities.order.models.Order;
import com.madirex.funkosspringrest.rest.entities.order.models.OrderLine;
import com.madirex.funkosspringrest.rest.entities.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación de la interfaz OrderService
 */
@Service
@Slf4j
@CacheConfig(cacheNames = {"orders"})
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final FunkoRepository funkoRepository;

    /**
     * Constructor de la clase
     *
     * @param orderRepository Repositorio Order
     * @param funkoRepository Repositorio Funko
     */
    public OrderServiceImpl(OrderRepository orderRepository, FunkoRepository funkoRepository) {
        this.orderRepository = orderRepository;
        this.funkoRepository = funkoRepository;
    }

    /**
     * Obtiene todos los pedidos
     *
     * @param pageable Pageable para paginar y ordenar
     * @return Page con los pedidos
     */
    @Override
    public Page<Order> findAll(Pageable pageable) {
        log.info("Obteniendo todos los orders paginados y ordenados con {}", pageable);
        return orderRepository.findAll(pageable);
    }

    /**
     * Obtiene un pedido por su ID
     *
     * @param idOrder id del pedido
     * @return Pedido
     */
    @Override
    @Cacheable(key = "#idOrder")
    public Order findById(ObjectId idOrder) {
        log.info("Obteniendo order con id: " + idOrder);
        return orderRepository.findById(idOrder).orElseThrow(() -> new OrderNotFound(idOrder.toHexString()));
    }

    /**
     * Guarda un pedido
     *
     * @param order pedido
     * @return Pedido guardado
     */
    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public Order save(Order order) {
        log.info("Guardando order: {}", order);
        checkValidOrder(order);
        var orderToSave = reserveStockOrders(order);
        orderToSave.setCreatedAt(LocalDateTime.now());
        orderToSave.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(orderToSave);
    }

    /**
     * Borra un pedido
     *
     * @param idOrder id del pedido
     */
    @Override
    @Transactional
    @CacheEvict(key = "#idOrder")
    public void delete(ObjectId idOrder) {
        log.info("Borrando order: " + idOrder);
        var orderToDelete = orderRepository.findById(idOrder).orElseThrow(() -> new OrderNotFound(idOrder.toHexString()));
        updateStockOrders(orderToDelete);
        orderRepository.deleteById(idOrder);
    }

    /**
     * Actualiza un pedido
     *
     * @param idOrder id del pedido
     * @param order   pedido
     * @return Pedido actualizado
     */
    @Override
    @Transactional
    @CachePut(key = "#idOrder")
    public Order update(ObjectId idOrder, Order order) {
        log.info("Actualizando order con id: " + idOrder);
        orderRepository.findById(idOrder).orElseThrow(() -> new OrderNotFound(idOrder.toHexString()));
        updateStockOrders(order);
        checkValidOrder(order);
        var orderToSave = reserveStockOrders(order);
        orderToSave.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(orderToSave);
    }

    /**
     * Reserva el stock de los pedidos
     *
     * @param order pedido
     * @return pedido
     */
    private Order reserveStockOrders(Order order) {
        log.info("Reservando stock del order: {}", order);
        if (order.getOrderLineList() == null || order.getOrderLineList().isEmpty()) {
            throw new OrderNotItems(order.getId());
        }
        order.getOrderLineList().forEach(orderLine -> {
            var funkoOpt = funkoRepository.findById(orderLine.getProductId());
            if (funkoOpt.isEmpty()) {
                throw new FunkoNotFoundException("id: " + orderLine.getProductId().toString());
            }
            var funko = funkoOpt.get();
            funko.setQuantity(funko.getQuantity() - orderLine.getQuantity());
            funkoRepository.save(funko);
            orderLine.setTotal(orderLine.getQuantity() * orderLine.getProductPrice());
        });
        var total = order.getOrderLineList().stream()
                .map(orderLine -> orderLine.getQuantity() * orderLine.getProductPrice())
                .reduce(0.0, Double::sum);
        var totalItems = order.getOrderLineList().stream()
                .map(OrderLine::getQuantity)
                .reduce(0, Integer::sum);
        order.setTotal(total);
        order.setQuantity(totalItems);
        return order;
    }

    /**
     * Actualiza el stock de los pedidos
     *
     * @param order pedido
     */
    private void updateStockOrders(Order order) {
        log.info("Retornando stock del order: {}", order);
        if (order.getOrderLineList() != null) {
            order.getOrderLineList().forEach(orderLine -> {
                var funkoOpt = funkoRepository.findById(orderLine.getProductId());
                if (funkoOpt.isEmpty()) {
                    throw new FunkoNotFoundException("id: " + orderLine.getProductId().toString());
                }
                var funko = funkoOpt.get();
                funko.setQuantity(funko.getQuantity() + orderLine.getQuantity());
                funkoRepository.save(funko);
            });
        }
    }

    /**
     * Comprueba si el pedido es válido
     *
     * @param order pedido
     */
    private void checkValidOrder(Order order) {
        log.info("Comprobando order: {}", order);
        if (order.getOrderLineList() == null || order.getOrderLineList().isEmpty()) {
            throw new OrderNotItems(order.getId());
        }
        order.getOrderLineList().forEach(orderLine -> {
            var funko = funkoRepository.findById(orderLine.getProductId())
                    .orElseThrow(() -> new ProductNotFound(orderLine.getProductId().toString()));
            if (funko.getQuantity() < orderLine.getQuantity() && orderLine.getQuantity() > 0) {
                throw new ProductNotStock(orderLine.getProductId().toString());
            }
            if (!funko.getPrice().equals(orderLine.getProductPrice())) {
                throw new ProductBadPrice(orderLine.getProductId().toString());
            }
        });
    }
}