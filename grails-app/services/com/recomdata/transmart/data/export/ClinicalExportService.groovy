package com.recomdata.transmart.data.export

import au.com.bytecode.opencsv.CSVWriter
import com.google.common.collect.Iterators
import com.google.common.collect.PeekingIterator
import org.transmartproject.core.dataquery.TabularResult
import org.transmartproject.core.dataquery.clinical.ClinicalVariableColumn
import org.transmartproject.core.dataquery.clinical.ComposedVariable
import org.transmartproject.core.dataquery.clinical.PatientRow
import org.transmartproject.core.ontology.OntologyTerm
import org.transmartproject.core.ontology.Study
import org.transmartproject.core.querytool.QueryResult
import org.transmartproject.db.concept.ConceptKey

import static org.transmartproject.core.dataquery.clinical.ClinicalVariable.NORMALIZED_LEAFS_VARIABLE
class ClinicalExportService {

    def queriesResourceService
    def clinicalDataResourceService
    def studiesResourceService
    def conceptsResourceService

    final static String DATA_FILE_NAME = 'data_clinical.tsv'
    final static String META_FILE_NAME = 'meta.tsv'
    final static String SUBJ_ID_TITLE = 'Subject ID'
    final static char COLUMN_SEPARATOR = '\t' as char
    final static List<String> META_FILE_HEADER = ['Variable', 'Attribute', 'Description']

    def jobResultsService

    List<File> exportClinicalData(Map args) {
        String jobName = args.jobName

        if (jobResultsService[jobName]["Status"] == "Cancelled") {
            return null
        }

        Long resultInstanceId = args.resultInstanceId as Long
        List<String> conceptKeys = args.conceptKeys
        File studyDir = args.studyDir

        QueryResult queryResult = queriesResourceService.getQueryResultFromId(resultInstanceId)
        List<ComposedVariable> variables
        if (conceptKeys) {
            variables = createClinicalVariablesForConceptKeys(conceptKeys)
        } else {
            Set<Study> studies = getQueriedStudies(queryResult)
            variables = createClinicalVariablesForStudies(studies)
        }

        def files = []

        files << exportClinicalDataToFile(queryResult, variables, studyDir, jobName)

        files
    }

    private File exportClinicalDataToFile(QueryResult queryResult,
                                          List<ComposedVariable> variables,
                                          File studyDir,
                                          String jobName) {

        TabularResult<ClinicalVariableColumn, PatientRow> tabularResult =
                clinicalDataResourceService.retrieveData(queryResult, variables)

        try {
            return writeToFile(tabularResult, variables, studyDir, jobName)
        } finally {
            tabularResult.close()
        }
    }

    private File writeToFile(TabularResult<ClinicalVariableColumn, PatientRow> tabularResult,
                             List<ComposedVariable> variables,
                             File studyDir,
                             String jobName) {
        PeekingIterator peekingIterator = Iterators.peekingIterator(tabularResult.iterator())

        File clinicalDataFile = new File(studyDir, DATA_FILE_NAME)
        clinicalDataFile.withWriter { Writer writer ->
            CSVWriter csvWriter = new CSVWriter(writer, COLUMN_SEPARATOR)

            def firstRow = peekingIterator.peek()
            List headRowList = [SUBJ_ID_TITLE] +
                    variables.collectMany {ComposedVariable var ->
                        firstRow[var].collect {
                            it.key.conceptPath
                        }
                    }

            csvWriter.writeNext(headRowList as String[])

            while (peekingIterator.hasNext()) {
                if (jobResultsService[jobName]["Status"] == "Cancelled") {
                    return null
                }
                def row = peekingIterator.next()
                List rowList = [row.patient.inTrialId] +
                        variables.collectMany { ComposedVariable var ->
                            row[var].values()
                        }

                csvWriter.writeNext(rowList as String[])
            }
        }

        clinicalDataFile
    }

    private Set<OntologyTerm> getRelatedOntologyTerms(List<ComposedVariable> variables) {
        variables.collect { ComposedVariable variable ->
            conceptsResourceService.getByKey(variable.key.toString())
        } as Set
    }

    private Collection<ComposedVariable> createClinicalVariablesForConceptKeys(Collection<String> conceptKeys) {
        conceptKeys.collectAll {
            def conceptKey = new ConceptKey(it)
            clinicalDataResourceService.createClinicalVariable(
                    NORMALIZED_LEAFS_VARIABLE,
                    concept_path: conceptKey.conceptFullName.toString()
            )
        }
    }

    private Collection<ComposedVariable> createClinicalVariablesForStudies(Set<Study> queriedStudies) {
        queriedStudies.collect { Study study ->
            clinicalDataResourceService.createClinicalVariable(
                    NORMALIZED_LEAFS_VARIABLE,
                    concept_path: study.ontologyTerm.fullName

                    )
        }
    }

    private Set<Study> getQueriedStudies(QueryResult queryResult) {
        def trials = queryResult.patients*.trial as Set

        trials.collect { String trialId ->
            studiesResourceService.getStudyById(trialId)
        } as Set
    }

}