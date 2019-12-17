package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.LotDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.SupplierRefusedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.*;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

@Component
public class SupplierRefusedReceiver extends EventReceiverAbs {

    private static final String AWARD = "award";

    @Autowired
    private SupplierRefusedDAOService dao;

    public SupplierRefusedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        SupplierRefusedEvent event = new SupplierRefusedEvent();
        event.setSingleTag(AWARD);
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        tender.setLots(getLots(order.getId()));
        event.setTender(tender);

        event.setParties(getParties(jsonEvent.getId()));
        event.setBids(getBids(jsonEvent));
        event.setAwards(getAwards(jsonEvent));
        event.setEventErrors(getSequenceErrors(jsonEvent));
        return event;
    }

    private List<Party> getParties(Integer jsonEventId) {
        List<Party> parties = new ArrayList<>();
        dao.getParties(jsonEventId).forEach(daoParty -> {
            Party party = new Party();

            String alpha2 = CountryCode.getByCode(daoParty.getCountryCode()).getAlpha2();
            party.setId(alpha2 + "-INN" + DASH + daoParty.getId());

            if (nonNull(daoParty.getRole())) {
                party.setRoles(singletonList(daoParty.getRole()));
            }
            parties.add(party);
        });
        return parties;
    }

    private List<Lot> getLots(Integer orderId) {
        List<Lot> lots = new ArrayList<>();
        List<LotDAO> daoLots = dao.getLots(orderId);
        for (LotDAO daoLot : daoLots) {
            Lot lot = new Lot();
            lot.setId(daoLot.getId());
            if (daoLot.getStatus().equals(3)) {
                lot.setStatus("unsuccessful");
            } else {
                lot.setStatus("active");
            }
            lots.add(lot);
        }
        return lots;
    }

    private Bids getBids(JsonEvent jsonEvent) {
        List<BidDetail> details = new ArrayList<>();

        dao.getBids(jsonEvent.getId()).forEach(daoDetail -> {
            BidDetail detail = new BidDetail();
            detail.setId(daoDetail.getId());
            detail.setStatus(daoDetail.getStatus());
            detail.setDate(convert(jsonEvent.getDateCreated()));
            details.add(detail);
        });

        return new Bids(details);
    }

    private List<Award> getAwards(JsonEvent jsonEvent) {
        List<Award> awards = new ArrayList<>();

        dao.getAwards(jsonEvent.getId()).forEach(daoAward -> {
            Award award = new Award();
            award.setId(daoAward.getId());
            award.setStatus(daoAward.getStatus());
            award.setRelatedLots(List.of(daoAward.getRelatedLot()));
            award.setRelatedBid(daoAward.getRelatedBid());

            Value value = new Value();
            value.setAmount(daoAward.getAmount());
            value.setCurrency("KGS");
            award.setValue(value);
            awards.add(award);
        });

        return awards.isEmpty() ? null : awards;
    }

}
