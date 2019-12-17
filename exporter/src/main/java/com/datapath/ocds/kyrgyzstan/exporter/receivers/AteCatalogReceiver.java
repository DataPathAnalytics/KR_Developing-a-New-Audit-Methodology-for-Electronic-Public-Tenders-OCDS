package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.catalogues.AteCatalog;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.CatalogDAOService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AteCatalogReceiver {

    private CatalogDAOService dao;

    public AteCatalogReceiver(CatalogDAOService dao) {
        this.dao = dao;
    }

    public List<AteCatalog> receive() {
        List<AteCatalog> ates = new ArrayList<>();

        dao.getAtes().forEach(daoAte -> {
            AteCatalog ate = new AteCatalog();
            ate.setId(daoAte.getId());

            ate.setId(daoAte.getId());
            ate.setCode(daoAte.getCode());
            ate.setCountry(daoAte.getCountry());
            ate.setRegion(daoAte.getRegion());
            ate.setSubRegion(daoAte.getSubRegion());
            ate.setDistrict(daoAte.getDistrict());
            ate.setSubDistrict(daoAte.getSubDistrict());
            ate.setSubSubDistrict(daoAte.getSubSubDistrict());
            ate.setLocality(daoAte.getLocality());

            ates.add(ate);
        });
        return ates;
    }

}