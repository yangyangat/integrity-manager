package com.microstrategy.tools.integritymanager.constant.enums;

import com.microstrategy.webapi.EnumDSSXMLObjectTypes;

public enum EnumExecutableType {
    ExecutableTypeReserved("ExecutableTypeReserved", 0),
    ExecutableTypeReport("ExecutableTypeReport", 1),
    ExecutableTypeDossier("ExecutableTypeDossier", 2),
    ExecutableTypeDocument("ExecutableTypeDocument", 3);

    private final String type;
    private final int value;

    EnumExecutableType(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public static EnumExecutableType fromString(String text) {
        for (EnumExecutableType v : EnumExecutableType.values()) {
            if (String.valueOf(v.type).equals(text)) {
                return v;
            }
        }
        return null;
    }

    public static EnumExecutableType fromInt(int value) {
        for (EnumExecutableType v : EnumExecutableType.values()) {
            if (v.value == value) {
                return v;
            }
        }
        return null;
    }

    public static EnumExecutableType fromObjectTypeAndViewMedial(int objectType, int viewMedia) {
        if (objectType == EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition)
            return ExecutableTypeReport;
        else if (objectType ==  EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition) {
            return EnumViewMedia.isDossier(viewMedia) ? ExecutableTypeDossier : ExecutableTypeDocument;
        }
        return ExecutableTypeReserved;
    }
}


