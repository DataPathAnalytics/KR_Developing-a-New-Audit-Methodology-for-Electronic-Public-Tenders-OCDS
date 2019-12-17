package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.EnquiryDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnquiryAnsweredDAOService {

    public static final String ENQUIRIES_QUERY = "SELECT\n" +
            "  ex.id         id,\n" +
            "  ex.answerdate date_answered,\n" +
            "  ex.answer     answer\n" +
            "FROM json_event je\n" +
            "  JOIN orders o ON o.id = je.order_id\n" +
            "  JOIN explanation ex ON je.object_id = ex.id\n" +
            "WHERE je.id = ?";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<EnquiryDAO> getEnquiries(Integer jsonEventId) {
        return jdbcTemplate.query(ENQUIRIES_QUERY, new BeanPropertyRowMapper<>(EnquiryDAO.class), jsonEventId);
    }

}