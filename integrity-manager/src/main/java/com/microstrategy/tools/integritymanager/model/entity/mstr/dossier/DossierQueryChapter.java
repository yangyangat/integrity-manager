package com.microstrategy.tools.integritymanager.model.entity.mstr.dossier;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DossierQueryChapter {
    private String key;
    private String name;
    List<DossierQueryViz> visualizations;
}
