package org.transmart.ontology

import fm.FmFolderAssociation
import grails.converters.JSON

class ConceptsController {

    def conceptsResourceService

    def getCategories() {
        render conceptsResourceService.allCategories as JSON
    }

    def getChildren() {
        def parentConceptKey = params.get('concept_key')
        def filesIndexOf = parentConceptKey.lastIndexOf('Files\\')
        def result
        if (filesIndexOf != parentConceptKey.length() - 6) {
            def parent = conceptsResourceService.getByKey(parentConceptKey)
            result = parent.children
            if (parent.cVisualattributes == 'FAS') {
                def fmFolderAssociation = FmFolderAssociation.findByObjectUid("EXP:${parent.sourcesystemCd}")
                def filesList = fmFolderAssociation?.fmFolder?.fmFiles
                if (filesList && filesList.size() > 0) {
                    result.add(new resJSON(
                            key: parent.key + 'Files\\',
                            level: parent.level + 1,
                            fullName: parent.fullName + 'Files\\',
                            name: 'Files',
                            tooltip: parent.tooltip + 'Files\\',
                            visualAttributes: ["FOLDER", "ACTIVE"],
                            metadata: "",
                            dimensionCode: parent.dimensionCode + 'Files\\',
                            dimensionTableName: "FILE"
                    ))
                }
            }
        } else {
            parentConceptKey = ((String)parentConceptKey).substring(0, filesIndexOf)
            def parent = conceptsResourceService.getByKey(parentConceptKey)
            result = []
            if (parent.cVisualattributes == 'FAS') {
                def fmFolderAssociation = FmFolderAssociation.findByObjectUid("EXP:${parent.sourcesystemCd}")
                def filesList = fmFolderAssociation?.fmFolder?.fmFiles

                filesList.each {
                    result.add(new resJSON(
                            key: parent.key + 'Files\\' + it.displayName,
                            level: parent.level + 2,
                            fullName: parent.fullName + 'Files\\' + it.displayName,
                            name: it.displayName,
                            tooltip: parent.tooltip + 'Files\\' + it.displayName,
                            visualAttributes: ["FILE", "ACTIVE"],
                            metadata: "{fileId: ${it.id}, fileType: '${it.fileType}'}",
                            dimensionCode: parent.dimensionCode + 'Files\\' + it.displayName,
                            dimensionTableName: "FILE"
                    ))
                }
            }
        }
        render result as JSON
    }

    static class resJSON{
        String key;
        String level;
        String fullName;
        String name;
        String tooltip;
        List   visualAttributes;
        String metadata;
        String dimensionCode;
        String dimensionTableName;
    }
}
