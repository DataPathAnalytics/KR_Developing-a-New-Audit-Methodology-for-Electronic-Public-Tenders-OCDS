package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ConditionOfContractDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.TwoStageInvitationPublishedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.ConditionOfContract;
import com.datapath.ocds.kyrgyzstan.exporter.events.Tender;
import com.datapath.ocds.kyrgyzstan.exporter.events.TenderDocument;
import com.datapath.ocds.kyrgyzstan.exporter.events.TwoStageInvitationPublishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.*;

@Component
@Slf4j
public class TwoStageInvitationPublishedReceiver {

    private TwoStageInvitationPublishedDAOService dao;

    public TwoStageInvitationPublishedReceiver(TwoStageInvitationPublishedDAOService dao) {
        this.dao = dao;
    }

    public TwoStageInvitationPublishedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        TwoStageInvitationPublishedEvent event = new TwoStageInvitationPublishedEvent();
        event.setSingleTag("tenderUpdate");
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        tender.setDatePublished2(convert(order.getDatePublished()));
        tender.setDocuments(getDocuments(jsonEvent));
        tender.setConditionOfContract(getConditionOfContract(jsonEvent.getOrderId()));

        return event;
    }

    private List<TenderDocument> getDocuments(JsonEvent jsonEvent) {
        List<TenderDocument> documents = new ArrayList<>();
        dao.getDocuments(jsonEvent.getId()).forEach(daoDocument -> {
            TenderDocument document = new TenderDocument();
            document.setId(daoDocument.getId());
            document.setDatePublished(convert(jsonEvent.getDateCreated()));
            document.setDocumentType("tender");
            document.setTitle(daoDocument.getTitle());

            documents.add(document);
        });
        return documents;
    }

    private ConditionOfContract getConditionOfContract(Integer orderId) {
        try {
            ConditionOfContractDAO daoCondition = dao.getConditionsOfContract(orderId);

            ConditionOfContract condition = new ConditionOfContract();
            condition.setId(daoCondition.getId());

            condition.setLateDeliveryRate(daoCondition.getLateDeliveryRate());
            condition.setLatePaymentRate(daoCondition.getLatePaymentRate());
            condition.setLateGuaranteeRate(daoCondition.getLateGuaranteeRate());

            condition.setGuaranteePercent(daoCondition.getGuaranteePercent());
            condition.setMaxDeductibleAmountDelivery(daoCondition.getMaxDeductibleAmountDelivery());
            condition.setMaxDeductibleAmountPayment(daoCondition.getMaxDeductibleAmountPayment());
            try {
                if (daoCondition.getMaxDeductibleAmountGuarantee() != null) {
                    condition.setMaxDeductibleAmountGuarantee(Double.parseDouble(daoCondition.getMaxDeductibleAmountGuarantee()));
                }
            } catch (NumberFormatException ex) {
                log.warn("Condition of contract has invalid MaxDeductibleAmountGuarantee value ");
            }

            condition.setHasGuarantee(daoCondition.getHasGuarantee());
            condition.setHasInsurance(daoCondition.getHasInsurance());
            condition.setHasRelatedServices(daoCondition.getHasRelatedServices());
            condition.setHasSpares(daoCondition.getHasSpares());
            condition.setHasTechnicalControl(daoCondition.getHasTechnicalControl());
            condition.setHasPrepayment(daoCondition.getHasPrepayment());
            condition.setHasAcceptancePayment(daoCondition.getHasAcceptancePayment());
            condition.setHasShipmentPayment(daoCondition.getHasShipmentPayment());
            condition.setPrepaymentPercent(daoCondition.getPrepaymentPercent());
            condition.setAcceptancePaymentPercent(daoCondition.getAcceptancePaymentPercent());
            condition.setShipmentPaymentPercent(daoCondition.getShipmentPaymentPercent());
            condition.setInsuranceType(daoCondition.getInsuranceType());
            condition.setHasArbitralTribunal(daoCondition.getHasArbitralTribunal());

            return condition;
        } catch (EmptyResultDataAccessException ex) {
            //In some cases that information absent in database. Need to discuss with Kyrgyzstan
            return null;
        }
    }

}
