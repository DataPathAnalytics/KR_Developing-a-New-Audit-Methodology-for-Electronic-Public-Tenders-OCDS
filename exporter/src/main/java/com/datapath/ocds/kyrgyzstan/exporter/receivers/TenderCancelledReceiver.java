package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.*;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.TenderCancelledDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;

@Component
public class TenderCancelledReceiver extends EventReceiverAbs {

    @Autowired
    private TenderCancelledDAOService dao;

    public TenderCancelledEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        TenderCancelledEvent event = new TenderCancelledEvent();
        event.setSingleTag("tenderCancellation");
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setStatus("cancelled");
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        tender.setLots(getLots(order.getId()));
        tender.setDocuments(getDocuments(order.getId()));
        tender.setDate(convert(jsonEvent.getDateCreated()));
        event.setTender(tender);
        event.setAwards(getAwards(jsonEvent));
        event.setContracts(getContracts(jsonEvent));
        event.setEventErrors(getSequenceErrors(jsonEvent));
        return event;
    }

    private List<Lot> getLots(Integer orderId) {
        List<Lot> lots = new ArrayList<>();

        List<LotDAO> daoLots = dao.getLots(orderId);
        for (LotDAO daoLot : daoLots) {
            Lot lot = new Lot();
            lot.setId(daoLot.getId());
            lot.setStatus("cancelled");
            lots.add(lot);
        }
        return lots;
    }

    private List<TenderDocument> getDocuments(Integer orderId) {
        List<TenderDocument> documents = new ArrayList<>();

        List<TenderDocumentDAO> daoDocuments = dao.getDocuments(orderId);
        for (TenderDocumentDAO daoDocument : daoDocuments) {
            TenderDocument document = new TenderDocument();
            document.setId(daoDocument.getId());
            documents.add(document);
        }

        return documents;
    }

    private List<Award> getAwards(JsonEvent jsonEvent) {
        List<Award> awards = new ArrayList<>();

        for (AwardDAO awardDAO : dao.getAwards(jsonEvent.getOrderId())) {
            Award award = new Award();
            award.setId(awardDAO.getId());
            award.setStatus("cancelled");
            award.setDate(convert(jsonEvent.getDateCreated()));
            awards.add(award);
        }
        return awards.isEmpty() ? null : awards;
    }

    private List<Contract> getContracts(JsonEvent jsonEvent) {
        List<Contract> contracts = new ArrayList<>();

        for (ContractDAO contractDAO : dao.getContracts(jsonEvent.getOrderId())) {
            Contract contract = new Contract();
            contract.setId(contractDAO.getId());
            contract.setStatus("terminated");
            contract.setDate(convert(jsonEvent.getDateCreated()));
            contracts.add(contract);
        }
        return contracts.isEmpty() ? null : contracts;
    }

}
