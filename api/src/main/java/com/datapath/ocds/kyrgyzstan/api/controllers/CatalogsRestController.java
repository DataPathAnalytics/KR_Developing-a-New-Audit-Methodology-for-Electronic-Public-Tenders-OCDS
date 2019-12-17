package com.datapath.ocds.kyrgyzstan.api.controllers;


import com.datapath.ocds.kyrgyzstan.api.requests.AteCatalogueRequest;
import com.datapath.ocds.kyrgyzstan.api.requests.CurrencyRateRequest;
import com.datapath.ocds.kyrgyzstan.api.requests.LegalFormsRequest;
import com.datapath.ocds.kyrgyzstan.api.responses.AteCatalogResponse;
import com.datapath.ocds.kyrgyzstan.api.responses.CurrencyRatesResponse;
import com.datapath.ocds.kyrgyzstan.api.responses.LegalFormsResponse;
import com.datapath.ocds.kyrgyzstan.api.services.CatalogsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/catalogs")
public class CatalogsRestController {

    @Autowired
    private CatalogsService service;

    @PostMapping("ate")
    @ApiOperation("Update all ate catalog")
    public Integer updateAteCatalog(@RequestBody AteCatalogueRequest request) {
        service.updateAteCatalog(request);
        return request.getCatalog().size();
    }

    @GetMapping("ate")
    @ApiOperation("Receive ate catalog")
    public AteCatalogResponse getAteCatalog() {
        return service.getAteCatalog();
    }

    @PostMapping("currency-rates")
    @ApiOperation("Update all currency rates")
    public Integer updateCurrencyRates(@RequestBody CurrencyRateRequest request) {
        service.updateCurrencyRates(request);
        return request.getRates().size();
    }

    @GetMapping("currency-rates")
    @ApiOperation("Get currency rates for all period")
    public CurrencyRatesResponse getCurrencyRates() {
        return service.getCurrencyRates();
    }

    @PostMapping("legal-forms")
    @ApiOperation("Update legal forms")
    public Integer updateLegalForms(@RequestBody LegalFormsRequest request) {
        service.updateLegalForms(request);
        return request.getLegalForms().size();
    }

    @GetMapping("legal-forms")
    @ApiOperation("Receive all legal forms")
    public LegalFormsResponse getLegalForms() {
        return service.getLegalForms();
    }

}
