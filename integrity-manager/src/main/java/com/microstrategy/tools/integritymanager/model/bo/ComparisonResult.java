package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import lombok.Getter;
import lombok.Setter;

public class ComparisonResult {
    @Setter
    @Getter
    private boolean[][] diff = {};

    @Setter
    @Getter
    private EnumComparisonStatus comparisonStatus = EnumComparisonStatus.NOT_COMPARED;
}
