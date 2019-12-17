package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.ComplaintResolvedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.Complaint;
import com.datapath.ocds.kyrgyzstan.exporter.events.ComplaintResolvedEvent;
import com.datapath.ocds.kyrgyzstan.exporter.events.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;

@Component
public class ComplaintResolvedEventReceiver extends EventReceiverAbs{

    @Autowired
    private ComplaintResolvedDAOService dao;

    public ComplaintResolvedEvent receive(JsonEvent jsonEvent) {
        ComplaintResolvedEvent event = new ComplaintResolvedEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("complaintUpdate");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());
        event.setComplaints(getComplaints(jsonEvent));
        event.setEventErrors(getSequenceErrors(jsonEvent));
        return event;
    }

    private List<Complaint> getComplaints(JsonEvent jsonEvent) {
        List<Complaint> complaints = new ArrayList<>();
        dao.getComplaints(jsonEvent.getId()).forEach(daoComplaint -> {
            Complaint complaint = new Complaint();
            complaint.setId(daoComplaint.getId());
            complaint.setStatus("resolved");
            complaint.setResponseDate(convert(daoComplaint.getResponseDate()));
            complaint.setResponse(daoComplaint.getResponse());
            complaint.setDocuments(getDocuments(jsonEvent, complaint.getId()));
            complaints.add(complaint);
        });
        return complaints;
    }

    private List<Document> getDocuments(JsonEvent jsonEvent, Integer complaintId) {
        List<Document> documents = new ArrayList<>();
        dao.getDocuments(jsonEvent.getId(), complaintId).forEach(daoDocument -> {
            Document document = new Document();
            document.setId(daoDocument.getId());
            document.setDatePublished(convert(jsonEvent.getDateCreated()));
            document.setDocumentType("complaintResolved");
            documents.add(document);
        });
        return documents;
    }

}