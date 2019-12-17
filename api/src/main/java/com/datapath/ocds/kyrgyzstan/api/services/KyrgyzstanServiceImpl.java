package com.datapath.ocds.kyrgyzstan.api.services;

import com.datapath.ocds.kyrgyzstan.api.EventMerger;
import com.datapath.ocds.kyrgyzstan.api.dao.EventRepository;
import com.datapath.ocds.kyrgyzstan.api.dao.ReleaseRepository;
import com.datapath.ocds.kyrgyzstan.api.events.*;
import com.datapath.ocds.kyrgyzstan.api.releases.Release;
import com.datapath.ocds.kyrgyzstan.api.responses.IdResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KyrgyzstanServiceImpl implements KyrgyzstanService {

    private static final String RELEASES = "releases";
    private static final String EVENTS = "events";
    private static final String OCID = "ocid";
    private EventRepository eventRepository;
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EventMerger eventMerger;

    public KyrgyzstanServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event getLastEvent() {
        return eventRepository.findFirstByOrderByDateDesc().orElse(new Event());
    }

    private IdResponse processEvent(Event event) {
        try {
            String rawEvent = objectMapper.writeValueAsString(event);
            Query ocidQuery = new Query().addCriteria(Criteria.where(OCID).is(event.getOcid()));
            String rawRelease = mongoTemplate.findOne(ocidQuery, String.class, RELEASES);
            String release = eventMerger.process(rawEvent, rawRelease);
            log.info("trying to save release - " + event.getOcid());
            mongoTemplate.save(release, RELEASES);
            mongoTemplate.save(rawEvent, EVENTS);
            return new IdResponse(event.getId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't convert event to json", e);
        }
    }

    @Override
    public IdResponse addCNPublicationEvent(CNPublicationEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addCNUpdatedEvent(CNUpdatedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addCNAutoProlongedEvent(CNAutoProlongedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addTenderCancelledEvent(TenderCancelledEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addBidsDisclosedEvent(BidsDisclosedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addEvaluationCompletedEvent(EvaluationCompletedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addContractRegisteredEvent(ContractRegisteredEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addSupplierRefusedEvent(SupplierRefusedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addSupplierConfirmedEvent(SupplierConfirmedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addAuctionStartedEvent(AuctionStartedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addEnquiryAnsweredEvent(EnquiryAnsweredEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addEnquiryReceivedEvent(EnquiryReceivedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addComplaintReviewEvent(ComplaintReviewEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addComplaintRejectedEvent(ComplaintRejectedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addComplaintResolvedEvent(ComplaintResolvedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addReEvaluationEvent(ReEvaluationEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addComplaintReceivedEvent(ComplaintReceivedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addQualificationCompletedEvent(QualificationCompletedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addBids1StageOpenedEvent(Bids1StageOpenedEvent event) {
        return processEvent(event);
    }

    @Override
    public IdResponse addTwoStageInvitationPublishedEvent(TwoStageInvitationPublishedEvent event) {
        return processEvent(event);
    }

    @Override
    public Page<Release> getReleases(String offset, Integer page, Integer size) {
        return releaseRepository.findByDateAfterOrderByDate(offset, PageRequest.of(page, size));
    }

    @Override
    public String getRelease(String ocid) {
        Query query = new Query().addCriteria(Criteria.where(OCID).is(ocid));
        return mongoTemplate.findOne(query, String.class, RELEASES);
    }

    @Override
    public List<Event> getEvents(String ocid) {
        return eventRepository.findByOcid(ocid);
    }

    @Override
    public String getEvent(String id) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, String.class, EVENTS);
    }

}