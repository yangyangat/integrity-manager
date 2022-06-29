package com.microstrategy.tools.integritymanager.model.entity.filesystem.sql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SqlDiffHolderJson {
    @JsonProperty("base_sql")
    SqlDiffJson baseSql;

    @JsonProperty("target_sql")
    SqlDiffJson targetSql;

    public void populateBase(int [] intervals, String fileToSqlPath, SqlDiffMetadataJson sqlDiffMetadataJson) {
        this.baseSql = SqlDiffJson.build(intervals, fileToSqlPath, sqlDiffMetadataJson);
    }

    public void populateTarget(int [] intervals, String fileToSqlPath, SqlDiffMetadataJson sqlDiffMetadataJson) {
        this.targetSql = SqlDiffJson.build(intervals, fileToSqlPath, sqlDiffMetadataJson);
    }

    /**
     * Get metadata by result folder.
     * @param relativeFolder The result folder.
     * @return The metadata.
     */
    public SqlDiffMetadataJson getMetadataByRelativeResultFolder(String relativeFolder) {
        SqlDiffMetadataJson base = this.baseSql.getSqlDiffMetadataJson();
        SqlDiffMetadataJson target = this.targetSql.getSqlDiffMetadataJson();

        if (base.resultFolder.equals(relativeFolder)) {
            return base;
        }

        if (target.resultFolder.equals(relativeFolder)) {
            return target;
        }

        return null;
    }
}
