package com.datapath.ocds.kyrgyzstan.exporter.managers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotYetRealizedEventManager implements EventManager {
    @Override
    public void process(JsonEvent jsonEvent) {
        log.warn("Not yet implemented event manager");
    }
}
