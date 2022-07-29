package com.microstrategy.tools.integritymanager.model.entity.mstr.document;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DocumentQueryLayout {
    private String key;
    private String name;
    List<DocumentQueryGridGraph> gridGraphs;
}
