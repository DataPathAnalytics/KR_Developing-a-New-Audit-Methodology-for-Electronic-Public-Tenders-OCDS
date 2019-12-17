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
public class BidsDisclosedDAOService {

    private static final String PARTIES_QUERY = "with ate_parsed as (\n" +
            "  with recursive ate_rec(id, code, tree) as (\n" +
            "    select\n" +
            "      id,\n" +
            "      code,\n" +
            "      name_ru :: text as tree\n" +
            "    from ate\n" +
            "    where parent is null\n" +
            "    union\n" +
            "    select\n" +
            "      a.id,\n" +
            "      a.code,\n" +
            "      concat(ate_rec.tree, '|', a.name_ru) as tree\n" +
            "    from ate a\n" +
            "      join ate_rec on a.parent = ate_rec.id\n" +
            "  )\n" +
            "  select\n" +
            "    id,\n" +
            "    code,\n" +
            "    nullif(split_part(tree, '|', 1), '') as country,\n" +
            "    nullif(split_part(tree, '|', 2), '') as region,\n" +
            "    nullif(split_part(tree, '|', 3), '') as subregion,\n" +
            "    nullif(split_part(tree, '|', 4), '') as district,\n" +
            "    nullif(split_part(tree, '|', 5), '') as subdistrict,\n" +
            "    nullif(split_part(tree, '|', 6), '') as subsubdistrict,\n" +
            "    nullif(split_part(tree, '|', 7), '') as locality\n" +
            "  from ate_rec)\n" +
            "select\n" +
            "  c.inn            AS id,\n" +
            "  c.title_en       AS name_en,\n" +
            "  c.title_ru       AS name_ru,\n" +
            "  c.title_ky       AS name_kg,\n" +
            "  a.code           as ate_code,\n" +
            "  cn.title_ru      as country_name,\n" +
            "  cn.iso_code      as country_code,\n" +
            "  a.region         as region,\n" +
            "  a.subregion      as subregion,\n" +
            "  a.district       as district,\n" +
            "  a.subdistrict    as subdistrict,\n" +
            "  a.subsubdistrict as subsubdistrict,\n" +
            "  a.locality       as locality,\n" +
            "  c.fact_address   as street_address\n" +
            "from json_event je\n" +
            "  inner join orders o on o.id = je.object_id\n" +
            "  inner join bid_submission bs on (bs.order_id = o.id and bs.status not in (0, 2))\n" +
            "  inner join company c on c.id = bs.company_id\n" +
            "  left join ate_parsed a on c.ate_id = a.id\n" +
            "  inner join country cn on c.country_id = cn.id\n" +
            "where je.id = ?";

    private static final String BID_DETAILS_QUERY = "select\n" +
            "  (select bs2.id\n" +
            "   from bid_submission bs2\n" +
            "   where bs2.company_id = bs.company_id\n" +
            "         and o2.id = bs2.order_id\n" +
            "         and bs2.status not in (0, 2)) id,\n" +
            "  c.inn                                tenderer_id,\n" +
            "  cn.iso_code                          country_code,\n" +
            "  bs.date_created                      date\n" +
            "from json_event je\n" +
            "  inner join orders o on o.id = je.object_id\n" +
            "  inner join bid_submission bs on (bs.order_id = o.id and bs.status not in (0, 2))\n" +
            "  inner join company c on c.id = bs.company_id\n" +
            "  inner join country cn on c.country_id = cn.id\n" +
            "  left join orders o2 on o2.id = je.order_id\n" +
            "where je.id = ?";

    private static final String RELATED_LOTS_QUERY = "select\n" +
            "  pt.lot_id      id,\n" +
            "  pt.sum_contest amount\n" +
            "from json_event je\n" +
            "  join orders o on o.id = je.object_id\n" +
            "  join bid_submission bs on (bs.order_id = o.id and bs.status not in (0, 2))\n" +
            "  join price_table pt on pt.bid_submission_id = bs.id\n" +
            "where je.type = 4 AND bs.id = ?";

    private static final String PRICE_PROPOSALS = "select\n" +
            "  pp.id                           id,\n" +
            "  pp.product_id                   related_item,\n" +
            "  pt.lot_id                       related_lot,\n" +
            "  pp.price_of_unit                unit_value_amount,\n" +
            "  coalesce(c.code, 'KGS' :: text) unit_value_currency\n" +
            "from json_event je\n" +
            "  inner join orders o on o.id = je.object_id\n" +
            "  inner join bid_submission bs on (bs.order_id = o.id and bs.status not in (0, 2))\n" +
            "  inner join price_table pt on pt.bid_submission_id = bs.id\n" +
            "  inner join price_of_the_product pp on pp.price_table_id = pt.id\n" +
            "  left join currency c on bs.currency_id = c.ncode\n" +
            "where je.type = 4 and bs.id = ?\n";


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
        return jdbcTemplate.query(BID_DETAILS_QUERY, new BeanPropertyRowMapper<>(BidDetailDAO.class), jsonEventId);
    }

    public List<LotDAO> getLots(Integer bidId) {
        return jdbcTemplate.query(RELATED_LOTS_QUERY, new BeanPropertyRowMapper<>(LotDAO.class), bidId);
    }

    public List<PriceProposalDAO> getPriceProposals(Integer bidId) {
        return jdbcTemplate.query(PRICE_PROPOSALS, new BeanPropertyRowMapper<>(PriceProposalDAO.class), bidId);
    }

}