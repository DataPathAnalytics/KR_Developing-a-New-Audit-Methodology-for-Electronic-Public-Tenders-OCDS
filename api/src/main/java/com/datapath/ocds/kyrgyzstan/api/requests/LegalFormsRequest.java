package com.datapath.ocds.kyrgyzstan.api.requests;

import lombok.Data;

import java.util.List;

@Data
public class LegalFormsRequest {

    private List<LegalForm> legalForms;

}
