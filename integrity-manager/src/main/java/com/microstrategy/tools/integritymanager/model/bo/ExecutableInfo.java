package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;
import com.microstrategy.tools.integritymanager.model.entity.mstr.ObjectInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class ExecutableInfo implements Executable {
    private ObjectInfo objectInfo;
    private int execID;
    private String convertedExecId;
    private int credIndex;

    @Override
    public int getType() {
        return objectInfo.getType();
    }

    @Override
    public String getID() {
        return objectInfo.getId();
    }

    @Override
    public String getName() {
        return objectInfo.getName();
    }

    @Override
    public String getPath() {
        return objectInfo.getAncestors().stream()
                .map(node -> node.get("name").asText())
                .collect(Collectors.joining("\\"));
    }

    @Override
    public int getSubType() {
        return objectInfo.getSubtype();
    }

    @Override
    public String getDesc() {
        return "\n";
    }

    @Override
    public String getCreationTime() {
        return objectInfo.getDateCreated();
    }

    @Override
    public String getModificationTime() {
        return objectInfo.getDateModified();
    }

    @Override
    public int getViewMedia() {
        return objectInfo.getViewMedia();
    }
}
