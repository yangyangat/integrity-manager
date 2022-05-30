package com.microstrategy.tools.integritymanager.model.entity.filesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.util.FileUtil;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class DataDiffHolderJson {

    @JsonProperty("base_data")
    DataDiffJson baseData;

    @JsonProperty("target_data")
    DataDiffJson targetData;

    /**
     * Build a object. If the file specified by jsonFileAbsolutePath exists, load and
     * return that object. Otherwise, create a new object.
     * @param jsonFileAbsolutePath The path to the json file.
     * @return The object.
     * @throws IOException When error occurs.
     */
    public static DataDiffHolderJson build(String jsonFileAbsolutePath) throws IOException {
        File file = new File(jsonFileAbsolutePath);
        if (file.exists()) {
            return (DataDiffHolderJson) FileUtil.jsonFileToObject(file, DataDiffHolderJson.class);
        }

        return new DataDiffHolderJson();
    }

    private DataDiffHolderJson() {

    }

    /**
     * Populate the data for the base. Will not populate the diff.
     * @param rwdNode The rwdata node.
     */
//    public void populateBase(Node rwdNode) {
//        if (rwdNode == null) {
//            return;
//        }
//        if (this.baseData == null) {
//            this.baseData = buildDataDiffJson(rwdNode);
//        } else {
//            this.baseData.populateInlineData(rwdNode);
//        }
//    }

    /**
     * Populate the data and diff for base report.
     * @param gridTable The grid data table.
     * @param diff The diff table.
     */
    public void populateBase(Vector<Vector<Object>> gridTable, boolean [][] diff) {
        if (gridTable == null) {
            return;
        }
        if (this.baseData == null) {
            this.baseData = buildDataDiffJson(gridTable, diff);
        } else {
            this.baseData.populateInlineData(gridTable);
            this.baseData.populateDiffMatrix(diff);
        }
    }

    /**
     * Populate the data and diff for base report.
     * @param gridTable The grid data table.
     * @param diff The diff table.
     */
    public void populateBase(List<List<Object>> gridTable, boolean [][] diff) {
        if (gridTable == null) {
            return;
        }
        if (this.baseData == null) {
            DataDiffJson dataDiffJson = new DataDiffJson();
            dataDiffJson.populateInlineData(gridTable);
            dataDiffJson.populateDiffMatrix(diff);
            this.baseData = dataDiffJson;
        } else {
            this.baseData.populateInlineData(gridTable);
            this.baseData.populateDiffMatrix(diff);
        }
    }

    /**
     * Populate the data for the target. Will not populate the diff.
     * @param rwdNode The rwdata node.
     */
//    public void populateTarget(Node rwdNode) {
//        if (rwdNode == null) {
//            return;
//        }
//        if (targetData == null) {
//            this.targetData = buildDataDiffJson(rwdNode);
//        } else {
//            this.targetData.populateInlineData(rwdNode);
//        }
//    }

    /**
     * Populate the data and diff for target report.
     * @param gridTable The grid table.
     * @param diff The diff table.
     */
    public void populateTarget(Vector<Vector<Object>> gridTable, boolean [][] diff) {
        if (gridTable == null) {
            return;
        }
        if (this.targetData == null) {
            this.targetData = buildDataDiffJson(gridTable, diff);
        } else {
            this.targetData.populateInlineData(gridTable);
            this.targetData.populateDiffMatrix(diff);
        }
    }

    /**
     * Populate the data and diff for target report.
     * @param gridTable The grid table.
     * @param diff The diff table.
     */
    public void populateTarget(List<List<Object>> gridTable, boolean [][] diff) {
        if (gridTable == null) {
            return;
        }
        if (this.targetData == null) {
            DataDiffJson dataDiffJson = new DataDiffJson();
            dataDiffJson.populateInlineData(gridTable);
            dataDiffJson.populateDiffMatrix(diff);
            this.targetData = dataDiffJson;
        } else {
            this.targetData.populateInlineData(gridTable);
            this.targetData.populateDiffMatrix(diff);
        }
    }

    DataDiffJson buildDataDiffJson(Vector<Vector<Object>> gridTable, boolean [][] diff) {
        DataDiffJson dataDiffJson = new DataDiffJson();
        dataDiffJson.populateInlineData(gridTable);
        dataDiffJson.populateDiffMatrix(diff);

        return dataDiffJson;
    }

//    DataDiffJson buildDataDiffJson(Node rwdNode) {
//        DataDiffJson dataDiffJson = new DataDiffJson();
//        dataDiffJson.populateInlineData(rwdNode);
//
//        return dataDiffJson;
//    }

}
