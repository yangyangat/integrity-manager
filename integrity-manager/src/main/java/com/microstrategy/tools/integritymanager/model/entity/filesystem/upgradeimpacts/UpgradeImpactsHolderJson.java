package com.microstrategy.tools.integritymanager.model.entity.filesystem.upgradeimpacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpgradeImpactsHolderJson {

    @JsonProperty("base_upgrade_impacts")
    List<UpgradeImpactJson> baseUpgradeImpacts = Collections.emptyList();

    @JsonProperty("target_upgrade_impacts")
    List<UpgradeImpactJson> targetUpgradeImpacts = Collections.emptyList();

    @JsonProperty("base_dependencies")
    List<UpgradeImpactsDependency> baseDependencies = Collections.emptyList();

    @JsonProperty("target_dependencies")
    List<UpgradeImpactsDependency> targetDependencies = Collections.emptyList();

    public void populateBaseUpgradeImpacts(List<List<String>> upgradeImpacts) {
        baseUpgradeImpacts = new ArrayList<>(upgradeImpacts.size());
        populateUpgradeImpacts(upgradeImpacts, baseUpgradeImpacts);
    }

    public void populateTargetUpgradeImpacts(List<List<String>> upgradeImpacts) {
        targetUpgradeImpacts = new ArrayList<>(upgradeImpacts.size());
        populateUpgradeImpacts(upgradeImpacts, targetUpgradeImpacts);
    }

    public void populateBaseDependencies(List<ExecutableSet> executableSets, String projectId) {
        baseDependencies = new ArrayList<>(executableSets.size());
        populateDependencies(executableSets, baseDependencies, projectId);
    }

    public void populateTargetDependencies(List<ExecutableSet> executableSets, String projectId) {
        targetDependencies = new ArrayList<>(executableSets.size());
        populateDependencies(executableSets, targetDependencies, projectId);
    }

    void populateDependencies(List<ExecutableSet> executableSets,
                              List<UpgradeImpactsDependency> upgradeImpactsDependencies,
                              String projectId) {
        return;
        /*
        for (ExecutableSet executableSet : executableSets) {
            StoredExecutionResult ser = (StoredExecutionResult) executableSet.getComparison().getLHSResult();
            String depPath = "";
            BufferedReader reader = null;
            try {
                if (ser != null && ser.getRelationLocation() != null) {
                    depPath = ser.getRelationLocation();
                }
                if (StringUtils.hasLength(depPath)) {
                    File file = new File(depPath);
                    reader = new BufferedReader(new FileReader(file));
                    String line = reader.readLine();
                    UpgradeImpactsDependency upgradeImpactsDependency = new UpgradeImpactsDependency();
                    upgradeImpactsDependency.populate(line, projectId);
                    upgradeImpactsDependencies.add(upgradeImpactsDependency);

                }
            } catch (IOException e) {
                //log the erros
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignore) {
                        //log the errors
                    }
                }
            }
        }

         */
    }

    void populateUpgradeImpacts(List<List<String>> upgradeImpacts, List<UpgradeImpactJson> upgradeImpactJsons) {
        if (upgradeImpacts == null || upgradeImpacts.size() < 2) {
            return;
        }

        for (List<String> upgradeImpactLine : upgradeImpacts.subList(1, upgradeImpacts.size())) {
            UpgradeImpactJson upgradeImpactJson = new UpgradeImpactJson();
            upgradeImpactJson.populate(upgradeImpactLine);
            upgradeImpactJsons.add(upgradeImpactJson);
        }
    }

}
