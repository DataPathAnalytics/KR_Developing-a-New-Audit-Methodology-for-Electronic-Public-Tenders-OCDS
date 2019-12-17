package com.datapath.ocds.kyrgyzstan.api.releases;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "releases")
public class Release {
    private String id;
    @Indexed(unique = true)
    private String ocid;
    @Indexed
    private String date;
}