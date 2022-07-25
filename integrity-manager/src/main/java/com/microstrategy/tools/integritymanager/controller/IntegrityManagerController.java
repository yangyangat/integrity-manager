package com.microstrategy.tools.integritymanager.controller;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.constant.enums.EnumViewMedia;
import com.microstrategy.tools.integritymanager.model.bo.*;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.upgradeimpacts.UpgradeImpactsHolderJson;
import com.microstrategy.tools.integritymanager.model.entity.mstr.MSTRAuthToken;
import com.microstrategy.tools.integritymanager.model.entity.mstr.ObjectInfo;
import com.microstrategy.tools.integritymanager.service.intf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Controller
public class IntegrityManagerController {
    @Autowired
    private JobManager jobManager;

    @Autowired
    private LoginService loginService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private ComparisonService comparisonService;

    @Autowired
    private BaselineService baselineService;

    @GetMapping(value = "/jobs/{jobId}")
    @ResponseBody
    public ResponseEntity query(@PathVariable String jobId) {
        List<Map<String, String>> mapsOfJobStatus = this.jobManager.queryJobStatus(jobId);
        return new ResponseEntity<>(mapsOfJobStatus, HttpStatus.OK);
    }

    @PostMapping(value = "/comparison")
    @ResponseBody
    public ResponseEntity compare(@RequestParam Optional<Integer> count) {
        int countInt = count.orElse(0);
        if (countInt <= 0 && countInt != -1) {
            return new ResponseEntity("Count parameter is expected to be larger than zero", HttpStatus.BAD_REQUEST);
        }

        //TODO, read the value from config
        final int sessionCount = 5;
        String sourceLibraryUrl = "http://10.23.34.25:8080/MicroStrategyLibrary";
        //MSTRAuthToken sourceToken = loginService.login(sourceLibraryUrl, "administrator", "");
        List<MSTRAuthToken> sourceTokenList = loginService.login(sourceLibraryUrl, "administrator", "", sessionCount);
        String sourceProjectId = "B19DEDCC11D4E0EFC000EB9495D0F44F";
        ExecutorService sourceExecutionExecutors = Executors.newFixedThreadPool(5);

        //String targetLibraryUrl = "http://10.23.34.25:8080/MicroStrategyLibrary";
        String targetLibraryUrl = "http://10.27.69.70:8080/MicroStrategyLibrary";
        //MSTRAuthToken targetToken = loginService.login(targetLibraryUrl, "administrator", "");
        List<MSTRAuthToken> targetTokenList = loginService.login(targetLibraryUrl, "administrator", "", sessionCount);
        String targetProjectId = "B19DEDCC11D4E0EFC000EB9495D0F44F";
        ExecutorService targetExecutionExecutors = Executors.newFixedThreadPool(5);

        if (sourceTokenList.isEmpty() || targetTokenList.isEmpty()) {
            return new ResponseEntity("Failed to login the library server", HttpStatus.UNAUTHORIZED);
        }

        List<ExecutionPair> pairList = new ArrayList<>();

        List<String> sourceObjectIds = searchService.getTopNReportIds(sourceLibraryUrl, sourceTokenList.get(0), sourceProjectId, countInt);
        //sourceObjectIds = Arrays.asList("13CFD83A458A68655A13CBA8D7C62CD5");
        //sourceObjectIds = Arrays.asList("0A9EBE87468B751C3663818889B10D73");
        //sourceObjectIds = Arrays.asList("00DBE0954D559B4424495898537D6143");
        //sourceObjectIds = Arrays.asList("016CB1464A56B21D11AA589964BA98CF");
        //sourceObjectIds = Arrays.asList("80FDE73E4A791F63F91F9384708FA258");
        int objectType = 3;
        EnumViewMedia viewMedia = EnumViewMedia.DssViewMediaViewAnalysis;

        List<String> targetObjectIds = new ArrayList<>(sourceObjectIds);

        countInt = Math.min(sourceObjectIds.size(), targetObjectIds.size());

        for (int i = 0; i < countInt; i++) {
            ExecutionPair executionPair = new ExecutionPair()
                                        .setSourceObjectId(sourceObjectIds.get(i))
                                        .setSourceObjectType(objectType)
                                        .setSourceViewMedia(viewMedia)
                                        .setSourceToken(sourceTokenList.get(i % sourceTokenList.size()))
                                        .setTargetObjectId(targetObjectIds.get(i))
                                        .setTargetObjectType(objectType)
                                        .setTargetViewMedia(viewMedia)
                                        .setTargetToken(targetTokenList.get(i % targetTokenList.size()))
                                        .setExecutionId(i + 1);

            pairList.add(executionPair);
        }

        String jobId = jobManager.newJob();
        ValidataionInfo validationInfo = new ValidataionInfo()
                                        .setJobId(jobId)
                                        .setSourceLibraryUrl(sourceLibraryUrl)
                                        .setSourceObjectIds(sourceObjectIds)
                                        .setTargetLibraryUrl(targetLibraryUrl)
                                        .setTargetObjectIds(targetObjectIds);

        //Init the result baseline
        try {
            baselineService.initBaseline(validationInfo);
        }
        // TODO, will use @RestControllerAdvice to handle all the execptions.
        catch (IOException e) {
            return new ResponseEntity("Server internal error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<CompletableFuture<ComparisonResult>> comparisonFutures = pairList.stream()
                .map(executionPair -> {
                    ValidationResult validationResult = new ValidationResult();
                    ExecutionResult sourceExecutionResult = new ExecutionResult();
                    sourceExecutionResult.setExecID(executionPair.getExecutionId());
                    ExecutionResult targetExecutionResult = new ExecutionResult();
                    targetExecutionResult.setExecID(executionPair.getExecutionId());

                    CompletableFuture<Object> sourceObjectExecution = executionService.executeAsync(sourceLibraryUrl, executionPair.getSourceToken(), sourceProjectId,
                            executionPair.getSourceObjectId(), executionPair.getSourceObjectType(), executionPair.getSourceViewMedia(), null, sourceExecutionExecutors)
                            .whenCompleteAsync((result, error) -> {
                                if (error == null) {
                                    ExecutionResult executionResult = (ExecutionResult) result;
                                    //baselineService.updateSourceBaseline(jobId, executionPair.getTargetObjectId(), executionResult);
                                    sourceExecutionResult.copyResult(executionResult);
//                                        sourceExecutionResult.setReport(executionResult.getReport());
//                                        sourceExecutionResult.setSqlStatement(executionResult.getSqlStatement());
//                                        sourceExecutionResult.setResultFormats(executionResult.getResultFormats());
                                }
                                else {
                                    sourceExecutionResult.getExecutedInfo().setDetailedExecStatus(error.getLocalizedMessage());
                                }
                            });

                    CompletableFuture<ObjectInfo> sourceObjectInfoExecution = executionService.executeObjectInfoAsync(sourceLibraryUrl, executionPair.getSourceToken(), sourceProjectId,
                            executionPair.getSourceObjectId(), executionPair.getSourceObjectType(),null, sourceExecutionExecutors)
                            .whenCompleteAsync((objectInfo, error) -> {
                                if (error == null) {
                                    sourceExecutionResult.setObjectInfo(objectInfo);
                                }
                            });

                    CompletableFuture<ExecutionResult> sourceObjectWithObjectInfo = sourceObjectExecution.thenCombineAsync(sourceObjectInfoExecution, (u, v) -> {
//                        validationResult.setSourceExecutionResult(sourceReportExecutionResult);
                        return sourceExecutionResult;
                    }).whenComplete((executionResult, error) -> {
                        validationResult.setSourceExecutionResult(sourceExecutionResult);
                        try {
                            baselineService.updateSourceBaseline(jobId, executionPair.getTargetObjectId(), executionResult);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    CompletableFuture<Object> targetObjectExecution = executionService.executeAsync(targetLibraryUrl, executionPair.getTargetToken(), targetProjectId,
                            executionPair.getTargetObjectId(), executionPair.getTargetObjectType(), executionPair.getTargetViewMedia(), null, targetExecutionExecutors)
                            .whenCompleteAsync((result, error) -> {
                                if (error == null) {
                                    ExecutionResult executionResult = (ExecutionResult) result;
                                    //baselineService.updateTargetBaseline(jobId, executionPair.getTargetObjectId(), executionResult);
                                    targetExecutionResult.copyResult(executionResult);
//                                        targetExecutionResult.setReport(executionResult.getReport());
//                                        targetExecutionResult.setSqlStatement(executionResult.getSqlStatement());
//                                        targetExecutionResult.setResultFormats(executionResult.getResultFormats());

                                }
                                else {
                                    targetExecutionResult.getExecutedInfo().setDetailedExecStatus(error.getLocalizedMessage());
                                }
                            });

                    CompletableFuture<ObjectInfo> targetObjectInfoExecution = executionService.executeObjectInfoAsync(targetLibraryUrl, executionPair.getTargetToken(), targetProjectId,
                                    executionPair.getTargetObjectId(), executionPair.getTargetObjectType(),null, targetExecutionExecutors)
                            .whenCompleteAsync((objectInfo, error) -> {
                                if (error == null) {
                                    targetExecutionResult.setObjectInfo(objectInfo);
                                }
                            });

                    CompletableFuture<ExecutionResult> targetObjectWithObjectInfo = targetObjectExecution.thenCombineAsync(targetObjectInfoExecution, (u, v) -> {
                        return targetExecutionResult;
                    }).whenComplete((executionResult, error) -> {
                        validationResult.setTargetExecutionResult(targetExecutionResult);
                        try {
                            baselineService.updateTargetBaseline(jobId, executionPair.getTargetObjectId(), targetExecutionResult);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    CompletableFuture<ComparisonResult> comparison = sourceObjectWithObjectInfo.thenCombineAsync(targetObjectWithObjectInfo, (source, target) -> {
//                        return comparisonService.compareReportResult(source, target);
                        return comparisonService.compareResult(source, target);
                    }).whenComplete((result, error) -> {
                        ComparedInfo comparedInfo = new ComparedInfo();
                        if (error == null) {
                            comparisonService.printDifferent(result);

                            //TODO, update result model and persist baseline
                            try {
                                baselineService.updateComparison(jobId, sourceProjectId, executionPair.getSourceObjectId(), result,
                                        sourceObjectWithObjectInfo.getNow(null), targetObjectWithObjectInfo.getNow(null));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ComparisonResult comparisonResult = result;
                            EnumComparisonStatus comparisonStatus = comparisonResult.getComparisonStatus();
                            validationResult.setComparisonResult(comparisonResult);
                            comparedInfo.setDataComparisonStatus(comparisonStatus);
                            comparedInfo.setDataComparisonStatusForNewSummary(comparisonStatus);
                            if (comparisonStatus == EnumComparisonStatus.NOT_MATCHED) {
                                //TODO, the data difference count and sql difference count need to move to the comaprison result as well.
                                comparedInfo.setRwdDataDifferenceCount(1);
                                comparedInfo.setRwdSqlDifferenceCount(1);
                            }
                        }
                        else {
                            comparedInfo.setDataComparisonStatus(EnumComparisonStatus.ERROR);
                            comparedInfo.setSQLComparisonStatus(EnumComparisonStatus.ERROR);
                            comparedInfo.setDataComparisonStatusForNewSummary(EnumComparisonStatus.ERROR);
                        }
                        validationResult.setComparedInfo(comparedInfo);
                    });

                    ValidationTask task = new ValidationTask()
                                    .setSourceObjectId(executionPair.getSourceObjectId())
                                    .setTargetObjectId(executionPair.getTargetObjectId())
                                    .setSourceObjectExecution(sourceObjectExecution)
                                    .setTargetObjectExecution(targetObjectExecution)
                                    .setComparison(comparison)
                                    .setValidationResult(validationResult);
                    jobManager.addTask(jobId, task);
                    return comparison;
                })
                .collect(Collectors.toList());


        // Create a combined Future using allOf()
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                comparisonFutures.toArray(new CompletableFuture[comparisonFutures.size()])
        );

        allFutures.whenComplete((v, e) -> {
            if (e == null)
                System.out.println("All comparisons done! At " + LocalDateTime.now());
            else {
                System.out.println("All comparisons done with the following error:\n" + e);
                System.out.println("Finished at: " + LocalDateTime.now());
                e.printStackTrace();
            }
            List<ValidationResult> validationResultSet = jobManager.getValidationResultSet(jobId);

            //TODO, generate upgrade impacts
            UpgradeImpactsHolderJson upgradeImpacts = new UpgradeImpactsHolderJson();
            try {
                baselineService.updateValidationSummary(jobId, validationResultSet);
                baselineService.updateUpgradeImpacts(jobId, upgradeImpacts);
            } catch (IOException ex) {

            }
        });

        System.out.println("Request accepted at:"+ LocalDateTime.now());

        Map<String, String> response = Map.of("jobId", jobId,
                "timestamp", LocalDateTime.now().toString());

        return new ResponseEntity(response, HttpStatus.CREATED);
    }
}
