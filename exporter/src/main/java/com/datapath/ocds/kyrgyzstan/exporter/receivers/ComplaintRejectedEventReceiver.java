package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.ComplaintRejectedDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.Complaint;
import com.datapath.ocds.kyrgyzstan.exporter.events.ComplaintRejectedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;

@Component
public class ComplaintRejectedEventReceiver extends EventReceiverAbs{

    @Autowired
    private ComplaintRejectedDAOService dao;

    public ComplaintRejectedEvent receive(JsonEvent jsonEvent) {
        ComplaintRejectedEvent event = new ComplaintRejectedEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("complaintUpdate");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());
        event.setComplaints(getComplaints(jsonEvent.getId()));
        event.setComplaints(getComplaints(jsonEvent.getId()));
        event.setEventErrors(getSequenceErrors(jsonEvent));
        return event;
    }

    private List<Complaint> getComplaints(Integer jsonEventId) {
        List<Complaint> complaints = new ArrayList<>();
        dao.getComplaints(jsonEventId).forEach(daoComplaint -> {
            Complaint complaint = new Complaint();
            complaint.setId(daoComplaint.getId());
            complaint.setStatus("rejected");
            complaint.setResponseDate(convert(daoComplaint.getResponseDate()));
            complaint.setResponse(daoComplaint.getResponse());

            complaints.add(complaint);
        });
        return complaints;

    }

}