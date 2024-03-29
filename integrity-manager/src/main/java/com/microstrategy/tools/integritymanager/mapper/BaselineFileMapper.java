package com.microstrategy.tools.integritymanager.mapper;

import com.microstrategy.next.generation.matester.models.RwdTreeStructureJson;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutableType;
import com.microstrategy.tools.integritymanager.model.bo.*;
import com.microstrategy.tools.integritymanager.model.entity.convertor.DataConvertor;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.DocumentTreeStructureJson;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.DossierTreeStructureJson;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.data.DataDiffHolderJson;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.sql.SqlDiffHolderJson;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.sql.SqlDiffMetadataJson;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.summary.SummaryJson;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.upgradeimpacts.UpgradeImpactsHolderJson;
import com.microstrategy.tools.integritymanager.util.FileUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class BaselineFileMapper {

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class BaselineInfoFileSystem {
        private ValidataionInfo basicInfo;

        private String baselineFullPath;
        private String sourceBaselinePath;
        private String targetBaselinePath;
        private String diffResutlsPath;
        private String summaryFile;
        private String upgradeImpactsFile;
        private Map<String, String> mapOfObjectsDiffPath = new HashMap<>();
        private Map<String, String> sourceObjectIdToFileName = new HashMap<>();
        private Map<String, String> targetObjectIdToFileName = new HashMap<>();

    }

    private final ConcurrentMap<String, BaselineInfoFileSystem> mapOfBaselineInfos;

    public BaselineFileMapper() {
        mapOfBaselineInfos = new ConcurrentHashMap<>();
    }

    public void initBaseline(ValidataionInfo validationInfo) throws IOException {
        String jobId = validationInfo.getJobId();
        String sourceLibraryUrl = validationInfo.getSourceLibraryUrl();
        List<String> sourceObjectIds = validationInfo.getSourceObjectIds();
        String targetLibraryUrl = validationInfo.getTargetLibraryUrl();
        List<String> targetObjectIds = validationInfo.getTargetObjectIds();

        BaselineInfoFileSystem baselineInfo = new BaselineInfoFileSystem().setBasicInfo(validationInfo);

        String baselineFullPathString = Files.createDirectories(Paths.get(getBaselineDirectory(), newBaselineFolder(jobId))).toAbsolutePath().toString();
        String sourceBaselinePath = Files.createDirectory(Paths.get(baselineFullPathString, getEnvironmentName(sourceLibraryUrl) + "_0")).toString();
        String targetBaselinePath = Files.createDirectory(Paths.get(baselineFullPathString, getEnvironmentName(targetLibraryUrl) + "_1")).toString();
        baselineInfo.setBaselineFullPath(baselineFullPathString)
                .setSourceBaselinePath(sourceBaselinePath)
                .setTargetBaselinePath(targetBaselinePath);

        String diffResultsPathString = Files.createDirectories(Paths.get(baselineFullPathString, "/summary/results/diff_results/app_objects")).toAbsolutePath().toString();
        
        baselineInfo.setDiffResutlsPath(diffResultsPathString)
                .setSummaryFile(Files.createFile(Paths.get(baselineFullPathString, "/summary/results/summary.json")).toString())
                .setUpgradeImpactsFile(Files.createFile(Paths.get(baselineFullPathString, "/summary/results/UpgradeImpacts.json")).toString());

        Map<String, String> mapOfObjectsDiffPath = new HashMap<>();
        Map<String, String> sourceObjectIdToFileName = new HashMap<>();
        for (ListIterator<String> it = sourceObjectIds.listIterator(); it.hasNext(); ) {
            String objId = it.next();
            String singleDiffPathString = Files.createDirectory(Paths.get(diffResultsPathString, String.format("%d_%s", it.previousIndex() + 1, objId))).toAbsolutePath().toString();
            Files.createFile(Paths.get(singleDiffPathString, "data_diff.json"));
            Files.createFile(Paths.get(singleDiffPathString, "sql_diff.json"));
            Files.createFile(Paths.get(singleDiffPathString, "pdf_diff.json"));

            String sourceObjectFileName = String.format("%d_%s", it.previousIndex() + 1, objId);
            sourceObjectIdToFileName.put(objId, sourceObjectFileName);

            mapOfObjectsDiffPath.put(objId, singleDiffPathString);
        }

        baselineInfo.setMapOfObjectsDiffPath(mapOfObjectsDiffPath);
        baselineInfo.setSourceObjectIdToFileName(sourceObjectIdToFileName);

        Map<String, String> targetObjectIdToFileName = new HashMap<>();
        for (ListIterator<String> it = targetObjectIds.listIterator(); it.hasNext(); ) {
            String objId = it.next();

            String targetObjectFileName = String.format("%d_%s", it.previousIndex() + 1, objId);
            targetObjectIdToFileName.put(objId, targetObjectFileName);
        }

        baselineInfo.setTargetObjectIdToFileName(targetObjectIdToFileName);

        mapOfBaselineInfos.put(jobId, baselineInfo);
    }

    public void updateSourceBaseline(String jobId, String objectId, ExecutionResult result) throws IOException {
        BaselineInfoFileSystem baselineInfo = mapOfBaselineInfos.get(jobId);
        String objectFileName = baselineInfo.getSourceObjectIdToFileName().get(objectId);

        EnumExecutableType type = result.getExecutableType();
        if (type == EnumExecutableType.ExecutableTypeReport) {
            ReportExecutionResult reportResult = result.getReportExecutionResult();
            Files.writeString(Paths.get(baselineInfo.getSourceBaselinePath(), objectFileName + ".json"), reportResult.getReport());
            if (StringUtils.hasLength(reportResult.getSql())) {
                Files.writeString(Paths.get(baselineInfo.getSourceBaselinePath(), objectFileName + ".sql"), reportResult.getSql());
            }
            if (StringUtils.hasLength(result.getPdfInString())) {
                byte[] decodedBytes = Base64.getDecoder().decode(result.getPdfInString());
                Files.write(Paths.get(baselineInfo.getSourceBaselinePath(), objectFileName + ".pdf"), decodedBytes);
            }
        }
        else if (type == EnumExecutableType.ExecutableTypeDossier
                || type == EnumExecutableType.ExecutableTypeDocument) {
            Path resultPath = Paths.get(baselineInfo.getSourceBaselinePath(), objectFileName);
            if (!Files.exists(resultPath)) {
                Files.createDirectory(resultPath);
            }
            saveDossierExecutionResult(result, resultPath);
        }
    }

    // Support both Dossier and Document
    private void saveDossierExecutionResult(ExecutionResult result, Path resultPath) throws IOException {
        // Save data & definition and sql of each noode
        result.getMapOfVizResult().forEach((key, vizResult) -> {
            try {
                if (StringUtils.hasLength(vizResult.getReport())) {
                    Files.writeString(Paths.get(resultPath.toString(), key + ".json"), vizResult.getReport());
                }
                else if (vizResult.getGridDataInCSV() != null) {
                    Files.write(Paths.get(resultPath.toString(), key + ".csv"), vizResult.getGridDataInCSV());
                }
                Files.writeString(Paths.get(resultPath.toString(), key + ".sql"), vizResult.getSql());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Save PDF result
        if (StringUtils.hasLength(result.getPdfInString())) {
            byte[] decodedBytes = Base64.getDecoder().decode(result.getPdfInString());
            Files.write(Paths.get(resultPath.toString() + ".pdf"), decodedBytes);
        }

        // Save Excel result
        byte[] excelInByte = result.getExcelInByte();
        if (excelInByte != null && excelInByte.length > 0) {
            Files.write(Paths.get(resultPath.toString() + ".xlsx"), excelInByte);
        }
    }

    public void updateTargetBaseline(String jobId, String objectId, ExecutionResult result) throws IOException {
        BaselineInfoFileSystem baselineInfo = mapOfBaselineInfos.get(jobId);
        String objectFileName = baselineInfo.getTargetObjectIdToFileName().get(objectId);

        EnumExecutableType type = result.getExecutableType();
        if (type == EnumExecutableType.ExecutableTypeReport) {
            ReportExecutionResult reportResult = result.getReportExecutionResult();
            Files.writeString(Paths.get(baselineInfo.getTargetBaselinePath(), objectFileName + ".json"), reportResult.getReport());
            Files.writeString(Paths.get(baselineInfo.getTargetBaselinePath(), objectFileName + ".sql"), reportResult.getSql());
        }
        else if (type == EnumExecutableType.ExecutableTypeDossier
                || type == EnumExecutableType.ExecutableTypeDocument) {
            Path resultPath = Paths.get(baselineInfo.getTargetBaselinePath(), objectFileName);
            if (!Files.exists(resultPath)) {
                Files.createDirectory(resultPath);
            }
            saveDossierExecutionResult(result, resultPath);
        }
    }

    public void updateComparisonResult(String jobId, String objectId,
                                       ComparisonResult comparisonResult, ExecutionResult source, ExecutionResult target)
            throws IOException {
        if (comparisonResult.getExecutableType() == EnumExecutableType.ExecutableTypeReport) {
            updateReportComparisonResult(jobId, objectId, comparisonResult, source, target);
        }
        else if (comparisonResult.getExecutableType() == EnumExecutableType.ExecutableTypeDossier
                || comparisonResult.getExecutableType() == EnumExecutableType.ExecutableTypeDocument) {
            updateDocumentComparisonResult(jobId, objectId, comparisonResult, source, target);
        }
    }

    private void updateDocumentComparisonResult(String jobId, String objectId,
                                       ComparisonResult comparisonResult, ExecutionResult source, ExecutionResult target)
            throws IOException {
        // Update the baselines for each node
        comparisonResult.getMapOfComparisons().forEach((key, vizComparisonResult) -> {
            try {
                ReportExecutionResult sourceViz = source.getMapOfVizResult().get(key);
                ReportExecutionResult targetViz = target.getMapOfVizResult().get(key);
                updateVizComparisonResult(jobId, objectId, key, vizComparisonResult, sourceViz, targetViz);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        // Update dossier/document definition
        // Assume the tree structures are the same between source and target. TODO
        EnumExecutableType executableType = comparisonResult.getExecutableType();
        RwdTreeStructureJson docDefinition = executableType == EnumExecutableType.ExecutableTypeDossier
                ? DossierTreeStructureJson.buildRwdTreeStructureJson(source.getHierarchyDefinition())
                : DocumentTreeStructureJson.buildRwdTreeStructureJson(source.getDocumentDefinition(), source) ;
        BaselineInfoFileSystem baselineInfo = mapOfBaselineInfos.get(jobId);
        String diffPath = baselineInfo.mapOfObjectsDiffPath.get(objectId);
        String definitionFile = Paths.get(diffPath, "tree_structure.json").toAbsolutePath().toString();
        FileUtil.jsonObjectToFile(docDefinition, definitionFile);
    }

    private void updateReportComparisonResult(String jobId, String objectId,
                                              ComparisonResult comparisonResult, ExecutionResult source, ExecutionResult target)
            throws IOException {
        updateVizComparisonResult(jobId, objectId, null, comparisonResult.getReportComparisonResult(),
                source.getReportExecutionResult(), target.getReportExecutionResult());
    }

    private void updateVizComparisonResult(String jobId, String objectId, String key,
                                           ReportComparisonResult comparisonResult, ReportExecutionResult source, ReportExecutionResult target)
            throws IOException {
        //update data comparison
//        List<List<Object>> sourceData = DataConvertor.restToFileSystem(source.getReport());
//        List<List<Object>> targetData = DataConvertor.restToFileSystem(target.getReport());
        List<List<Object>> sourceData = source.getGridData();
        List<List<Object>> targetData = target.getGridData();
        //ReportComparisonResult reportComparisonResult = comparisonResult.getReportComparisonResult();
        boolean[][] diff = comparisonResult.getDiff();
        this.updateDataDiff(jobId, objectId, key, sourceData, targetData, diff);

        //update sql comparison
        int sourceSqlDiff[] = comparisonResult.getSourceSqlDiff();
        int targetSqlDiff[] = comparisonResult.getTargetSqlDiff();
        this.updateSqlDiff(jobId, objectId, key, sourceSqlDiff, targetSqlDiff);
    }

    public void updateValidationSummary(String jobId, List<ValidationResult> validationResultSet) throws IOException {
        SummaryJson summaryInJson = SummaryJson.build(validationResultSet);
        BaselineInfoFileSystem baselineInfo = mapOfBaselineInfos.get(jobId);
        FileUtil.jsonObjectToFile(summaryInJson, baselineInfo.getSummaryFile());
    }

    private void updateDataDiff(String jobId, String objId, List<List<Object>> sourceData, List<List<Object>> targetData, boolean [][] diff) throws IOException {
        updateDataDiff(jobId, objId, null, sourceData, targetData, diff);
    }

    private void updateDataDiff(String jobId, String objId, String key,
                               List<List<Object>> sourceData, List<List<Object>> targetData, boolean [][] diff) throws IOException {
        // Find the folder path of the data diff file
        BaselineInfoFileSystem baselineInfo = mapOfBaselineInfos.get(jobId);
        String diffPath = baselineInfo.mapOfObjectsDiffPath.get(objId);

        Path filePath = StringUtils.hasLength(key) ? Paths.get(diffPath, key + "_data_diff.json") : Paths.get(diffPath, "data_diff.json");
        String dataDiffFile = filePath.toAbsolutePath().toString();

        // Construct the DTO model
        DataDiffHolderJson dataDiffHolderJson = DataDiffHolderJson.build("");
        dataDiffHolderJson.populateBase(sourceData, diff);
        dataDiffHolderJson.populateTarget(targetData, diff);

        // Save the diff
        FileUtil.jsonObjectToFile(dataDiffHolderJson, dataDiffFile);
    }

    public void updateUpgradeImpacts(String jobId, UpgradeImpactsHolderJson upgradeImpacts) throws IOException {
        BaselineInfoFileSystem baselineInfo = mapOfBaselineInfos.get(jobId);
        FileUtil.jsonObjectToFile(upgradeImpacts, baselineInfo.getUpgradeImpactsFile());
    }

    public void updateSqlDiff(String jobId, String objectId, String key,
                              int[] sourceSqlDiff, int[] targetSqlDiff) throws IOException {
        SqlDiffHolderJson sqlDiffHolderJson = new SqlDiffHolderJson();

        BaselineInfoFileSystem baselineInfo = mapOfBaselineInfos.get(jobId);
        String diffPath = baselineInfo.mapOfObjectsDiffPath.get(objectId);

        String sourceBaselinePath = baselineInfo.getSourceBaselinePath();
        String sourceBaselineFolder = Paths.get(sourceBaselinePath).getFileName().toString();

        String objectFileName = baselineInfo.getTargetObjectIdToFileName().get(objectId);
        Path sourceSqlPath = StringUtils.hasLength(key) ?
                Paths.get(baselineInfo.getSourceBaselinePath(), objectFileName, key + ".sql")
                : Paths.get(baselineInfo.getSourceBaselinePath(), objectFileName + ".sql");
        Path sourceRelativeSqlFilePath = Paths.get(diffPath).relativize(sourceSqlPath);

        SqlDiffMetadataJson sourceSqlDiffMetadataJson = SqlDiffMetadataJson.build()
                //TODO, to get and set the sql contain unicode info
//                .setSqlsContainUnicode(determineSqlsContainUnicode(this.baseExecutionResult,
//                        rwDiffNode, rwDiffNode == null ? null : rwDiffNode::getBaseSqlsContainUnicode))
                .setResultFolder(sourceBaselineFolder);
        sqlDiffHolderJson.populateBase(sourceSqlDiff, sourceRelativeSqlFilePath.toString(), sourceSqlDiffMetadataJson);

        String targetBaselinePath = baselineInfo.getTargetBaselinePath();
        String targetBaselineFolder = Paths.get(targetBaselinePath).getFileName().toString();

        Path targetSqlPath = StringUtils.hasLength(key) ?
                Paths.get(baselineInfo.getTargetBaselinePath(), objectFileName, key + ".sql")
                : Paths.get(baselineInfo.getTargetBaselinePath(), objectFileName + ".sql");
        Path targetRelativeSqlFilePath = Paths.get(diffPath).relativize(targetSqlPath);
        SqlDiffMetadataJson targetSqlDiffMetadataJson = SqlDiffMetadataJson.build()
                //TODO, to get and set the sql contain unicode info
//                .setSqlsContainUnicode(determineSqlsContainUnicode(this.baseExecutionResult,
//                        rwDiffNode, rwDiffNode == null ? null : rwDiffNode::getBaseSqlsContainUnicode))
                .setResultFolder(targetBaselineFolder);
        sqlDiffHolderJson.populateTarget(targetSqlDiff, targetRelativeSqlFilePath.toString(), targetSqlDiffMetadataJson);

        String sqlDiffFile = StringUtils.hasLength(key) ?
                Paths.get(diffPath, key + "_sql_diff.json").toAbsolutePath().toString()
                : Paths.get(diffPath, "sql_diff.json").toAbsolutePath().toString();
        FileUtil.jsonObjectToFile(sqlDiffHolderJson, sqlDiffFile);
    }

    private String getBaselineDirectory() {
        return System.getProperty("user.home");
    }

    private String newBaselineFolder(String jobId) {
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
        return timeStamp + "_" + jobId;
    }

    private String getEnvironmentName(String environmentUrl) {
        try {
            URI uri = new URI(environmentUrl);
            return uri.getHost();
        }
        catch (URISyntaxException e) {
            return "";
        }
    }
}
