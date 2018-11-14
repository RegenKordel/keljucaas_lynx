package eu.openreq.keljucaas.servicesTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;

import eu.openreq.keljucaas.controllers.KeljuController;



@RunWith(SpringRunner.class)
@SpringBootTest

public class ConsistencyCheckServiceTest {
	@Autowired
	private KeljuController keljuController;
	
	private Gson gson;
	

    @Before
    public void setUp() {
    	gson = new Gson();
    }
	
	@Test
	public void Consistent01() {

		try {
			
			String inputFileName = "consistent_01.txt";
			String jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
				
			ResponseEntity<?> response = keljuController.uploadDataCheckForConsistencyAndDoDiagnosis(jsonText);
	
			FullResponse fullResponse  = gson.fromJson(response.getBody().toString(), FullResponse.class);
			List<String> diagnosis = fullResponse.getResponse().getDiagnosis();
			
			assertEquals(response.getStatusCodeValue(), 200);
			assertTrue(diagnosis==null);
			assertTrue(fullResponse.getResponse().isConsistent());
			
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}

	@Test
	public void inconsistentIncompatible01() {

		try {
			
			String inputFileName = "inconsistent_incompatible_01.txt";
			String jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
				
			ResponseEntity<?> response = keljuController.uploadDataCheckForConsistencyAndDoDiagnosis(jsonText);
			
			FullResponse fullResponse  = gson.fromJson(response.getBody().toString(), FullResponse.class);
			List<String> diagnosis = fullResponse.getResponse().getDiagnosis();
		
			assertTrue(diagnosis.contains("REQ1"));
			assertTrue(!diagnosis.contains("REQ2"));
			assertTrue(!diagnosis.contains("REQ3"));
			assertTrue(!diagnosis.contains("REQ4"));
			assertTrue(!diagnosis.contains("REQ5"));
			assertEquals(fullResponse.getResponse().isConsistent(), false);
			assertEquals(response.getStatusCodeValue(), 200);
			
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void inconsistentIncompatibleResourceExceeded() {

		try {
			
			String inputFileName = "inconsistent_incompatible_resourceExceed.txt";
			String jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
				
			ResponseEntity<?> response = keljuController.uploadDataCheckForConsistencyAndDoDiagnosis(jsonText);

			FullResponse fullResponse  = gson.fromJson(response.getBody().toString(), FullResponse.class);
			List<String> diagnosis = fullResponse.getResponse().getDiagnosis();
			
			assertTrue(diagnosis.contains("REQ1"));
			assertTrue(diagnosis.contains("REQ3"));
			assertTrue(!diagnosis.contains("REQ2"));
			assertTrue(!diagnosis.contains("REQ4"));
			assertTrue(!diagnosis.contains("REQ5"));
			assertEquals(fullResponse.getResponse().isConsistent(), false);
			assertEquals(response.getStatusCodeValue(), 200);
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
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
	
	private static class FullResponse {
		private Response response;
		public Response getResponse() {
			return response;
		}
	}

	private static class Response {
		private List<String> diagnosis;
		private boolean consistent;
	
		public List<String> getDiagnosis() {
			return diagnosis;
		}
		public void setDiagnosis(List<String> diagnosis) {
			this.diagnosis = diagnosis;
		}
		public boolean isConsistent() {
			return consistent;
		}
		public void setConsistent(boolean consistent) {
			this.consistent = consistent;
		}
	}

}
