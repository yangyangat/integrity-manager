package com.microstrategy.tools.integritymanager.model.entity.mstr.document;

import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DocumentQueryDetails {
    private List<DocumentQueryLayout> layouts;

    public Map<String, Query> getMapOfQuery() {
        return layouts.stream()
                .flatMap(layout -> layout.getGridGraphs().stream())
                .collect(Collectors.toMap(DocumentQueryGridGraph::getKey, Function.identity()));
    }
}
