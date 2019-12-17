package com.datapath.ocds.kyrgyzstan.exporter.requests;


import com.datapath.ocds.kyrgyzstan.exporter.catalogues.AteCatalog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AteCatalogRequest {
    private List<AteCatalog> catalog;
}