package com.datapath.ocds.kyrgyzstan.api.services;

import com.datapath.ocds.kyrgyzstan.api.requests.*;
import com.datapath.ocds.kyrgyzstan.api.responses.AteCatalogResponse;
import com.datapath.ocds.kyrgyzstan.api.responses.CurrencyRatesResponse;
import com.datapath.ocds.kyrgyzstan.api.responses.LegalFormsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class CatalogsServiceImpl implements CatalogsService {

    private static final String ATE = "ate";
    private static final String DATE = "date";
    private static final String CODE = "code";
    private static final String CURRENCY_RATES = "currencyRates";
    private static final String LEGAL_FORMS = "legalForms";
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void updateAteCatalog(AteCatalogueRequest request) {
        for (AteCatalog ate : request.getCatalog()) {
            mongoTemplate.save(ate, ATE);
        }
    }

    @Override
    public AteCatalogResponse getAteCatalog() {
        return new AteCatalogResponse(mongoTemplate.findAll(AteCatalog.class, ATE));
    }

    @Override
    public void updateCurrencyRates(CurrencyRateRequest request) {
        for (CurrencyRate rate : request.getRates()) {
            Query existQuery = new Query().addCriteria(
                    new Criteria().andOperator(
                            Criteria.where(DATE).is(rate.getDate()),
                            Criteria.where(CODE).is(rate.getCode())
                    ));
            boolean exists = mongoTemplate.exists(existQuery, CURRENCY_RATES);
            if (!exists) {
                mongoTemplate.save(rate, CURRENCY_RATES);
            }
        }
    }

    @Override
    public CurrencyRatesResponse getCurrencyRates() {
        return new CurrencyRatesResponse(mongoTemplate.findAll(CurrencyRate.class, CURRENCY_RATES));
    }

    @Override
    public void updateLegalForms(LegalFormsRequest request) {
        for (LegalForm form : request.getLegalForms()) {
            mongoTemplate.save(form, LEGAL_FORMS);
        }
    }

    @Override
    public LegalFormsResponse getLegalForms() {
        return new LegalFormsResponse(mongoTemplate.findAll(LegalForm.class, LEGAL_FORMS));
    }
}
