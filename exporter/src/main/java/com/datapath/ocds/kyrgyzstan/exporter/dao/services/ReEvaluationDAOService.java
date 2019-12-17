package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ContractDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReEvaluationDAOService {

    private static final String CONTRACTS_QUERUY = "select mc.id\n" +
            "from json_event je\n" +
            "  join orders o on o.id = je.object_id\n" +
            "  join materialized_contract mc on mc.order_id = o.id\n" +
            "where je.id = ?";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<ContractDAO> getContracts(Integer jsonEventId) {
        return jdbcTemplate.query(CONTRACTS_QUERUY, new BeanPropertyRowMapper<>(ContractDAO.class), jsonEventId);
    }

}
