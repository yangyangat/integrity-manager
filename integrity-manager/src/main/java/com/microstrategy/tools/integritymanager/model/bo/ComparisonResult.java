package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutableType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class ComparisonResult {

    private EnumExecutableType executableType;

    private EnumComparisonStatus comparisonStatus = EnumComparisonStatus.NOT_COMPARED;

    // for report
    private ReportComparisonResult reportComparisonResult;

    // for dossier and document
    private Map<String, ReportComparisonResult> mapOfComparisons;
}
