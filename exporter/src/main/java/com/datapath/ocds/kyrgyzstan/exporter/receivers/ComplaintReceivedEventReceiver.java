package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.ComplaintReceivedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;
import static java.util.Collections.singletonList;

@Component
public class ComplaintReceivedEventReceiver extends EventReceiverAbs {

    @Autowired
    private ComplaintReceivedDAOService dao;

    public ComplaintReceivedEvent receive(JsonEvent jsonEvent) {
        ComplaintReceivedEvent event = new ComplaintReceivedEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("complaint");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setComplaints(getComplaints(jsonEvent.getId()));
        event.setParties(getParties(jsonEvent.getId()));
        event.setEventErrors(getSequenceErrors(jsonEvent));
        return event;
    }

    private List<Complaint> getComplaints(Integer jsonEventId) {
        List<Complaint> complaints = new ArrayList<>();
        dao.getComplaints(jsonEventId).forEach(daoComplaint -> {
            Complaint complaint = new Complaint();
            complaint.setId(daoComplaint.getId());
            complaint.setStatus("pending");
            complaint.setSubmissionDate(convert(daoComplaint.getSubmissionDate()));
            complaint.setType(daoComplaint.getType());
            complaint.setTitle(daoComplaint.getTitle());
            complaint.setDescription(daoComplaint.getDescription());
            if (daoComplaint.getAuthor() != null) {
                String alpha2 = CountryCode.getByCode(daoComplaint.getCountryCode()).getAlpha2();

                String authorId = alpha2 + "-INN" + DASH + daoComplaint.getAuthor();
                complaint.setAuthor(new Author(authorId));
            }
            complaint.setDocuments(getDocuments(daoComplaint.getId()));

            complaints.add(complaint);
        });
        return complaints.isEmpty() ? null : complaints;
    }

    private List<Document> getDocuments(Integer complaintId) {
        List<Document> documents = new ArrayList<>();
        dao.getDocuments(complaintId).forEach(daoDocument -> {
            Document document = new Document();
            document.setId(daoDocument.getId());
            documents.add(document);
        });
        return documents;
    }

    private List<Party> getParties(Integer jsonEventId) {
        List<Party> parties = new ArrayList<>();
        dao.getParties(jsonEventId).forEach(daoParty -> {

            String alpha2 = CountryCode.getByCode(daoParty.getCountryCode()).getAlpha2();

            Party party = new Party();
            party.setId(alpha2 + "-INN-" + daoParty.getId());
            party.setRoles(singletonList("complainer"));

            Identifier identifier = new Identifier();
            identifier.setId(daoParty.getId());
            identifier.setScheme(alpha2 + "-INN");
            identifier.setLegalName(daoParty.getNameEn());
            identifier.setLegalName_ru(daoParty.getNameRu());
            identifier.setLegalName_kg(daoParty.getNameKg());
            party.setIdentifier(identifier);

            Address address = new Address();
            address.setAteCode(daoParty.getAteCode());
            address.setCountryName(daoParty.getCountryName());
            address.setRegion(daoParty.getRegion());
            address.setSubRegion(daoParty.getSubRegion());
            address.setDistrict(daoParty.getDistrict());
            address.setSubDistrict(daoParty.getSubDistrict());
            address.setSubSubDistrict(daoParty.getSubSubDistrict());
            address.setLocality(daoParty.getLocality());
            address.setStreetAddress(daoParty.getStreetAddress());
            party.setAddress(address);
            party.setAdditionalContactPoints(getContactPoints(jsonEventId));

            parties.add(party);
        });
        return parties;
    }

    private List<ContactPoint> getContactPoints(Integer jsonEventId) {
        ArrayList<ContactPoint> contactPoints = new ArrayList<>();
        //TODO add converter to convert dao objects in to event objects
        dao.getContactPoints(jsonEventId).forEach(daoPoint -> {
            ContactPoint point = new ContactPoint();
            point.setName(daoPoint.getName());
            point.setPhone(daoPoint.getPhone());
            point.setEmail(daoPoint.getEmail());
            point.setRole(daoPoint.getRole());
            contactPoints.add(point);
        });
        return contactPoints;
    }

}