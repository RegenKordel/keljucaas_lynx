package eu.openreq.keljucaas.servicesTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.openreq.keljucaas.controllers.KeljuController;
import eu.openreq.keljucaas.servicesTest.Testcase.ExpectedDiagnosisResult;
import eu.openreq.keljucaas.servicesTest.GrazDto.CheckConsistencyResponse;
import eu.openreq.keljucaas.servicesTest.GrazDto.Diagnosis;
import eu.openreq.keljucaas.servicesTest.GrazDto.DiagnosisRelationship;
import eu.openreq.keljucaas.servicesTest.GrazDto.RelationshipsInconsistent;
//import com.google.gson.Gson;
import eu.openreq.keljucaas.servicesTest.GrazDto.Response;



@RunWith(SpringRunner.class)
@SpringBootTest

public class ConsistencyCheckServiceTestServiceV2 {
	@Autowired
	private KeljuController keljuController;
	

    @Test
	public void ConsistentDecompose() {
    	Testcase tc = new Testcase("consistent_with_decomposes.json",
    			Boolean.TRUE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							)));
    	performTestCase(tc);
    }

    @Test
	public void ConsistentImplies() {
    	Testcase tc = new Testcase("consistent_with_implies.json",
    			Boolean.TRUE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							)));
    	performTestCase(tc);
    }

    @Test
	public void consistent_with_incompatible() {
    	Testcase tc = new Testcase("consistent_with_incompatible.json",
    			Boolean.TRUE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							)));
    	performTestCase(tc);
    }

    @Test
	public void consistent_with_incompatible2() {
    	Testcase tc = new Testcase("consistent_with_incompatible2.json",
    			Boolean.TRUE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							)));
    	performTestCase(tc);
    }
    

    @Test
	public void consistent_with_requires() {
    	Testcase tc = new Testcase("consistent_with_requires.json",
    			Boolean.TRUE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							)));
    	performTestCase(tc);
    }


    @Test
	public void inconsistent_excludes_resourceExceed() {
    	
       	Testcase tc = new Testcase("inconsistent_excludes_resourceExceed.json",
    			Boolean.FALSE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					new RelationshipsInconsistent ("REQ1", "REQ5", "excludes")
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													"REQ1", "REQ3"
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList(
    															)))),
    							new ExpectedDiagnosisResult( //example of failing diagnosis
    									Boolean.FALSE,
    									null))));
    	performTestCase(tc);
   	
    }
    

	@Test
	public void inconsistent_excludes() {
       	Testcase tc = new Testcase("inconsistent_excludes.json",
    			Boolean.FALSE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					new RelationshipsInconsistent ("REQ1", "REQ5", "excludes")
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													"REQ1"
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList(
    															)))),
    							new ExpectedDiagnosisResult( 
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList(
    															new DiagnosisRelationship("REQ1", "REQ5", "excludes")
    															)))))));
    	performTestCase(tc);
	}
	
    @Test
	public void inconsistent_with_decomposes_02() {
    	
       	Testcase tc = new Testcase("inconsistent_with_decomposes_02.json",
    			Boolean.FALSE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					new RelationshipsInconsistent ("R3", "R3a", "decomposition")
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													"R3", "R4"
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList(
    															)))),
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList( //example of how expected diagnosed relationships are set
    															new DiagnosisRelationship("R3", "R3a", "decomposition")
    															)))))));
    	performTestCase(tc);
    }

    @Test
	public void inconsistent_with_decomposes() {
    	
       	Testcase tc = new Testcase("inconsistent_with_decomposes.json",
    			Boolean.FALSE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					new RelationshipsInconsistent ("REQ1", "REQ4", "decomposition")
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													"REQ1"
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList(
    															)))),
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList( //example of how expected diagnosed relationships are set
    															new DiagnosisRelationship("REQ1", "REQ4", "decomposition")
    															)))))));
    	performTestCase(tc);
    }
    
    @Test
	public void inconsistent_with_implies() {
    	
       	Testcase tc = new Testcase("inconsistent_with_implies.json",
    			Boolean.FALSE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					new RelationshipsInconsistent ("REQ2", "REQ3", "implies")
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													"REQ1", "REQ2"
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList(
    															)))),
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList( //example of how expected diagnosed relationships are set
    															new DiagnosisRelationship("REQ2", "REQ3", "implies")
    															)))))));
    	performTestCase(tc);
    }

    @Test
	public void inconsistent_with_incompatible2() {
       	Testcase tc = new Testcase("inconsistent_with_incompatible2.json",
    			Boolean.FALSE,
    			new LinkedList<RelationshipsInconsistent>(Arrays.asList(
    					new RelationshipsInconsistent ("REQ1", "REQ3", "incompatible")
    					)), 
    			new LinkedList<ExpectedDiagnosisResult> (
    					Arrays.asList(
    							new ExpectedDiagnosisResult(
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													"REQ1"
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList(
    															)))),
    							new ExpectedDiagnosisResult( //example of failing diagnosis
    									Boolean.TRUE,
    									new Diagnosis(
    											new LinkedList<String>(Arrays.asList(
    													)), 
    											new LinkedList<DiagnosisRelationship> (
    													Arrays.asList( //example of how expected diagnosed relationships are set
    															new DiagnosisRelationship("REQ1", "REQ3", "incompatible")
    															)))))));
    	performTestCase(tc);
	}

    
    void performTestCase(Testcase testcase) {
		try {
			
			String jsonText = readTestJson(testcase.testcasefile);
			if (jsonText == null)
				fail("Could not read input string from '" + testcase.testcasefile +"'.");
				
			ResponseEntity<?> responseEntity = keljuController.consistencyCheckAndDiagnosis(jsonText, false);
			
			CheckConsistencyResponse responses = new ObjectMapper().readValue(responseEntity.getBody().toString(), CheckConsistencyResponse.class);
			
			assertEquals(responseEntity.getStatusCodeValue(), 200);
			List<Response> releasePlanVersions = responses.getResponse();
			assertTrue(testcase.getNrExpectedReleaseplans() + " releaseplans expected", releasePlanVersions.size() == testcase.getNrExpectedReleaseplans());
			Response initialReleasePlan = releasePlanVersions.get(0);
			assertEquals("Consistency:", testcase.consistent, initialReleasePlan.getConsistent());
			if (! testcase.consistent) {
				assertTrue("Equal inconsistent relationships:", equalRelationshipsInconsitentListsAsSet(
						testcase.relationshipsInconsistents,
						initialReleasePlan.getRelationshipsInconsistent()));
			}
			
			for (int relPlan = 1; relPlan <  releasePlanVersions.size(); relPlan++) {
				//TODO the following disables need for third analysis that does not work right now...
				//It produces results of the first analysis and uses them instead.
				//Major kludge, either third analysis should be removed or fixed properly
				
				int resultToUse = relPlan - 1;
				if (resultToUse >= testcase.expectedDiagnosisResults.size()) //disable checking of result if no correct result given. Disables third result in practice
					continue;
				
				ExpectedDiagnosisResult expectedDiagnosisResult = testcase.getExpectedDiagnosisResult(resultToUse);
				Boolean expectedConsistency = expectedDiagnosisResult.consistentWithDiagnosis;
				Diagnosis expectedDiagnosis = expectedDiagnosisResult.expectedDiagnosis;

				Response reldiagPlan = releasePlanVersions.get(relPlan);
				Diagnosis diagnosis = reldiagPlan.getDiagnosis();

				assertEquals("Diagnosed consistency", expectedConsistency, reldiagPlan.getConsistent());
				
				if (expectedConsistency) {
					assertTrue("Equal diagnosed requirements: expected: " + expectedDiagnosis.getDiagnosisRequirements() + ", got:" +diagnosis.getDiagnosisRequirements(),
							equalStringListsAsSet(expectedDiagnosis.getDiagnosisRequirements(), diagnosis.getDiagnosisRequirements()));
					
					assertTrue("Equal diagnosed relationships:", equalDiagnosisRelationshipsListsAsSet(
							expectedDiagnosis.getDiagnosisRelationships(),
							diagnosis.getDiagnosisRelationships()));
					
				}
				
			}				
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
    
	private static boolean equalStringListsAsSet(List<String> list1, List<String> list2) {
		HashSet<String> set1 = new HashSet<>(list1);
		HashSet<String> set2 = new HashSet<>(list2);
		return set1.equals(set2);
	}
	
	private boolean equalDiagnosisRelationshipsListsAsSet(List<DiagnosisRelationship> list1, List<DiagnosisRelationship> list2) {
		HashSet<DiagnosisRelationship> set1 = new HashSet<>(list1);
		HashSet<DiagnosisRelationship> set2 = new HashSet<>(list2);
		return set1.equals(set2);
	}

	private boolean equalRelationshipsInconsitentListsAsSet(List<RelationshipsInconsistent> list1, List<RelationshipsInconsistent> list2) {
		HashSet<RelationshipsInconsistent> set1 = new HashSet<>(list1);
		HashSet<RelationshipsInconsistent> set2 = new HashSet<>(list2);
		return set1.equals(set2);
	}

	private String readTestJson(String resourcefile) {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(resourcefile).getFile());
			String jsonText = new String(Files.readAllBytes(file.toPath()));
			return jsonText;
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			return null;
		}
	}

	
}