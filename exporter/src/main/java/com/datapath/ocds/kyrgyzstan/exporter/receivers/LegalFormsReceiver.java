package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.catalogues.LegalForm;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.CatalogDAOService;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.DASH;

@Component
public class LegalFormsReceiver {

    @Autowired
    private CatalogDAOService dao;

    public List<LegalForm> receive() {
        ArrayList<LegalForm> legalForms = new ArrayList<>();
        dao.getLegalForms().forEach(daoLegalForm -> {
            String alpha2 = CountryCode.getByCode(daoLegalForm.getCountryCode()).getAlpha2();

            LegalForm legalForm = new LegalForm();
            legalForm.setOrganizationId(alpha2 + "-INN" + DASH + daoLegalForm.getInn());
            legalForm.setLegalForm(daoLegalForm.getTitleRu());
            legalForms.add(legalForm);
        });
        return legalForms;
    }

}
