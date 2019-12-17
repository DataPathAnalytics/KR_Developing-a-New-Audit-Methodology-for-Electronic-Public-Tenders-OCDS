package com.datapath.ocds.kyrgyzstan.api.responses;

import com.datapath.ocds.kyrgyzstan.api.requests.AteCatalog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AteCatalogResponse {

    private List<AteCatalog> catalog;

}
