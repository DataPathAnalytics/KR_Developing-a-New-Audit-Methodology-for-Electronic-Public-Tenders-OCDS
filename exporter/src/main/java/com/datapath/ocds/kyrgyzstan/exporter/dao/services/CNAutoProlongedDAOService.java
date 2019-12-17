package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CNAutoProlongedDAOService {

    @Autowired
    private OrderRepository orderRepository;

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

}
