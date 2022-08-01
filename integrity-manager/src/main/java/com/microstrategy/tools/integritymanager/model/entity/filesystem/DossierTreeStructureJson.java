package com.microstrategy.tools.integritymanager.model.entity.filesystem;

import com.microstrategy.next.generation.matester.models.RwdTreeStructureJson;
import com.microstrategy.tools.integritymanager.model.entity.mstr.document.DocumentQueryDetails;
import com.microstrategy.tools.integritymanager.model.entity.mstr.dossier.DossierChapter;
import com.microstrategy.tools.integritymanager.model.entity.mstr.dossier.DossierDefinition;
import com.microstrategy.tools.integritymanager.model.entity.mstr.dossier.DossierPage;
import com.microstrategy.tools.integritymanager.model.entity.mstr.dossier.DossierVisualization;

import java.util.List;
import java.util.stream.Collectors;


public class DossierTreeStructureJson extends RwdTreeStructureJson {
    public static RwdTreeStructureJson buildRwdTreeStructureJson(DossierDefinition dossierDefinition) {
        RwdTreeStructureJson rwdTreeStructureJson = new RwdTreeStructureJson();
        rwdTreeStructureJson.setNodeType("Section");
        rwdTreeStructureJson.setNodeName(dossierDefinition.getName());

        List<RwdTreeStructureJson> children = dossierDefinition.getChapters().stream()
                .map(chapter -> DossierTreeStructureJson.buildRwdTreeStructureJsonFromDossierChapter(chapter))
                .collect(Collectors.toList());

        rwdTreeStructureJson.setChildren(children);

        return rwdTreeStructureJson;
    }

    private static RwdTreeStructureJson buildRwdTreeStructureJsonFromDossierChapter(DossierChapter dossierChapter) {
        RwdTreeStructureJson rwdTreeStructureJson = new RwdTreeStructureJson();
        rwdTreeStructureJson.setNodeType("GroupBy");
        rwdTreeStructureJson.setNodeName(dossierChapter.getName());
        rwdTreeStructureJson.setNodeId(dossierChapter.getKey());

        List<RwdTreeStructureJson> children = dossierChapter.getPages().stream()
                .map(page -> DossierTreeStructureJson.buildRwdTreeStructureJsonFromDossierPage(page))
                .collect(Collectors.toList());

        rwdTreeStructureJson.setChildren(children);

        return rwdTreeStructureJson;
    }

    private static RwdTreeStructureJson buildRwdTreeStructureJsonFromDossierPage(DossierPage dossierPage) {
        RwdTreeStructureJson rwdTreeStructureJson = new RwdTreeStructureJson();
        rwdTreeStructureJson.setNodeType("Panel");
        rwdTreeStructureJson.setNodeName("VisulizationPanel");
        rwdTreeStructureJson.setNodeId(dossierPage.getKey());
        rwdTreeStructureJson.setNodeTitle(dossierPage.getName());

        List<RwdTreeStructureJson> children = dossierPage.getVisualizations().stream()
                .map(viz -> DossierTreeStructureJson.buildRwdTreeStructureJsonFromDossierViz(viz))
                .collect(Collectors.toList());

        rwdTreeStructureJson.setChildren(children);

        return rwdTreeStructureJson;
    }

    private static RwdTreeStructureJson buildRwdTreeStructureJsonFromDossierViz(DossierVisualization dossierViz) {
        RwdTreeStructureJson rwdTreeStructureJson = new RwdTreeStructureJson();
        rwdTreeStructureJson.setNodeType("GridGraph");
        rwdTreeStructureJson.setNodeName(dossierViz.getName());
        rwdTreeStructureJson.setNodeId(dossierViz.getKey());
        rwdTreeStructureJson.setNodeTitle(dossierViz.getName());

        rwdTreeStructureJson.setDataDiffFile(dossierViz.getKey() + "_data_diff.json");
        rwdTreeStructureJson.setSqlDiffFile(dossierViz.getKey() + "_sql_diff.json");

        return rwdTreeStructureJson;
    }
}
