package com.microstrategy.tools.integritymanager.constant.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumViewMedia {
    DssViewMediaReserved("DssViewMediaReserved", 0),
    DssViewMediaViewAnalysis("DssViewMediaViewAnalysis", 0x60000000),
    DssViewMediaViewAnalysisHTML5("DssViewMediaViewAnalysisHTML5", 0x70000000),
    DssViewMediaDefaultMask("DssViewMediaDefaultMask", 0xf8000000),
    DssViewMediaAvailableBitMask("DssViewMediaAvailableBitMask",
            DssViewMediaReserved.value | DssViewMediaViewAnalysis.value | DssViewMediaViewAnalysisHTML5.value );

    private final String type;
    private final int value;

    EnumViewMedia(String type, int value) {
        this.type = type;
        this.value = value;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public static EnumViewMedia fromType(String text) {
        for (EnumViewMedia v : EnumViewMedia.values()) {
            if (String.valueOf(v.type).equals(text)) {
                return v;
            }
        }
        return null;
    }

    public static EnumViewMedia fromValue(int value) {
        for (EnumViewMedia v : EnumViewMedia.values()) {
            if (v.value == value) {
                return v;
            }
        }
        return null;
    }

    public static boolean isDossier(EnumViewMedia viewMedia) {
        int type = viewMedia.getValue() & EnumViewMedia.DssViewMediaDefaultMask.getValue();
        return type == EnumViewMedia.DssViewMediaViewAnalysis.getValue()
                || type == EnumViewMedia.DssViewMediaViewAnalysisHTML5.getValue();
    }

    public static boolean isDossier(int viewMedia) {
        int type = viewMedia & EnumViewMedia.DssViewMediaDefaultMask.getValue();
        return type == EnumViewMedia.DssViewMediaViewAnalysis.getValue()
                || type == EnumViewMedia.DssViewMediaViewAnalysisHTML5.getValue();
    }
}