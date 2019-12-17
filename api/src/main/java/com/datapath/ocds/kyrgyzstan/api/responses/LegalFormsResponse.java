package com.datapath.ocds.kyrgyzstan.api.responses;

import com.datapath.ocds.kyrgyzstan.api.requests.LegalForm;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LegalFormsResponse {

    private List<LegalForm> legalForms;

}
