package com.datapath.ocds.kyrgyzstan.api.requests;

import lombok.Data;

import java.util.List;

@Data
public class AteCatalogueRequest {

    private List<AteCatalog> catalog;

}