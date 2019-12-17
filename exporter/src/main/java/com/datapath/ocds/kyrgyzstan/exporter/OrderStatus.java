package com.datapath.ocds.kyrgyzstan.exporter;

public enum OrderStatus {
    DRAFT(0, "draft"),
    PUBLISHED(3, "published"),
    CHANGED(4, "changed"),
    CANCELLED(5, "cancelled"),
    EVALUATION_COMPLETE(6, "evaluationComplete"),
    CONTRACT_SIGNED(7, "contractSigned"),
    BIDS_OPENED(8, "bidsOpened"),
    AUTO_PROLONGED(9, "autoProlonged"),
    EVALUATION_RESULTS_PENDING(10, "evaluationResultsPending"),
    CONTRACT_SIGN_PENDING(11, "contractSignPending"),;
    private int id;
    private String name;

    OrderStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static OrderStatus getStatus(int id) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new RuntimeException("Order status with id = " + id + " not found");
    }
}
