package com.microstrategy.tools.integritymanager.controller;

import com.microstrategy.tools.integritymanager.model.appobject.ValidationTask;
import com.microstrategy.tools.integritymanager.model.bizobject.MSTRAuthToken;
import com.microstrategy.tools.integritymanager.service.intf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.util.Pair;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        if (countInt <= 0) {
            return new ResponseEntity("count parameter is expected to be larger than zero", HttpStatus.BAD_REQUEST);
        }

        String libraryUrl = "http://10.23.34.25:8080/MicroStrategyLibrary";
        MSTRAuthToken token = loginService.login(libraryUrl, "administrator", "");
        String projectId = "B19DEDCC11D4E0EFC000EB9495D0F44F";
        ExecutorService executionExecutors = Executors.newFixedThreadPool(5);

        List<Pair<String, String>> objectPairs = new ArrayList<Pair<String, String>>();

        List<String> sourceObjectIds = searchService.getTopNReportIds(libraryUrl, token, projectId, countInt);
        List<String> targetObjectIds = new ArrayList<>(sourceObjectIds);

        countInt = Math.min(sourceObjectIds.size(), targetObjectIds.size());

        for (int i = 0; i < countInt; i++) {
            objectPairs.add(Pair.of(sourceObjectIds.get(i), targetObjectIds.get(i)));
        }

        String jobId = jobManager.newJob();

        List<CompletableFuture<Object>> comparisonFutures = objectPairs.stream()
                .map(objectPair -> {
                    CompletableFuture<String> sourceObjectExecution = executionService.executeAsync(libraryUrl, token, projectId, objectPair.getFirst(),
                            0, null, executionExecutors);
                    CompletableFuture<String> targetObjectExecution = executionService.executeAsync(libraryUrl, token, projectId, objectPair.getSecond(),
                            0, null, executionExecutors);
                    CompletableFuture<Object> comparison = sourceObjectExecution.thenCombineAsync(targetObjectExecution, (source, target) -> {
                        return comparisonService.compareResult(source, target);
                    }).whenComplete((u, v) -> {
                        if (v == null) {
                            comparisonService.printDifferent(u);
                        }
                    });

                    ValidationTask task = new ValidationTask(objectPair.getFirst(),
                            objectPair.getSecond(),
                            sourceObjectExecution,
                            targetObjectExecution,
                            comparison);
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
        });

        System.out.println("Request accepted at:"+ LocalDateTime.now());

        Map<String, String> response = Map.of("jobId", jobId,
                "timestamp", LocalDateTime.now().toString());

        return new ResponseEntity(response, HttpStatus.CREATED);
    }
}
