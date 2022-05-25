package com.microstrategy.tools.integritymanager.model.bo.intf;

public interface Executable {
    int getType();

    String getID();

    String getName();

    String getPath();

    int getSubType();

    int getExecID();

    String getConvertedExecId();

    String getDesc();

    String getCreationTime();

    String getModificationTime();

    int getCredIndex();

    int getViewMedia();
}
