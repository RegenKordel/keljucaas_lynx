package eu.openreq.keljucaas.servicesTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.openreq.keljucaas.servicesTest.GrazDto.Diagnosis;
import eu.openreq.keljucaas.servicesTest.GrazDto.RelationshipsInconsistent;

public class Testcase {
	protected String testcasefile;
	protected Boolean consistent;
	protected List<RelationshipsInconsistent> relationshipsInconsistents;
	protected List <ExpectedDiagnosisResult> expectedDiagnosisResults;
	
	protected ExpectedDiagnosisResult getExpectedDiagnosisResult(int nth) {
		return expectedDiagnosisResults.get(nth);
	}
	
	protected int getNrExpectedReleaseplans() {
		if (consistent)
			return 1;
		else
			return 4;
	}
	
	public static class ExpectedDiagnosisResult {
		protected Boolean consistentWithDiagnosis;
		protected Diagnosis expectedDiagnosis;
		public ExpectedDiagnosisResult(Boolean consistentWithDiagnosis, Diagnosis expectedDiagnosis) {
			super();
			this.consistentWithDiagnosis = consistentWithDiagnosis;
			this.expectedDiagnosis = expectedDiagnosis;
		}
		
	}

	public Testcase(String testcasefile, Boolean consistent, List<RelationshipsInconsistent> relationshipsInconsistents,
			List<ExpectedDiagnosisResult> expectedDiagnosisResults) {
		super();
		this.testcasefile = testcasefile;
		this.consistent = consistent;
		this.relationshipsInconsistents = relationshipsInconsistents;
		this.expectedDiagnosisResults = expectedDiagnosisResults;
	}
	
	
	
	
	
}
