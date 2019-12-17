package com.datapath.ocds.kyrgyzstan.api.requests;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class LegalForm {

    @Id
    private String organizationId;
    private String legalForm;

}
