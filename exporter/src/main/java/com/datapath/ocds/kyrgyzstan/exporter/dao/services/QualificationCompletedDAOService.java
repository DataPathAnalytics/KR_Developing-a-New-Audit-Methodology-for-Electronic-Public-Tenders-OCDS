package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.BidDetailDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QualificationCompletedDAOService {

    private static final String BIDS_QUERY = "select\n" +
            "  bs.id         id,\n" +
            "  case when bs.confirmed_qualification = false\n" +
            "    then 'valid'\n" +
            "  when bs.confirmed_qualification = true\n" +
            "    then 'disqualified'\n" +
            "  else null end status\n" +
            "from json_event je\n" +
            "  inner join orders o on o.id = je.object_id\n" +
            "  inner join bid_submission bs on (bs.order_id = o.id and bs.status not in (0, 2))\n" +
            "  inner join price_table pt on pt.bid_submission_id = bs.id\n" +
            "where je.id = ?";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<BidDetailDAO> getBidDetails(Integer jsonEventId) {
        return jdbcTemplate.query(BIDS_QUERY, new BeanPropertyRowMapper<>(BidDetailDAO.class), jsonEventId);
    }

}