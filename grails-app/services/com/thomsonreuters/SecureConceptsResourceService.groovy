package com.thomsonreuters

import grails.transaction.Transactional

@Transactional
class SecureConceptsResourceService {
    def conceptsResourceService
    def springSecurityService
    def i2b2HelperService
    def dataSource

    def getChildConcepts(String parentConceptKey) {
        def parent = conceptsResourceService.getByKey(parentConceptKey)
        def result = parent.children
        def user = springSecurityService.getPrincipal()
        def accession = result*.sourcesystemCd as Set

        if (accession && !i2b2HelperService.isAdmin(user)) {
            def userid = user?.id
            groovy.sql.Sql sql = new groovy.sql.Sql(dataSource)
            accession = sql.rows("""
                select
                    accession
                from (
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
                    union
                    select
                      distinct sourcesystem_cd as accession
                    from
                      i2b2metadata.i2b2_secure
                    where
                      secure_obj_token = 'EXP:PUBLIC'
                ) foo where accession in (${(['?'] * accession.size()).join(',')})
            """, [userid] + accession).accession as Set
        }

        result.findAll {
            accession.contains(it.sourcesystemCd)
        }
    }
}
