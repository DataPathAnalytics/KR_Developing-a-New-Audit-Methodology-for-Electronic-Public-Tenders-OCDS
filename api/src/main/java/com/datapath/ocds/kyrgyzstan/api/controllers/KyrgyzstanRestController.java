package com.datapath.ocds.kyrgyzstan.api.controllers;

import com.datapath.ocds.kyrgyzstan.api.events.*;
import com.datapath.ocds.kyrgyzstan.api.releases.Release;
import com.datapath.ocds.kyrgyzstan.api.responses.EventsResponse;
import com.datapath.ocds.kyrgyzstan.api.responses.IdResponse;
import com.datapath.ocds.kyrgyzstan.api.services.KyrgyzstanService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class KyrgyzstanRestController {

    private KyrgyzstanService service;

    public KyrgyzstanRestController(KyrgyzstanService service) {
        this.service = service;
    }

    @GetMapping("events/last")
    @ApiOperation("Receive last saved event")
    public Event getLastEvent() {
        return service.getLastEvent();
    }

    @PostMapping("events/cn-publication")
    @ApiOperation("Save new CN Publication event")
    public IdResponse addCNPublicationEvent(@RequestBody CNPublicationEvent event) {
        return service.addCNPublicationEvent(event);
    }

    @PostMapping("events/cn-updated")
    @ApiOperation("Save new CN Updated event")
    public IdResponse addCNUpdatedEvent(@RequestBody CNUpdatedEvent event) {
        return service.addCNUpdatedEvent(event);
    }

    @PostMapping("events/cn-auto-prolonged")
    @ApiOperation("Save new CN auto prolonged event")
    public IdResponse addCNAutoProlongedEvent(@RequestBody CNAutoProlongedEvent event) {
        return service.addCNAutoProlongedEvent(event);
    }

    @PostMapping("events/tender-cancelled")
    @ApiOperation("Save new Tender cancellation event")
    public IdResponse addTenderCancelledEvent(@RequestBody TenderCancelledEvent event) {
        return service.addTenderCancelledEvent(event);
    }

    @PostMapping("events/bids-disclosed")
    @ApiOperation("Save new Bids disclosed event")
    public IdResponse addBidsDisclosedEvent(@RequestBody BidsDisclosedEvent event) {
        return service.addBidsDisclosedEvent(event);
    }

    @PostMapping("events/evaluation-completed")
    @ApiOperation("Save new Evaluation completed event")
    public IdResponse addEvaluationCompletedEvent(@RequestBody EvaluationCompletedEvent event) {
        return service.addEvaluationCompletedEvent(event);
    }

    @PostMapping("events/contract-registered")
    @ApiOperation("Save new Contract Registered event")
    public IdResponse addContractRegisteredEvent(@RequestBody ContractRegisteredEvent event) {
        return service.addContractRegisteredEvent(event);
    }

    @PostMapping("events/supplier-refused")
    @ApiOperation("Save new Supplier refused event")
    public IdResponse addSupplierRefusedEvent(@RequestBody SupplierRefusedEvent event) {
        return service.addSupplierRefusedEvent(event);
    }

    @PostMapping("events/supplier-confirmed")
    @ApiOperation("Save new Supplier confirmed event")
    public IdResponse addSupplierConfirmedEvent(@RequestBody SupplierConfirmedEvent event) {
        return service.addSupplierConfirmedEvent(event);
    }

    @PostMapping("events/auction-started")
    @ApiOperation("Save new Auction started event")
    public IdResponse addAuctionStartedEvent(@RequestBody AuctionStartedEvent event) {
        return service.addAuctionStartedEvent(event);
    }

    @PostMapping("events/enquiry-answered")
    @ApiOperation("Save new Enquiry Answered event")
    public IdResponse addEnquiryAnsweredEvent(@RequestBody EnquiryAnsweredEvent event) {
        return service.addEnquiryAnsweredEvent(event);
    }

    @PostMapping("events/enquiry-received")
    @ApiOperation("Save new Enquiry Received event")
    public IdResponse addEnquiryReceivedEvent(@RequestBody EnquiryReceivedEvent event) {
        return service.addEnquiryReceivedEvent(event);
    }

    @PostMapping("events/complaint-review")
    @ApiOperation("Save new Complaint Review event")
    public IdResponse sddComplaintReviewEvent(@RequestBody ComplaintReviewEvent event) {
        return service.addComplaintReviewEvent(event);
    }

    @PostMapping("events/complaint-rejected")
    @ApiOperation("Save new Complaint Rejected event")
    public IdResponse addComplaintRejectedEvent(@RequestBody ComplaintRejectedEvent event) {
        return service.addComplaintRejectedEvent(event);
    }

    @PostMapping("events/complaint-resolved")
    @ApiOperation("Save new Complaint Resolved event")
    public IdResponse addComplaintResolvedEvent(@RequestBody ComplaintResolvedEvent event) {
        return service.addComplaintResolvedEvent(event);
    }

    @PostMapping("events/re-evaluation")
    @ApiOperation("Save new  Re-evaluation event")
    public IdResponse addReEvaluationEvent(@RequestBody ReEvaluationEvent event) {
        return service.addReEvaluationEvent(event);
    }

    @PostMapping("events/complaint-received")
    @ApiOperation("Save new Complaint Received event")
    public IdResponse addComplaintReceivedEvent(@RequestBody ComplaintReceivedEvent event) {
        return service.addComplaintReceivedEvent(event);
    }

    @PostMapping("events/qualification-completed")
    @ApiOperation("Save new Qualification Completed event")
    public IdResponse addQualificationCompletedEvent(@RequestBody QualificationCompletedEvent event) {
        return service.addQualificationCompletedEvent(event);
    }

    @PostMapping("events/bids1-stage-opened")
    @ApiOperation("Save new Bids 1 Stage Opened event")
    public IdResponse addBids1StageOpenedEvent(@RequestBody Bids1StageOpenedEvent event) {
        return service.addBids1StageOpenedEvent(event);
    }

    @PostMapping("events/two-stage-invitation-published")
    @ApiOperation("Save new Two Stage Invitation Published event")
    public IdResponse addTwoStageInvitationPublishedEvent(@RequestBody TwoStageInvitationPublishedEvent event) {
        return service.addTwoStageInvitationPublishedEvent(event);
    }

    @GetMapping("releases")
    @ApiOperation("Get releases by offset,page and size")
    public Page<Release> getReleases(@RequestParam(defaultValue = "2010-01-01") String offset,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "100") Integer size) {
        return service.getReleases(offset, page, size);
    }

    @GetMapping("releases/{ocid}")
    @ApiOperation("Get release by ocid")
    public String getRelease(@PathVariable String ocid) {
        return service.getRelease(ocid);
    }

    @GetMapping("releases/{ocid}/events")
    @ApiOperation("Get events by ocid")
    public EventsResponse getEvents(@PathVariable String ocid) {
        return new EventsResponse(service.getEvents(ocid));
    }

    @GetMapping("events/{id}")
    @ApiOperation("Get event by id")
    public String getEvent(@PathVariable String id) {
        return service.getEvent(id);
    }

}