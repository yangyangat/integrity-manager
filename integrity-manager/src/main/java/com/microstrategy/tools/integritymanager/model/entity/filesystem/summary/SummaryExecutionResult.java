package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutionStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executed;

class SummaryExecutionResult {
    @JsonProperty("name")
    String name;

    @JsonProperty("id")
    String id;

    @JsonProperty("path")
    String path;

    @JsonProperty("desc")
    String description;

    @JsonProperty("type")
    int type;

    @JsonProperty("subtype")
    int subType;

    @JsonProperty("creation_time")
    String creationTime;

    @JsonProperty("modification_time")
    String modificationTime;

    @JsonProperty("credention_index")
    int credentionIndex;

    @JsonProperty("view_media")
    int viewMedia;

    @JsonProperty("execution_status")
    String executionStatus;

    @JsonProperty("execution_status_val")
    int executionStatusVal;

    @JsonProperty("detailed_exec_status")
    String detailedExecutionStatus;

    @JsonProperty("prev_detailed_exec_status")
    String prevDetailedExecStatus;

    @JsonProperty("prompt_detail")
    String promptDetail;

    @JsonProperty("rows_generated")
    int rowsGenerated;

    @JsonProperty("cols_generated")
    int colsGenerated;

    @JsonProperty("report_row_page_size")
    int reportRowPageSize;

    @JsonProperty("note_file")
    String noteFile;

    /**
     * Execution time in million seconds.
     */
    @JsonProperty("execution_time")
    long executionTime;

    public static SummaryExecutionResult build(ExecutableSet executableSet,
                                               Executable executable, Executed resultInfo) {
        if (executableSet == null) {
            return null;
        }

        if (executable == null) {
            return null;
        }

        SummaryExecutionResult summaryExecutionResult = new SummaryExecutionResult();
        summaryExecutionResult.name = executable.getName();
        summaryExecutionResult.id = executable.getID();
        summaryExecutionResult.path = executable.getPath();
        summaryExecutionResult.description = executable.getDesc();
        summaryExecutionResult.type = executable.getType();
        summaryExecutionResult.subType = executable.getSubType();
        summaryExecutionResult.creationTime = executable.getCreationTime();
        summaryExecutionResult.modificationTime = executable.getModificationTime();
        summaryExecutionResult.credentionIndex = executable.getCredIndex();
        summaryExecutionResult.viewMedia = executable.getViewMedia();

        EnumExecutionStatus executionStatus = calculateExecutionStatus(executableSet, resultInfo);
        summaryExecutionResult.executionStatus = executionStatus.toString();
        summaryExecutionResult.executionStatusVal = executionStatus.getType();
        summaryExecutionResult.detailedExecutionStatus = resultInfo.getDetailedExecStatus();
        summaryExecutionResult.prevDetailedExecStatus = resultInfo.getPrevDetailedStatus();
        summaryExecutionResult.promptDetail = resultInfo.getPromptAnsDetail();
        summaryExecutionResult.rowsGenerated = resultInfo.getDataRowsGenerated();
        summaryExecutionResult.colsGenerated = resultInfo.getDataColsGenerated();
        summaryExecutionResult.reportRowPageSize = resultInfo.getReportRowPageSize();
        summaryExecutionResult.executionTime = getAverageExecutionTime(resultInfo.getExecutionTime());
        //summaryExecutionResult.noteFile = executionResult instanceof StoredExecutionResult
        //        ? ((StoredExecutionResult) executionResult).getNoteLocation() : "";
        summaryExecutionResult.noteFile = "";
        return summaryExecutionResult;
    }

    static EnumExecutionStatus calculateExecutionStatus(ExecutableSet executableSet, Executed resultInfo) {
        EnumExecutionStatus executionStatus = SummaryJsonUtils.calculateExecutionStatus(executableSet);
        if (executionStatus == EnumExecutionStatus.COMPLETED) {
            return executionStatus;
        }

        return resultInfo.getExecutionStatus();


    }

    static long getAverageExecutionTime(long [][] executionTimes) {
        if (executionTimes == null || executionTimes.length == 0) {
            return -1;
        }

        long sum = 0;
        int validCount = 0;
        for (long [] ets : executionTimes) {
            if (ets[1] >= 0) {
                sum += ets[1];
                ++validCount;
            }
        }
        if (validCount == 0) {
            return -1;
        }
        return sum / validCount;
    }
}
