package org.transmart.ontology

import grails.converters.JSON

class ConceptsController {
    def conceptsResourceService
    def uploadedFilesService

    def getCategories() {
        render conceptsResourceService.allCategories as JSON
    }

    def getChildren() {
        def parentConceptKey = params.get('concept_key') as String

        def result
        if (!uploadedFilesService.isFilesConcept(parentConceptKey)) {
            result = []
            def parent = conceptsResourceService.getByKey(parentConceptKey)
            result.addAll(parent.children)
            result.addAll(uploadedFilesService.getChildConcepts(parentConceptKey))
        } else {
            result = uploadedFilesService.getChildConcepts(parentConceptKey)
        }
        render result as JSON
    }
}
