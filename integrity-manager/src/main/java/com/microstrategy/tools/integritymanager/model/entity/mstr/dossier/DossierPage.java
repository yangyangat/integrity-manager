package com.microstrategy.tools.integritymanager.model.entity.mstr.dossier;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DossierPage {
    private String key;
    private String name;
    List<DossierVisualization> visualizations;
}
