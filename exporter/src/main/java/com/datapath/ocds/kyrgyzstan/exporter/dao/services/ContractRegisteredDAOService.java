package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ContractDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.LotDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.TenderItemDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractRegisteredDAOService {

    private static final String CONTRACTS_QUERY = "SELECT\n" +
            "  mc.id,\n" +
            "  mc.date_of_contract               date_signed,\n" +
            "  mc.contract_amount_supplier_price amount\n" +
            "FROM orders o\n" +
            "JOIN materialized_contract mc on mc.order_id = o.id\n" +
            "WHERE o.id  = ?";

    private static final String AWARD_IDS_QUERY = "select mcd.price_table_id\n" +
            "        from materialized_contract mc\n" +
            "        join materialized_contract_details mcd on mcd.contract_id=mc.id\n" +
            "where mc.id = ?";

    private static final String ITEMS_QUERY = "select p.id id, p.lot_id related_lot\n" +
            "    from materialized_contract mc\n" +
            "    join materialized_contract_details mcd on mcd.contract_id=mc.id\n" +
            "    join price_table pt on pt.id=mcd.price_table_id\n" +
            "    join price_of_the_product pp on pp.price_table_id=pt.id\n" +
            "    join products p on p.id=pp.product_id\n" +
            "where mc.id = ?";

    private static final String LOTS_QUERY = "select l.id id\n" +
            "from json_event je\n" +
            "  join orders o on o.id = je.object_id\n" +
            "  join materialized_contract mc on mc.order_id = o.id\n" +
            "  join materialized_contract_details mcd on mcd.contract_id = mc.id\n" +
            "  join price_table pt on mcd.price_table_id = pt.id\n" +
            "  join lot l on pt.lot_id = l.id\n" +
            "where je.id = ?\n";


    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<ContractDAO> getContracts(Integer orderId) {
        return jdbcTemplate.query(CONTRACTS_QUERY, new BeanPropertyRowMapper<>(ContractDAO.class), orderId);
    }

    public List<Long> getAwardIdentifiers(Long contractId) {
        return jdbcTemplate.queryForList(AWARD_IDS_QUERY, Long.class, contractId);
    }

    public List<TenderItemDAO> getItems(Long contractId) {
        return jdbcTemplate.query(ITEMS_QUERY, new BeanPropertyRowMapper<>(TenderItemDAO.class), contractId);
    }

    public List<LotDAO> getLots(Integer jsonEventId) {
        return jdbcTemplate.query(LOTS_QUERY, new BeanPropertyRowMapper<>(LotDAO.class), jsonEventId);
    }

}