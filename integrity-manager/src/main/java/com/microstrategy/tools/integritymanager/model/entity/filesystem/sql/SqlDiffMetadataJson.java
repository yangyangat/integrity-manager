package com.microstrategy.tools.integritymanager.model.entity.filesystem.sql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SqlDiffMetadataJson {

    @JsonProperty("sqls_contain_unicode")
    Integer [] sqlsContainUnicode;

    @JsonProperty("result_folder")
    String resultFolder;

    public static SqlDiffMetadataJson build() {
        return new SqlDiffMetadataJson();
    }

    public SqlDiffMetadataJson setSqlsContainUnicode(Integer [] sqlsContainUnicode) {
        this.sqlsContainUnicode = sqlsContainUnicode;
        return this;
    }

    public Integer [] getSqlsContainUnicode() {
        return sqlsContainUnicode;
    }

    public String getResultFolder() {
        return resultFolder;
    }

    public SqlDiffMetadataJson setResultFolder(String resultFolder) {
        this.resultFolder = resultFolder;
        return this;
    }


}
