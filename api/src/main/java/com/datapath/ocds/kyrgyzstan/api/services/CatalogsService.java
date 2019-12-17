package com.datapath.ocds.kyrgyzstan.api.services;

import com.datapath.ocds.kyrgyzstan.api.responses.AteCatalogResponse;
import com.datapath.ocds.kyrgyzstan.api.responses.CurrencyRatesResponse;
import com.datapath.ocds.kyrgyzstan.api.responses.LegalFormsResponse;
import com.datapath.ocds.kyrgyzstan.api.requests.AteCatalogueRequest;
import com.datapath.ocds.kyrgyzstan.api.requests.CurrencyRateRequest;
import com.datapath.ocds.kyrgyzstan.api.requests.LegalFormsRequest;

public interface CatalogsService {

    void updateAteCatalog(AteCatalogueRequest request);

    AteCatalogResponse getAteCatalog();

    void updateCurrencyRates(CurrencyRateRequest request);

    CurrencyRatesResponse getCurrencyRates();

    void updateLegalForms(LegalFormsRequest request);

    LegalFormsResponse getLegalForms();

}