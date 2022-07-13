package com.microstrategy.tools.integritymanager.model.entity.mstr.dossier;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DossierChapter {
    private String key;
    private String name;
    List<DossierPage> pages;
    List<DossierFilter> filters;

    List<String> getAllVizKeys() {
        return pages.stream()
                .flatMap(page -> page.getVisualizations().stream())
                .map(viz -> viz.getKey())
                .collect(Collectors.toList());
    }
}
