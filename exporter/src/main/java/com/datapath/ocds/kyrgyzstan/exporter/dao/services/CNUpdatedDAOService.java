package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.LotDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.TenderDocumentDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.TenderItemDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CNUpdatedDAOService {

    private static final String LOTS_QUERY = "SELECT id,sum_contest amount FROM lot WHERE order_id=?";
    private static final String ITEMS_QUERY =
            "select p.id            id,\n" +
                    "  p.amount        quantity,\n" +
                    "  m.id            unit_id,\n" +
                    "  m.full_name     unit_name,\n" +
                    "  p.prce_for_unit unit_value_amount\n" +
                    "from json_event je\n" +
                    "  join lot l on l.order_id = je.order_id\n" +
                    "  join products p on p.lot_id = l.id\n" +
                    "  join okgz oz on oz.code = p.okgz_id\n" +
                    "  join measurement_unit m on p.measurement_unit_id = m.id\n" +
                    "where je.id = ?";

    private static final String DOCUMENTS_QUERY = "SELECT att.id\n" +
            "FROM orders o\n" +
            "JOIN corrigendum c on c.order_id=o.id\n" +
            "JOIN corrigendum_detail cd on (cd.corrigendum_id=c.id and cd.type<>6)\n" +
            "JOIN corrigendum_detail_attachment cda on cda.corrigendum_detail_id=cd.id\n" +
            "JOIN attachment att on cda.attachment_id = att.id\n" +
            "WHERE o.id= ?";

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

    public List<TenderItemDAO> getItems(Integer jsonEventId) {
        return jdbcTemplate.query(ITEMS_QUERY, new BeanPropertyRowMapper<>(TenderItemDAO.class), jsonEventId);
    }

    public List<TenderDocumentDAO> getDocuments(Integer orderId) {
        return jdbcTemplate.query(DOCUMENTS_QUERY, new BeanPropertyRowMapper<>(TenderDocumentDAO.class), orderId);
    }

}