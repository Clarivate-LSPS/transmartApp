import fm.FmFolderAssociation
import grails.converters.JSON

/**
 * Created by transmart on 5/21/14.
 */
class ConceptsSecuryController {
    def conceptsResourceService
    def i2b2HelperService

    def getCategories() {
        render conceptsResourceService.allCategories as JSON
    }

    def springSecurityService
    def dataSource

    def getChildren() {
        def parentConceptKey = params.get('concept_key')

        def filesIndexOf = parentConceptKey.lastIndexOf('Files\\')
        def result
        if (filesIndexOf != parentConceptKey.length() - 6) {
            def parent = conceptsResourceService.getByKey(parentConceptKey)
            result = parent.children
            def user = springSecurityService.getPrincipal()
            def accession = result*.sourcesystemCd

            if(!i2b2HelperService.isAdmin(user)) {
                def userid = user?.id
                groovy.sql.Sql sql = new groovy.sql.Sql(dataSource)
                def accessionSQLSet = sql.rows("""
                select
                  distinct be.accession
                from
                  searchapp.search_auth_sec_object_access sasoa
                    left join searchapp.search_auth_group_member sagm
                    on sasoa.auth_principal_id = sagm.auth_group_id,
                  searchapp.search_sec_access_level ssal,
                  searchapp.search_secure_object sso,
                  biomart.bio_experiment be
                  where
                    ssal.access_level_value > 0
                    and sasoa.secure_access_level_id = ssal.search_sec_access_level_id
                    and coalesce(sagm.auth_user_id, sasoa.auth_principal_id) = ?
                    and sasoa.secure_object_id = sso.search_secure_object_id
                    and sso.bio_data_id = be.bio_experiment_id
            """, userid)
                accession = accessionSQLSet.accession
            }

            result = result.findAll {
                accession.contains(it.sourcesystemCd)
            }

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
