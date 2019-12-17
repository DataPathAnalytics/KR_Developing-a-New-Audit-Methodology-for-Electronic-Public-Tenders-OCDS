package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.BidDetailDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.PartyDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Bids1StageOpenedDAOService {

    private static final String PARTIES_QUERY = "select c.inn id, cn.iso_code country_code\n" +
            "from json_event je\n" +
            "  join orders o on o.id = je.object_id\n" +
            "  join bid_submission bs on (bs.order_id = o.id and bs.status not in (0, 2))\n" +
            "  join company c on c.id = bs.company_id\n" +
            "  join country cn on cn.id = c.country_id\n" +
            "where je.id = ?";

    private static final String BIDS_QUERY = "SELECT\n" +
            "  bs.id       id,\n" +
            "  c.inn       tenderer_id,\n" +
            "  cn.iso_code country_code\n" +
            "FROM json_event je\n" +
            "  JOIN orders o ON o.id = je.object_id\n" +
            "  JOIN bid_submission bs ON (bs.order_id = o.id AND bs.status NOT IN (0, 2))\n" +
            "  JOIN company c ON c.id = bs.company_id\n" +
            "  join country cn on cn.id = c.country_id\n" +
            "WHERE je.id = ?";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<PartyDAO> getParties(Integer jsonEventId) {
        return jdbcTemplate.query(PARTIES_QUERY, new BeanPropertyRowMapper<>(PartyDAO.class), jsonEventId);
    }

    public List<BidDetailDAO> getBidDetails(Integer jsonEventId) {
        return jdbcTemplate.query(BIDS_QUERY, new BeanPropertyRowMapper<>(BidDetailDAO.class), jsonEventId);
    }

}
