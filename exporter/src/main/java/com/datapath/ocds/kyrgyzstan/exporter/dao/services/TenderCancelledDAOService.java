package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.*;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenderCancelledDAOService {

    private static final String LOTS_QUERY = "SELECT id FROM lot WHERE order_id=?";
    private static final String DOCUMENTS_QUERY = "select att.id\n" +
            "from orders o\n" +
            "join corrigendum c on c.order_id=o.id\n" +
            "join corrigendum_detail cd on (cd.corrigendum_id=c.id and cd.type=6)\n" +
            "join corrigendum_detail_attachment cda on cda.corrigendum_detail_id=cd.id\n" +
            "join attachment att on cda.attachment_id = att.id\n" +
            "where o.id = ?";

    private static final String AWARDS_QUERY = "SELECT pt.id\n" +
            "FROM orders o\n" +
            "JOIN lot l on l.order_id=o.id\n" +
            "JOIN evaluation_lot el on el.lot_id=l.id\n" +
            "JOIN price_table pt on pt.evaluation_lot=el.id\n" +
            "where o.id = ?";

    private static final String CONTRACTS_QUERY = "SELECT c.id\n" +
            "FROM orders o\n" +
            "JOIN materialized_contract c on c.order_id = o.id\n" +
            "where o.id = ?";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<LotDAO> getLots(Integer orderId) {
        return jdbcTemplate.query(LOTS_QUERY, new BeanPropertyRowMapper<>(LotDAO.class), orderId);
    }

    public List<TenderDocumentDAO> getDocuments(Integer orderId) {
        return jdbcTemplate.query(DOCUMENTS_QUERY, new BeanPropertyRowMapper<>(TenderDocumentDAO.class), orderId);
    }

    public List<AwardDAO> getAwards(Integer orderId) {
        return jdbcTemplate.query(AWARDS_QUERY, new BeanPropertyRowMapper<>(AwardDAO.class), orderId);
    }

    public List<ContractDAO> getContracts(Integer orderId) {
        return jdbcTemplate.query(CONTRACTS_QUERY, new BeanPropertyRowMapper<>(ContractDAO.class), orderId);
    }



}