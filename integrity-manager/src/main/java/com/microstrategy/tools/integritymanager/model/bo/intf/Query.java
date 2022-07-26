package com.microstrategy.tools.integritymanager.model.bo.intf;

public interface Query {
    String getSql();

    default String getQueryDetails() {
        throw new UnsupportedOperationException();
    }
}
