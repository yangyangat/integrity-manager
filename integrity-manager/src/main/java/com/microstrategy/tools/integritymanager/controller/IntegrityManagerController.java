package com.microstrategy.tools.integritymanager.controller;

import com.microstrategy.tools.integritymanager.model.bo.*;
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
import java.util.*;
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

        String targetLibraryUrl = "http://10.23.34.25:8080/MicroStrategyLibrary";
        //MSTRAuthToken targetToken = loginService.login(targetLibraryUrl, "administrator", "");
        List<MSTRAuthToken> targetTokenList = loginService.login(targetLibraryUrl, "administrator", "", sessionCount);
        String targetProjectId = "B19DEDCC11D4E0EFC000EB9495D0F44F";
        ExecutorService targetExecutionExecutors = Executors.newFixedThreadPool(5);

        if (sourceTokenList.isEmpty() || targetTokenList.isEmpty()) {
            return new ResponseEntity("Failed to login the library server", HttpStatus.UNAUTHORIZED);
        }

        List<ExecutionPair> pairList = new ArrayList<>();

        List<String> sourceObjectIds = searchService.getTopNReportIds(sourceLibraryUrl, sourceTokenList.get(0), sourceProjectId, countInt);
        sourceObjectIds = Arrays.asList("13CFD83A458A68655A13CBA8D7C62CD5");

        List<String> targetObjectIds = new ArrayList<>(sourceObjectIds);

        countInt = Math.min(sourceObjectIds.size(), targetObjectIds.size());

        for (int i = 0; i < countInt; i++) {
            pairList.add(new ExecutionPair(sourceObjectIds.get(i), 3, sourceTokenList.get(i % sourceTokenList.size()),
                    targetObjectIds.get(i), 3, targetTokenList.get(i % targetTokenList.size())));
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

        List<CompletableFuture<Object>> comparisonFutures = pairList.stream()
                .map(executionPair -> {
                    ValidationResult validationResult = new ValidationResult();
                    ReportExecutionResult sourceReportExecutionResult = new ReportExecutionResult();
                    ReportExecutionResult targetReportExecutionResult = new ReportExecutionResult();

                    CompletableFuture<String> sourceObjectExecution = executionService.executeAsync(sourceLibraryUrl, executionPair.getSourceToken(), sourceProjectId,
                            executionPair.getSourceObjectId(), executionPair.getSourceObjectType(),null, sourceExecutionExecutors)
                            .whenCompleteAsync((result, error) -> {
                                if (error == null) {
                                    try {
                                        baselineService.updateSourceBaseline(jobId, executionPair.getTargetObjectId(), result);
                                        sourceReportExecutionResult.setReport(result);
                                        //validationResult.setSourceExecutionResult(ReportExecutionResult.build().setReport(result));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    CompletableFuture<ObjectInfo> sourceObjectInfoExecution = executionService.executeObjectInfoAsync(sourceLibraryUrl, executionPair.getSourceToken(), sourceProjectId,
                            executionPair.getSourceObjectId(), executionPair.getSourceObjectType(),null, sourceExecutionExecutors)
                            .whenCompleteAsync((objectInfo, error) -> {
                                if (error == null) {
                                    sourceReportExecutionResult.setObjectInfo(objectInfo);
                                }
                            });

                    CompletableFuture<ReportExecutionResult> sourceObjectWithObjectInfo = sourceObjectExecution.thenCombineAsync(sourceObjectInfoExecution, (u, v) -> {
                        validationResult.setSourceExecutionResult(sourceReportExecutionResult);
                        return sourceReportExecutionResult;
                    });

                    CompletableFuture<String> targetObjectExecution = executionService.executeAsync(targetLibraryUrl, executionPair.getTargetToken(), targetProjectId,
                            executionPair.getTargetObjectId(), executionPair.getTargetObjectType(),null, targetExecutionExecutors)
                            .whenCompleteAsync((result, error) -> {
                                if (error == null) {
                                    try {
                                        baselineService.updateTargetBaseline(jobId, executionPair.getTargetObjectId(), result);
                                        targetReportExecutionResult.setReport(result);
                                        //validationResult.setTargetExecutionResult(ReportExecutionResult.build().setReport(result));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    CompletableFuture<ObjectInfo> targetObjectInfoExecution = executionService.executeObjectInfoAsync(targetLibraryUrl, executionPair.getTargetToken(), targetProjectId,
                                    executionPair.getTargetObjectId(), executionPair.getTargetObjectType(),null, targetExecutionExecutors)
                            .whenCompleteAsync((objectInfo, error) -> {
                                if (error == null) {
                                    targetReportExecutionResult.setObjectInfo(objectInfo);
                                }
                            });

                    CompletableFuture<ReportExecutionResult> targetObjectWithObjectInfo = targetObjectExecution.thenCombineAsync(targetObjectInfoExecution, (u, v) -> {
                        validationResult.setTargetExecutionResult(targetReportExecutionResult);
                        return targetReportExecutionResult;
                    });

                    CompletableFuture<Object> comparison = sourceObjectWithObjectInfo.thenCombineAsync(targetObjectWithObjectInfo, (source, target) -> {
                        return comparisonService.compareResult(source.getReport(), target.getReport());
                    }).whenComplete((comparisonResult, error) -> {
                        if (error == null) {
                            comparisonService.printDifferent(comparisonResult);

                            //TODO, update result model and persist baseline
                            try {
                                baselineService.updateComparison(jobId, sourceProjectId, executionPair.getSourceObjectId(), comparisonResult,
                                        sourceObjectWithObjectInfo.getNow(null), targetObjectWithObjectInfo.getNow(null));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            validationResult.setComparisonResult(new ComparisonResult());
                        }
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
            }
            List<ValidationResult> validationResultSet = jobManager.getValidationResultSet(jobId);
            try {
                baselineService.updateValidationSummary(jobId, validationResultSet);
            } catch (IOException ex) {

            }
        });

        System.out.println("Request accepted at:"+ LocalDateTime.now());

        Map<String, String> response = Map.of("jobId", jobId,
                "timestamp", LocalDateTime.now().toString());

        return new ResponseEntity(response, HttpStatus.CREATED);
    }
}
