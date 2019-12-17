package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.ComplaintReviewDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.Complaint;
import com.datapath.ocds.kyrgyzstan.exporter.events.ComplaintReviewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.OCID_PREFIX;
import static com.datapath.ocds.kyrgyzstan.exporter.Constants.convert;

@Component
public class ComplaintReviewEventReceiver extends EventReceiverAbs{

    @Autowired
    private ComplaintReviewDAOService dao;

    public ComplaintReviewEvent receive(JsonEvent jsonEvent) {
        ComplaintReviewEvent event = new ComplaintReviewEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag("complaintUpdate");
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());
        event.setComplaints(getComplaints(jsonEvent.getId()));
        event.setEventErrors(getSequenceErrors(jsonEvent));
        return event;
    }

    private List<Complaint> getComplaints(Integer jsonEventId) {
        List<Complaint> complaints = new ArrayList<>();
        dao.getComplaints(jsonEventId).forEach(daoComplaint -> {
            Complaint complaint = new Complaint();
            complaint.setId(daoComplaint.getId());
            complaint.setStatus("review");
            complaint.setReviewDate(convert(daoComplaint.getReviewDate()));
            complaints.add(complaint);
        });
        return complaints;
    }

}
