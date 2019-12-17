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
public class SupplierRefusedDAOService {

    private static final String LOTS_QUERY = "SELECT id,status FROM lot WHERE order_id = ?";

    private static final String PARTIES_QUERY = "select DISTINCT\n" +
            "  c.inn         AS id,\n" +
            "  cn.iso_code   AS country_code,\n" +
            "  case when(pt.\"position\" = 1 and bs.confirmed = true and pt.canceled = false)\n" +
            "         THEN 'supplier'\n" +
            "       when(pt.\"position\" = 1 and bs.confirmed = true and pt.canceled is null)\n" +
            "         THEN 'supplier'\n" +
            "       else null end AS role\n" +
            "from json_event je\n" +
            "  join orders o on o.id =  (select order_id from bid_submission where bid_submission.id = je.object_id)\n" +
            "  join orders o2 on o2.id = je.order_id\n" +
            "  join lot l on l.order_id = o.id and l.status <> 3\n" +
            "  join evaluation_lot el on el.lot_id = l.id\n" +
            "  join price_table pt on pt.evaluation_lot = el.id and (pt.position is not null or pt.canceled = true)\n" +
            "  join bid_submission bs on bs.id = pt.bid_submission_id and bs.confirmed_qualification = false\n" +
            "  join company c on bs.company_id = c.id\n" +
            "  join country cn on c.country_id = cn.id\n" +
            "where je.id = ?";

    private static final String BIDS_QUERY = "select\n" +
            "  COALESCE((select bs2.id\n" +
            "            from bid_submission bs2\n" +
            "            where bs2.company_id = bs.company_id and o2.id = bs2.order_id and bs2.status not in (0, 2)), bs.id)\n" +
            "                id,\n" +
            "  case when bs.confirmed_qualification = false\n" +
            "    then 'valid'\n" +
            "  when bs.confirmed_qualification = true\n" +
            "    then 'disqualified'\n" +
            "  else null end status\n" +
            "from json_event je\n" +
            "  join orders o on o.id =  (select order_id from bid_submission where bid_submission.id = je.object_id)\n" +
            "  left join orders o2 on o2.id = je.order_id\n" +
            "  join bid_submission bs on (bs.order_id = o.id and bs.status not in (0, 2))\n" +
            "where je.id = ?";


    private static final String AWARDS_QUERY = "select pt.id,\n" +
            "       case when(pt.\"position\" = 1 and bs.confirmed = true and pt.canceled = true) then 'disqualified'\n" +
            "            when(pt.\"position\" = 1 and bs.confirmed = true and pt.canceled = false) then 'active'\n" +
            "            when(pt.\"position\" = 1 and bs.confirmed = true and pt.canceled is null) then 'active'\n" +
            "            when(pt.\"position\" = 1 and bs.confirmed = false and pt.canceled = true) then 'rejected'\n" +
            "            when(pt.\"position\" = 1 and bs.confirmed = false and pt.canceled = false) then 'rejected'\n" +
            "            when(pt.\"position\" = 1 and bs.confirmed = false and pt.canceled is null) then 'rejected'\n" +
            "            when(pt.\"position\" = 1 and bs.confirmed is null and pt.canceled = true) then 'disqualified'\n" +
            "            when(pt.\"position\" = 1 and bs.confirmed is null and pt.canceled = false) then 'pending'\n" +
            "            when(pt.\"position\" = 1 and bs.confirmed is null and pt.canceled is null) then 'pending'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed = true and pt.canceled = true) then 'disqualified'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed = true and pt.canceled = false) then 'pending'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed = true and pt.canceled is null) then 'pending'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed = false and pt.canceled = true) then 'rejected'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed = false and pt.canceled = false) then 'rejected'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed = false and pt.canceled is null) then 'rejected'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed is null and pt.canceled = true) then 'disqualified'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed is null and pt.canceled = false) then 'pending'\n" +
            "            when(pt.\"position\" > 1 and bs.confirmed is null and pt.canceled is null) then 'pending'\n" +
            "            when(pt.\"position\" is null and bs.confirmed = true and pt.canceled = true) then 'disqualified'\n" +
            "            when(pt.\"position\" is null and bs.confirmed = true and pt.canceled = false) then 'pending'\n" +
            "            when(pt.\"position\" is null and bs.confirmed = true and pt.canceled is null) then 'pending'\n" +
            "            when(pt.\"position\" is null and bs.confirmed = false and pt.canceled = true) then 'rejected'\n" +
            "            when(pt.\"position\" is null and bs.confirmed = false and pt.canceled = false) then 'rejected'\n" +
            "            when(pt.\"position\" is null and bs.confirmed = false and pt.canceled is null) then 'rejected'\n" +
            "            when(pt.\"position\" is null and bs.confirmed is null and pt.canceled = true) then 'disqualified'\n" +
            "            when(pt.\"position\" is null and bs.confirmed is null and pt.canceled = false) then 'pending'\n" +
            "            when(pt.\"position\" is null and bs.confirmed is null and pt.canceled is null) then 'pending'\n" +
            "            else null end status,\n" +
            "       pt.sum_contest  amount,\n" +
            "       pt.lot_id related_lot,\n" +
            "       COALESCE ((select bs2.id from bid_submission bs2 where bs2.company_id=bs.company_id and o2.id=bs2.order_id and bs2.status not in(0,2)),bs.id) related_bid\n" +
            "from json_event je\n" +
            "       inner join orders o on o.id=(select order_id from bid_submission where bid_submission.id = je.object_id)\n" +
            "       inner join orders o2 on o2.id=je.order_id\n" +
            "       inner join lot l on l.order_id=o.id and l.status<>3\n" +
            "       inner join evaluation_lot el on el.lot_id=l.id\n" +
            "       inner join price_table pt on pt.evaluation_lot=el.id and (pt.position is not null or pt.canceled=true)\n" +
            "       inner join bid_submission bs on bs.id=pt.bid_submission_id and bs.confirmed_qualification=false\n" +
            "       inner join company c on bs.company_id = c.id\n" +
            "\n" +
            "where je.id = ?";

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

    public List<BidDetailDAO> getBids(Integer jsonEventId) {
        return jdbcTemplate.query(BIDS_QUERY, new BeanPropertyRowMapper<>(BidDetailDAO.class), jsonEventId);
    }

    public List<PartyDAO> getParties(Integer jsonEventId) {
        return jdbcTemplate.query(PARTIES_QUERY, new BeanPropertyRowMapper<>(PartyDAO.class), jsonEventId);
    }

    public List<AwardDAO> getAwards(Integer jsonEventId) {
        return jdbcTemplate.query(AWARDS_QUERY, new BeanPropertyRowMapper<>(AwardDAO.class), jsonEventId);
    }

}
