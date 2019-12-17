package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.Constants;
import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.Bids1StageOpenedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static java.util.Collections.singletonList;

@Component
public class Bids1StageOpenedEventReceiver {

    private Bids1StageOpenedDAOService dao;

    public Bids1StageOpenedEventReceiver(Bids1StageOpenedDAOService dao) {
        this.dao = dao;
    }

    public Bids1StageOpenedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        Bids1StageOpenedEvent event = new Bids1StageOpenedEvent();
        event.setDate(Constants.convert(jsonEvent.getDateCreated()));
        event.setSingleTag("bidsPartialOpening");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        event.setTender(tender);

        event.setParties(getParties(jsonEvent));
        event.setBids(getBids(jsonEvent));

        return event;
    }

    private List<Party> getParties(JsonEvent jsonEvent) {
        List<Party> parties = new ArrayList<>();

        dao.getParties(jsonEvent.getId()).forEach(daoParty -> {
            Party party = new Party();

            String alpha2 = CountryCode.getByCode(daoParty.getCountryCode()).getAlpha2();
            party.setId(alpha2 + "-INN" + DASH + daoParty.getId());

            Identifier identifier = new Identifier();
            identifier.setId(daoParty.getId());
            identifier.setScheme(alpha2 + "-INN");
            party.setIdentifier(identifier);

            party.setRoles(singletonList("tenderer"));
            parties.add(party);
        });

        return parties;
    }

    private Bids getBids(JsonEvent jsonEvent) {
        List<BidDetail> details = new ArrayList<>();
        dao.getBidDetails(jsonEvent.getId()).forEach(daoDetail -> {

            BidDetail detail = new BidDetail();
            detail.setId(daoDetail.getId());
            detail.setStatus("pending");

            Party tenderer = new Party();
            String alpha2 = CountryCode.getByCode(daoDetail.getCountryCode()).getAlpha2();
            tenderer.setId(alpha2 + "-INN" + DASH + daoDetail.getTendererId());

            Identifier identifier = new Identifier();
            identifier.setId(daoDetail.getTendererId());
            identifier.setScheme(alpha2 + "-INN");
            tenderer.setIdentifier(identifier);

            detail.setTenderers(singletonList(tenderer));

            details.add(detail);
        });
        return new Bids(details);
    }

}
