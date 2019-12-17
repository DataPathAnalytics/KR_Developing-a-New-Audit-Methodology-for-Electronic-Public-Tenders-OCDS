package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.Constants;
import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ContractDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.TenderItemDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.ContractRegisteredDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convertDateToDateTime;

@Component
public class ContractRegisteredReceiver extends EventReceiverAbs {

    private static final String CONTRACT = "contract";
    private static final String COMPLETE = "complete";

    @Autowired
    private ContractRegisteredDAOService dao;

    public ContractRegisteredEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        ContractRegisteredEvent event = new ContractRegisteredEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag(CONTRACT);
        event.setOcid(Constants.OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + Constants.DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        tender.setStatus(COMPLETE);
        tender.setDate(convert(jsonEvent.getDateCreated()));
        tender.setLots(getLots(jsonEvent.getId()));
        event.setTender(tender);

        event.setContracts(getContracts(jsonEvent));
        event.setEventErrors(getSequenceErrors(jsonEvent));

        return event;
    }

    private List<Contract> getContracts(JsonEvent jsonEvent) {
        List<Contract> contracts = new ArrayList<>();

        List<ContractDAO> daoContracts = dao.getContracts(jsonEvent.getOrderId());

        for (ContractDAO daoContract : daoContracts) {
            List<Long> awards = dao.getAwardIdentifiers(daoContract.getId());

            Contract contract = new Contract();
            contract.setId(daoContract.getId());
            contract.setStatus("active");
            contract.setDateSigned(convertDateToDateTime(daoContract.getDateSigned()));
            contract.setDate(convert(jsonEvent.getDateCreated()));

            Value value = new Value();
            value.setAmount(daoContract.getAmount());
            value.setCurrency("KGS");
            contract.setValue(value);

            contract.setAwardIDs(awards);
            contract.setItems(getItems(daoContract.getId()));
            contracts.add(contract);
        }

        return contracts.isEmpty() ? null : contracts;
    }

    private List<TenderItem> getItems(Long contractId) {
        List<TenderItem> items = new ArrayList<>();
        List<TenderItemDAO> daoItems = dao.getItems(contractId);
        for (TenderItemDAO daoItem : daoItems) {
            TenderItem item = new TenderItem();
            item.setId(daoItem.getId());
            item.setRelatedLot(daoItem.getRelatedLot());
            items.add(item);
        }
        return items;
    }

    private List<Lot> getLots(Integer jsonEventId) {
        List<Lot> lots = new ArrayList<>();

        dao.getLots(jsonEventId).forEach(daoLot -> {
            Lot lot = new Lot();
            lot.setId(daoLot.getId());
            lot.setStatus("complete");
            lots.add(lot);
        });

        return lots.isEmpty() ? null : lots;
    }

}