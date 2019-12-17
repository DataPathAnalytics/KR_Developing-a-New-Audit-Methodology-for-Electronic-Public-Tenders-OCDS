package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;

public interface EventManager {

    void process(JsonEvent jsonEvent);

}
