package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ConditionOfContractDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.TenderDocumentDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwoStageInvitationPublishedDAOService {

    private static final String DOCUMENTS_QUERY = "select\n" +
            "  att.id,\n" +
            "  att.file_name title\n" +
            "from json_event je\n" +
            "  join orders o on o.id = je.object_id\n" +
            "  join attachment att on (att.id = o.main_order_attachment or att.id = o.reason_attachment_id)\n" +
            "where je.id = ?";

    private static final String CONDITION_OF_CONTRACT_QUERY = "select\n" +
            "  cc.id,\n" +
            "  cc.day_rate_delivery              late_delivery_rate,\n" +
            "  cc.day_rate_payment               late_payment_rate,\n" +
            "  cc.forfeit_day_rate               late_guarantee_rate,\n" +
            "  cc.guarantee_provision            guarantee_percent,\n" +
            "  cc.max_deductible_amount_delivery max_deductible_amount_delivery,\n" +
            "  cc.max_deductible_amount_payment  max_deductible_amount_payment,\n" +
            "  cc.max_deductible_amount          max_deductible_amount_guarantee,\n" +
            "  cc.allow_guarantee                has_guarantee,\n" +
            "  cc.allow_insurance                has_insurance,\n" +
            "  cc.allow_related_service          has_related_services,\n" +
            "  cc.allow_spares                   has_spares,\n" +
            "  cc.allow_technical_control        has_technical_control,\n" +
            "  cc.allow_advance                  has_prepayment,\n" +
            "  cc.allow_after_acceptance         has_acceptance_payment,\n" +
            "  cc.allow_after_shipment           has_shipment_payment,\n" +
            "  cc.advance                        prepayment_percent,\n" +
            "  cc.after_acceptance               acceptance_payment_percent,\n" +
            "  cc.after_shipment                 shipment_payment_percent,\n" +
            "  cc.insurance                      insurance_type,\n" +
            "  cc.court_type                     has_arbitral_tribunal\n" +
            "from conditions_of_contract cc\n" +
            "where cc.order_id = ?";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<TenderDocumentDAO> getDocuments(Integer jsonEventId) {
        return jdbcTemplate.query(DOCUMENTS_QUERY, new BeanPropertyRowMapper<>(TenderDocumentDAO.class), jsonEventId);
    }

    public ConditionOfContractDAO getConditionsOfContract(Integer orderId) {
        return jdbcTemplate.queryForObject(CONDITION_OF_CONTRACT_QUERY, new BeanPropertyRowMapper<>(ConditionOfContractDAO.class), orderId);
    }

}