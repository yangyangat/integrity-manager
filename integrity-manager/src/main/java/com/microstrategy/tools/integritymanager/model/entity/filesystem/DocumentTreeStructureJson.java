package com.microstrategy.tools.integritymanager.model.entity.filesystem;

import com.microstrategy.next.generation.matester.models.RwdTreeStructureJson;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;
import com.microstrategy.tools.integritymanager.model.entity.mstr.document.DocumentQueryDetails;
import com.microstrategy.tools.integritymanager.model.entity.mstr.document.DocumentQueryGridGraph;
import com.microstrategy.tools.integritymanager.model.entity.mstr.document.DocumentQueryLayout;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentTreeStructureJson extends RwdTreeStructureJson {
    public static RwdTreeStructureJson buildRwdTreeStructureJson(DocumentQueryDetails documentDefinition, Executable documentInfo) {
        RwdTreeStructureJson rwdTreeStructureJson = new RwdTreeStructureJson();
        rwdTreeStructureJson.setNodeType("Section");
        if (documentInfo != null) {
            rwdTreeStructureJson.setNodeName(documentInfo.getName());
            rwdTreeStructureJson.setNodeId(documentInfo.getID());
        }
        else {
            rwdTreeStructureJson.setNodeName("");
        }

        List<RwdTreeStructureJson> children = documentDefinition.getLayouts().stream()
                .map(layout -> DocumentTreeStructureJson.buildRwdTreeStructureJsonFromDocumentLayout(layout))
                .collect(Collectors.toList());

        rwdTreeStructureJson.setChildren(children);

        return rwdTreeStructureJson;
    }

    private static RwdTreeStructureJson buildRwdTreeStructureJsonFromDocumentLayout(DocumentQueryLayout layout) {
        RwdTreeStructureJson rwdTreeStructureJson = new RwdTreeStructureJson();
        rwdTreeStructureJson.setNodeType("GroupBy");
        rwdTreeStructureJson.setNodeName(layout.getName());
        rwdTreeStructureJson.setNodeId(layout.getKey());

        List<RwdTreeStructureJson> children = layout.getGridGraphs().stream()
                .map(gridGraph -> DocumentTreeStructureJson.buildRwdTreeStructureJsonFromGridGraph(gridGraph))
                .collect(Collectors.toList());

        rwdTreeStructureJson.setChildren(children);

        return rwdTreeStructureJson;
    }

    private static RwdTreeStructureJson buildRwdTreeStructureJsonFromGridGraph(DocumentQueryGridGraph gridGraph) {
        RwdTreeStructureJson rwdTreeStructureJson = new RwdTreeStructureJson();
        rwdTreeStructureJson.setNodeType("GridGraph");
        rwdTreeStructureJson.setNodeName(gridGraph.getRawName());
        rwdTreeStructureJson.setNodeId(gridGraph.getKey());
        rwdTreeStructureJson.setNodeTitle(gridGraph.getRawName());

        rwdTreeStructureJson.setDataDiffFile(gridGraph.getKey() + "_data_diff.json");
        rwdTreeStructureJson.setSqlDiffFile(gridGraph.getKey() + "_sql_diff.json");

        return rwdTreeStructureJson;
    }
}
