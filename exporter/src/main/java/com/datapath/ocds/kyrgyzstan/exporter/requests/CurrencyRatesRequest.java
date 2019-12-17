package com.datapath.ocds.kyrgyzstan.exporter.requests;

import com.datapath.ocds.kyrgyzstan.exporter.catalogues.CurrencyRate;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CurrencyRatesRequest {

    private List<CurrencyRate> rates;

}
