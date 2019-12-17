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
public class EnquiryReceivedDAOService {

    private static final String ENQUIRIES_QUERY = "select\n" +
            "  ex.id    id,\n" +
            "  ex.date  date,\n" +
            "  ex.body description,\n" +
            "  c.inn    author_id\n" +
            "from json_event je\n" +
            "  inner join orders o on o.id = je.order_id\n" +
            "  inner join explanation ex on je.object_id = ex.id\n" +
            "  left join person p on p.id = ex.person_id\n" +
            "  left join company c on c.id = p.company_id and c.inn = '01707201410029'\n" +
            "where je.id = ?";

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
