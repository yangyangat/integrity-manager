package com.microstrategy.tools.integritymanager.model.entity.filesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class DataDiffJson {

    @JsonProperty("inline_data")
    List<List<Object>> inlineData;

    @JsonProperty("data_file")
    DataDiffDataFileJson dataFile;

    @JsonProperty("diffs")
    List<DataDiffMatrixItemJson> diffs;

    public void populateInlineData(List<List<Object>> dataTable) {
        if (this.inlineData == null) {
            this.inlineData = new ArrayList<>();
        }
        this.inlineData.addAll(dataTable);
    }

    /**
     * For Text node.
     * @param text The text.
     * @param isDiff Whether is diff.
     */
    public void populateInlineData(String text, boolean isDiff) {

        List<Object> elements = new ArrayList<>();
        this.diffs = new ArrayList<>();
        elements.add(text);
        if (isDiff) {
            this.diffs.add(new DataDiffMatrixItemJson(0, 0));
        }

        if (this.inlineData == null) {
            this.inlineData = new ArrayList<>();
        }
        this.inlineData.add(elements);
    }

    /**
     * Populate inline data for report.
     * @param dataTable The data table.
     */
    public void populateInlineData(Vector<Vector<Object>> dataTable) {
        if (dataTable == null) {
            return;
        }
        if (this.inlineData == null) {
            this.inlineData = new Vector<>();
        }
        for (int i = 0; i < dataTable.size(); ++i) {
            Vector<Object> line = dataTable.get(i);
            if (line == null) {
                continue;
            }

            this.inlineData.add(line);
        }
    }

    /**
     * Populate the equals matrix.
     * @param equals true -> equal, false -> not equal
     */
    public void populateDiffMatrix(boolean [][] equals) {
        if (equals == null) {
            return;
        }
        diffs = new ArrayList<>();

        for (int i = 0; i < equals.length; ++i) {
            for (int j = 0; j < equals[i].length; ++j) {
                if (!equals[i][j]) {
                    diffs.add(new DataDiffMatrixItemJson(i, j));
                }
            }
        }

    }
}
