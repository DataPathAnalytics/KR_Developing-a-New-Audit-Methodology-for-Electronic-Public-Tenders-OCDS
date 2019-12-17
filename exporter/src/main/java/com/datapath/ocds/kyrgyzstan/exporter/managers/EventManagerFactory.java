package com.datapath.ocds.kyrgyzstan.exporter.managers;

public interface EventManagerFactory {

    EventManager getManager(int eventType);

}
