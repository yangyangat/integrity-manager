package com.microstrategy.tools.integritymanager.model.entity.filesystem.sql;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SqlDiffJson {
    @JsonProperty("inline_sql")
    String inlineSql;

    @JsonProperty("metadata")
    SqlDiffMetadataJson sqlDiffMetadataJson;

    @JsonProperty("sql_file")
    SqlDiffFileJson sqlDiffFileJson;

    @JsonProperty("diffs")
    List<SqlDiffMatrixItemJson> diffs;

    public static SqlDiffJson build(int [] intervals, String fileLocation, SqlDiffMetadataJson sqlDiffMetadataJson) {
        SqlDiffJson sqlDiffJson = new SqlDiffJson();
        if (intervals != null) {
            sqlDiffJson.diffs = new ArrayList<>();
            for (int i = 0; i < intervals.length; i += 2) {
                sqlDiffJson.diffs.add(new SqlDiffMatrixItemJson(intervals[i], intervals[i + 1]));
            }
        }

        sqlDiffJson.sqlDiffMetadataJson = sqlDiffMetadataJson;

        sqlDiffJson.sqlDiffFileJson = new SqlDiffFileJson(true, -1,
                Arrays.asList(fileLocation));

        return sqlDiffJson;
    }

    public SqlDiffMetadataJson getSqlDiffMetadataJson() {
        return sqlDiffMetadataJson;
    }
}
