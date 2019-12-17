package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.catalogues.CurrencyRate;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.CatalogDAOService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CurrencyRateCatalogReceiver {

    private CatalogDAOService dao;

    public CurrencyRateCatalogReceiver(CatalogDAOService dao) {
        this.dao = dao;
    }

    public List<CurrencyRate> receive() {
        List<CurrencyRate> rates = new ArrayList<>();

        dao.getRates().forEach(daoRate -> {
            CurrencyRate rate = new CurrencyRate();
            rate.setDate(daoRate.getDate());
            rate.setRate(daoRate.getRate());
            rate.setCode(daoRate.getCode());
            rate.setName(daoRate.getName());
            rates.add(rate);
        });

        return rates;
    }

}
