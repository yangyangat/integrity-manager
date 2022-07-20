package com.microstrategy.tools.integritymanager.model.entity.mstr.dossier;

import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DossierQueryDetails {
    private List<DossierQueryChapter> chapters;

    public Map<String, Query> getMapOfQuery() {
        return chapters.stream()
                .flatMap(chapter -> chapter.getVisualizations().stream())
                .collect(Collectors.toMap(DossierQueryViz::getKey, Function.identity()));
    }
}
