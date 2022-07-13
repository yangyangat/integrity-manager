package com.microstrategy.tools.integritymanager.model.entity.mstr.dossier;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DossierDefinition {
    private String name;
    private String id;
    List<DossierChapter> chapters;

    public Map<String, List<String>> getChapterVizMap() {
        return chapters.stream().collect(Collectors.toMap(DossierChapter::getKey, DossierChapter::getAllVizKeys));
    }
}
