package com.datapath.ocds.kyrgyzstan.exporter.requests;

import com.datapath.ocds.kyrgyzstan.exporter.catalogues.LegalForm;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LegalFormsRequest {

    private List<LegalForm> legalForms;

}
