package com.microstrategy.tools.integritymanager.model.entity.mstr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.microstrategy.tools.integritymanager.constant.enums.EnumViewMedia;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ObjectInfo {
    private String name;
    private String id;
    private int type;
    private int subtype;
    private String dateCreated;
    private String dateModified;
    private String version;
    private int acg;
    private JsonNode owner;

    @JsonProperty("acl")
    private List<JsonNode> aclList;

    private int extType;
    private int viewMedia;
    private List<JsonNode> ancestors;
    private JsonNode certifiedInfo;

    public boolean isDossier() {
        return type == EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition
                && EnumViewMedia.isDossier(viewMedia);
    }
}
