package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.ReEvaluationDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.Contract;
import com.datapath.ocds.kyrgyzstan.exporter.events.ReEvaluationEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.Tender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;

@Component
public class ReEvaluationEventReceiver {

    @Autowired
    private ReEvaluationDAOService dao;

    public ReEvaluationEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        ReEvaluationEvent event = new ReEvaluationEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("contractUpdate");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setStatus("active");
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        event.setTender(tender);

        event.setContracts(getContracts(jsonEvent.getId()));

        return event;
    }

    private List<Contract> getContracts(Integer jsonEventId) {
        List<Contract> contracts = new ArrayList<>();

        dao.getContracts(jsonEventId).forEach(daoContract -> {
            Contract contract = new Contract();
            contract.setId(daoContract.getId());
            contract.setStatus("pending");
            contracts.add(contract);
        });

        return contracts;
    }

}