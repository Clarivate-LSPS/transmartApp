import grails.converters.JSON
/**
 * Created by transmart on 5/21/14.
 */
class SecureConceptsController {
    def secureConceptsResourceService
    def conceptsResourceService
    def i2b2HelperService
    def uploadedFilesService

    def getCategories() {
        render conceptsResourceService.allCategories as JSON
    }

    def springSecurityService
    def dataSource

    def getChildren() {
        def parentConceptKey = params.get('concept_key') as String

        def result
        if (!uploadedFilesService.isFilesConcept(parentConceptKey)) {
            result = []
            result.addAll(secureConceptsResourceService.getChildConcepts(parentConceptKey))
            result.addAll(uploadedFilesService.getChildConcepts(parentConceptKey))
        } else {
            result = uploadedFilesService.getChildConcepts(parentConceptKey)
        }

        render result as JSON
    }


}
