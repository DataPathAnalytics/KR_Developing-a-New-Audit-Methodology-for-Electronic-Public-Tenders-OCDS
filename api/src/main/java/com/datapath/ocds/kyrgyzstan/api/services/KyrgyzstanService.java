package com.datapath.ocds.kyrgyzstan.api.services;

import com.datapath.ocds.kyrgyzstan.api.events.*;
import com.datapath.ocds.kyrgyzstan.api.responses.IdResponse;
import com.datapath.ocds.kyrgyzstan.api.releases.Release;
import org.springframework.data.domain.Page;

import java.util.List;

public interface KyrgyzstanService {

    Event getLastEvent();

    IdResponse addCNPublicationEvent(CNPublicationEvent event);

    IdResponse addCNUpdatedEvent(CNUpdatedEvent event);

    IdResponse addCNAutoProlongedEvent(CNAutoProlongedEvent event);

    IdResponse addTenderCancelledEvent(TenderCancelledEvent event);

    IdResponse addBidsDisclosedEvent(BidsDisclosedEvent event);

    IdResponse addEvaluationCompletedEvent(EvaluationCompletedEvent event);

    IdResponse addContractRegisteredEvent(ContractRegisteredEvent event);

    IdResponse addSupplierRefusedEvent(SupplierRefusedEvent event);

    IdResponse addSupplierConfirmedEvent(SupplierConfirmedEvent event);

    IdResponse addAuctionStartedEvent(AuctionStartedEvent event);

    IdResponse addEnquiryAnsweredEvent(EnquiryAnsweredEvent event);

    IdResponse addEnquiryReceivedEvent(EnquiryReceivedEvent event);

    IdResponse addComplaintReviewEvent(ComplaintReviewEvent event);

    IdResponse addComplaintRejectedEvent(ComplaintRejectedEvent event);

    IdResponse addComplaintResolvedEvent(ComplaintResolvedEvent event);

    IdResponse addReEvaluationEvent(ReEvaluationEvent event);

    IdResponse addComplaintReceivedEvent(ComplaintReceivedEvent event);

    IdResponse addQualificationCompletedEvent(QualificationCompletedEvent event);

    IdResponse addBids1StageOpenedEvent(Bids1StageOpenedEvent event);

    IdResponse addTwoStageInvitationPublishedEvent(TwoStageInvitationPublishedEvent event);

    Page<Release> getReleases(String offset, Integer page, Integer size);

    String getRelease(String ocid);

    List<Event> getEvents(String ocid);

    String getEvent(String id);
}
