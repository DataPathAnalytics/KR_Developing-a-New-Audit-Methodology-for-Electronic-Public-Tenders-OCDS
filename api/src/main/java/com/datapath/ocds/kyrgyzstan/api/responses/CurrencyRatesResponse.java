package com.datapath.ocds.kyrgyzstan.api.responses;

import com.datapath.ocds.kyrgyzstan.api.requests.CurrencyRate;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CurrencyRatesResponse {

    private List<CurrencyRate> rates;

}
