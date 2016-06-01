package com.thomsonreuters

import fm.FmFolderAssociation
import grails.transaction.Transactional
import org.transmartproject.core.ontology.OntologyTerm

/**
 * Date: 19-Apr-16
 * Time: 11:35
 */
@Transactional
class UploadedFilesService {
    def conceptsResourceService
    def i2b2HelperService

    private static final String FILES_CONCEPT_NAME = "Files"

    Collection getChildConcepts(String parentConceptKey) {
        def studyNode = getStudyNodeByConceptKey(parentConceptKey)
        if (!studyNode)
            return []

        def fmFolderAssociation = FmFolderAssociation.findByObjectUid("EXP:${studyNode.sourcesystemCd}")
        def filesList = fmFolderAssociation?.fmFolder?.fmFiles
        if (!filesList)
            return []

        if (!isFilesConcept(parentConceptKey)) {
            return [new FileConceptNode(
                key: studyNode.key + 'Files\\',
                level: studyNode.level + 1,
                fullName: studyNode.fullName + 'Files\\',
                name: 'Files',
                tooltip: studyNode.tooltip + 'Files\\',
                visualAttributes: ["FOLDER", "ACTIVE"],
                metadata: "",
                dimensionCode: studyNode.dimensionCode + 'Files\\',
                dimensionTableName: "FILE"
            )]
        } else {
            return filesList.collect {
                new FileConceptNode(
                    key: studyNode.key + 'Files\\' + it.displayName,
                    level: studyNode.level + 2,
                    fullName: studyNode.fullName + 'Files\\' + it.displayName,
                    name: it.displayName,
                    tooltip: studyNode.tooltip + 'Files\\' + it.displayName,
                    visualAttributes: ["FILE", "ACTIVE"],
                    metadata: "{fileId: ${it.id}, fileType: '${it.fileType}'}",
                    dimensionCode: studyNode.dimensionCode + 'Files\\' + it.displayName,
                    dimensionTableName: "FILE"
                )
            }
        }
    }

    boolean isFilesConcept(String parentConceptKey) {
        parentConceptKey.endsWith("\\$FILES_CONCEPT_NAME\\")
    }

    private def getStudyNodeByConceptKey(String conceptKey) {
        if (isFilesConcept(conceptKey)) {
            conceptKey = conceptKey[0..-1 - FILES_CONCEPT_NAME.length() - 1]
        }
        def node = conceptsResourceService.getByKey(conceptKey)
        node?.visualAttributes?.contains(OntologyTerm.VisualAttributes.STUDY) ? node : null
    }

    static class FileConceptNode {
        String key
        String level
        String fullName
        String name
        String tooltip
        List visualAttributes
        String metadata
        String dimensionCode
        String dimensionTableName
    }

    def getNodePermissions(String conceptKey) {
        def studyNode = getStudyNodeByConceptKey(conceptKey)
        if (isFilesConcept(conceptKey)) {
            def fmFolderAssociation = FmFolderAssociation.findByObjectUid("EXP:${studyNode.sourcesystemCd}")
            def filesList = fmFolderAssociation?.fmFolder?.fmFiles
            def i2b2Secure = org.transmartproject.db.ontology.I2b2Secure.findByNameAndFullName(studyNode.name, studyNode.fullName)

            return filesList.collectEntries{
                [(studyNode.fullName + FILES_CONCEPT_NAME + '\\' + it.displayName) : i2b2Secure.secureObjectToken]
            }
        } else if (studyNode) {
            def i2b2Secure = org.transmartproject.db.ontology.I2b2Secure.findByNameAndFullName(studyNode.name, studyNode.fullName)
            return [(i2b2HelperService.keyToPath(conceptKey + FILES_CONCEPT_NAME + '\\')): i2b2Secure.secureObjectToken]
        }
        [:]
    }
}
