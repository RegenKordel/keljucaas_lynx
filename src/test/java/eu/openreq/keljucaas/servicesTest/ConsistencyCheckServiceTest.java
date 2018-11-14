package eu.openreq.keljucaas.servicesTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

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
	
	@Test

	public void Consistent01() {

		try {
			
			String inputFileName = "consistent_01.txt";
			String jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
				
			ResponseEntity<?> response = keljuController.uploadDataCheckForConsistencyAndDoDiagnosis(jsonText);
			assertEquals(response.getStatusCodeValue(), 200);
			System.out.println(response);
			System.out.println(response.getStatusCodeValue());
			System.out.println(response.getHeaders());
			System.out.println(response.getBody());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}

	@Test
	public void inconsistent_incompatible_01() {

		try {
			
			String inputFileName = "inconsistent_incompatible_01.txt";
			String jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
				
			ResponseEntity<?> response = keljuController.uploadDataCheckForConsistencyAndDoDiagnosis(jsonText);
			assertEquals(response.getStatusCodeValue(), 200);
			System.out.println(response);
			System.out.println(response.getStatusCodeValue());
			System.out.println(response.getHeaders());
			System.out.println(response.getBody());
		} catch (Exception e) {
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void Inconsistent_incompatible_resourceExceed() {

		try {
			
			String inputFileName = "inconsistent_incompatible_resourceExceed.txt";
			String jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
				
			ResponseEntity<?> response = keljuController.uploadDataCheckForConsistencyAndDoDiagnosis(jsonText);
			assertEquals(response.getStatusCodeValue(), 200);
			System.out.println(response);
			System.out.println(response.getStatusCodeValue());
			System.out.println(response.getHeaders());
			System.out.println(response.getBody());
			
			
			 Gson gson = new Gson();
			FullResponse fullResponse  = gson.fromJson(response.getBody().toString(), FullResponse.class);
			    System.out.println(gson.toJson(fullResponse));
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
		DiagnosisResponse response;
	}
	private static class DiagnosisResponse {
		public boolean consistent;
		public ArrayList<String> diagnosis;
		@Override
		public String toString() {
			return "DiagnosisResponse [consistent=" + consistent + ", diagnosis=" + diagnosis + "]";
		}
		
	}

}
