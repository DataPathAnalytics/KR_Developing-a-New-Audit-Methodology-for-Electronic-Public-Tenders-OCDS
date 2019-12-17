package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.*;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.BidsDisclosedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.*;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

@Component
public class BidsDisclosedReceiver extends EventReceiverAbs {

    private static final String BIDS_FULL_OPENING = "bidsFullOpening";
    private static final String TENDERER = "tenderer";

    private BidsDisclosedDAOService dao;

    public BidsDisclosedReceiver(BidsDisclosedDAOService dao) {
        this.dao = dao;
    }

    public BidsDisclosedEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());

        BidsDisclosedEvent event = new BidsDisclosedEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag(BIDS_FULL_OPENING);
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        event.setParties(getParties(jsonEvent.getId()));

        Tender tender = new Tender();
        tender.setId(String.valueOf(order.getId()));
        tender.setCurrentStage(OrderStatus.getStatus(order.getStatus()).getName());
        if (Objects.isNull(order.getPreQualification())) {
            tender.setStatus("pending");
        } else if (!order.getPreQualification()) {
            tender.setStatus("pending");
        }
        event.setTender(tender);

        event.setBids(getBids(jsonEvent.getId(), order.getDateContest()));
        event.setEventErrors(getSequenceErrors(jsonEvent));
        return event;
    }

    private List<Party> getParties(Integer jsonEventId) {
        List<Party> parties = new ArrayList<>();

        List<PartyDAO> daoParties = dao.getParties(jsonEventId);
        for (PartyDAO partyDAO : daoParties) {
            String alpha2 = CountryCode.getByCode(partyDAO.getCountryCode()).getAlpha2();

            Party party = new Party();
            party.setId(alpha2 + "-INN" + DASH + partyDAO.getId());

            Identifier identifier = new Identifier();
            identifier.setId(partyDAO.getId());
            identifier.setScheme(alpha2 + "-INN");
            identifier.setLegalName(partyDAO.getNameEn());
            identifier.setLegalName_ru(partyDAO.getNameRu());
            identifier.setLegalName_kg(partyDAO.getNameKg());
            party.setIdentifier(identifier);

            Address address = new Address();
            address.setAteCode(partyDAO.getAteCode());
            address.setCountryName(partyDAO.getCountryName());
            address.setRegion(partyDAO.getRegion());
            address.setSubRegion(partyDAO.getSubRegion());
            address.setDistrict(partyDAO.getDistrict());
            address.setSubDistrict(partyDAO.getSubDistrict());
            address.setSubSubDistrict(partyDAO.getSubSubDistrict());
            address.setLocality(partyDAO.getLocality());
            address.setStreetAddress(partyDAO.getStreetAddress());
            party.setAddress(address);

            party.setRoles(singletonList(TENDERER));

            parties.add(party);
        }
        return parties;
    }

    private Bids getBids(Integer jsonEventId, String orderDateContest) {
        List<BidDetail> details = new ArrayList<>();
        List<BidDetailDAO> daoDetails = dao.getBidDetails(jsonEventId);
        for (BidDetailDAO daoDetail : daoDetails) {

            BidDetail detail = new BidDetail();
            detail.setId(daoDetail.getId());
            detail.setStatus("pending");
            detail.setDate(convertDateToDateTime(daoDetail.getDate()));
            detail.setDateDisclosed(convert(orderDateContest));

            if (nonNull(daoDetail.getCountryCode())) {
                String alpha2 = CountryCode.getByCode(daoDetail.getCountryCode()).getAlpha2();

                Party party = new Party();
                party.setId(alpha2 + "-INN" + DASH + daoDetail.getTendererId());
                detail.setTenderers(singletonList(party));
            }

            detail.setRelatedLots(getLots(daoDetail.getId()));
            detail.setPriceProposals(getPriceProposals(daoDetail.getId()));

            details.add(detail);
        }
        return new Bids(details);
    }

    private List<Lot> getLots(Integer bidId) {
        List<LotDAO> daoLots = dao.getLots(bidId);
        List<Lot> lots = new ArrayList<>();
        for (LotDAO daoLot : daoLots) {
            Lot lot = new Lot();
            lot.setId(daoLot.getId());

            Value value = new Value();
            value.setAmount(daoLot.getAmount());
            value.setCurrency("KGS");
            lot.setValue(value);

            lots.add(lot);
        }
        return lots;
    }

    private List<PriceProposal> getPriceProposals(Integer bidId) {
        List<PriceProposal> proposals = new ArrayList<>();
        List<PriceProposalDAO> daoProposals = dao.getPriceProposals(bidId);

        for (PriceProposalDAO daoProposal : daoProposals) {
            PriceProposal proposal = new PriceProposal();
            proposal.setId(daoProposal.getId());
            proposal.setRelatedItem(daoProposal.getRelatedItem());
            proposal.setRelatedLot(daoProposal.getRelatedLot());


            Value value = new Value();
            value.setAmount(daoProposal.getUnitValueAmount());
            value.setCurrency(daoProposal.getUnitValueCurrency());

            Unit unit = new Unit();
            unit.setValue(value);

            proposal.setUnit(unit);

            proposals.add(proposal);
        }
        return proposals;
    }

}