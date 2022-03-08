package com.microstrategy.tools.integritymanager.service.impl;

import com.microstrategy.tools.integritymanager.model.appobject.ValidationJob;
import com.microstrategy.tools.integritymanager.model.appobject.ValidationTask;
import com.microstrategy.tools.integritymanager.service.intf.JobManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class JobManagerImpl implements JobManager {
    private final ConcurrentMap<String, ValidationJob> mapOfJobs;

    public JobManagerImpl() {
        mapOfJobs = new ConcurrentHashMap<String, ValidationJob>();
    }

    @Override
    public String newJob() {
        String jobId = UUID.randomUUID().toString();
        ValidationJob validationJob = new ValidationJob();
        this.putJob(jobId, validationJob);
        return jobId;
    }

    @Override
    public void addTask(String jobId, ValidationTask task) {
        ValidationJob job = this.getJob(jobId);
        if (job != null) {
            job.addTask(task);
        }
    }

    public void putJob(String jobId, ValidationJob theJob) {
        mapOfJobs.put(jobId, theJob);
    }

    public ValidationJob getJob(String jobId) {
        return mapOfJobs.get(jobId);
    }

    public void removeJob(String jobId) {
        mapOfJobs.remove(jobId);
    }

    public List<Map<String, String>> queryJobStatus(String jobId) {
        return this.getJob(jobId).queryStatus();
    }
}