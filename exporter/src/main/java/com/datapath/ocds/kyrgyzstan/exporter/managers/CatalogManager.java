package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.catalogues.AteCatalog;
import com.datapath.ocds.kyrgyzstan.exporter.catalogues.CurrencyRate;
import com.datapath.ocds.kyrgyzstan.exporter.catalogues.LegalForm;
import com.datapath.ocds.kyrgyzstan.exporter.receivers.AteCatalogReceiver;
import com.datapath.ocds.kyrgyzstan.exporter.receivers.CurrencyRateCatalogReceiver;
import com.datapath.ocds.kyrgyzstan.exporter.receivers.LegalFormsReceiver;
import com.datapath.ocds.kyrgyzstan.exporter.requests.AteCatalogRequest;
import com.datapath.ocds.kyrgyzstan.exporter.requests.CurrencyRatesRequest;
import com.datapath.ocds.kyrgyzstan.exporter.requests.LegalFormsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class CatalogManager {

    @Autowired
    private AteCatalogReceiver ateReceiver;
    @Autowired
    private CurrencyRateCatalogReceiver rateReceiver;
    @Autowired
    private LegalFormsReceiver legalFormsReceiver;
    @Autowired
    private RestTemplate restTemplate;

    private void updateCatalogues() {
        updateAtes();
        updateCurrencyRates();
        updateLegalForms();
    }

    private void updateAtes() {
        List<AteCatalog> ates = ateReceiver.receive();
        Integer count = restTemplate.postForObject("/catalogs/ate",
                new AteCatalogRequest(ates), Integer.class);
        log.info("Saved {} ates", count);
    }

    private void updateCurrencyRates() {
        List<CurrencyRate> rates = rateReceiver.receive();
        Integer count = restTemplate.postForObject("/catalogs/currency-rates",
                new CurrencyRatesRequest(rates), Integer.class);
        log.info("Saved {} currency rates", count);
    }

    private void updateLegalForms() {
        List<LegalForm> rates = legalFormsReceiver.receive();
        Integer count = restTemplate.postForObject("/catalogs/legal-forms",
                new LegalFormsRequest(rates), Integer.class);
        log.info("Saved {} legal forms", count);
    }

    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    public void run() {
        updateCatalogues();
    }
}
