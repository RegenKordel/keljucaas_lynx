package eu.openreq.keljucaas.servicesTest;
import static org.junit.Assert.assertFalse;
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
import eu.openreq.keljucaas.domain.TransitiveClosure;
import fi.helsinki.ese.murmeli.Parts;
import fi.helsinki.ese.murmeli.Relationship;



@RunWith(SpringRunner.class)
@SpringBootTest

public class TransitiveClosureTest {
	@Autowired
	private KeljuController keljuController;
	
	private Gson gson;
	

    @Before
    public void setUp() {
    	gson = new Gson();
    }

    @Test
    public void transitiveClosure() {
    	
    	try {		
			String inputFileName = "qtwbmurmelina.txt";
			String jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
				
			keljuController.importModelAndUpdateGraph(jsonText);
	
			ResponseEntity<?> response = keljuController.findTransitiveClosureOfElement("QTWB-6", 5);
			
			TransitiveClosure tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
			assertFalse(tc == null);
			assertTrue(tc.getLayers().get(1).contains("QTBUG-51306-mock") && tc.getLayers().get(1).contains("QTBUG-63307-mock") && tc.getLayers().get(0).contains("QTWB-6"));
			assertTrue(tc.getLayers().get(1).size() == 2);
			assertTrue(tc.getLayers().get(2) == null);
			assertTrue(tc.getModel().getElements().keySet().size() == 3);
			assertTrue(tc.getModel().getRelations().size() == 2);
			
			inputFileName = "incrementaltest.txt";
			jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
				
			keljuController.updateModel(jsonText);
	
			response = keljuController.findTransitiveClosureOfElement("QTWB-6", 5);
			
			tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
			assertFalse(tc == null);
			assertTrue(tc.getLayers().get(1).contains("QTBUG-51306"));
			assertTrue(tc.getLayers().get(1).contains("QTBUG-63307-mock"));
			assertTrue(tc.getLayers().get(0).contains("QTWB-6"));
			assertTrue(tc.getLayers().get(2).contains("QTWB-987"));
			assertTrue(tc.getLayers().get(1).contains("QTWB-23"));
			assertTrue(tc.getLayers().get(3).contains("QTBUG-0-mock"));
			assertTrue(tc.getLayers().get(4) == null);
			assertTrue(tc.getModel().getElements().keySet().size() == 6);
			assertTrue(tc.getModel().getRelations().size() == 5);
			assertTrue(tc.getModel().getElements().get("QTWB-6").getAttributes().get("priority") == 53);
			
			response = keljuController.findTransitiveClosureOfElement("QTWB-31", 5);
			
			tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
			assertFalse(tc.getModel().getElements().containsKey("QTWB-30-mock"));
			assertTrue(tc.getModel().getElements().containsKey("QTWB-30"));
			assertFalse(tc.getModel().getRelations().contains(new Relationship(null, "QTWB-31", "QTWB-30-mock")));
			assertTrue(tc.getModel().getRelations().contains(new Relationship(null, "QTWB-31", "QTWB-30")));
			
			response = keljuController.findTransitiveClosureOfElement("QTWB-30", 5);
			
			tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
			assertFalse(tc.getModel().getElements().containsKey("QTWB-30-mock"));
			assertTrue(tc.getModel().getElements().containsKey("QTWB-30"));
			assertFalse(tc.getModel().getRelations().contains(new Relationship(null, "QTWB-31", "QTWB-30-mock")));
			assertTrue(tc.getModel().getRelations().contains(new Relationship(null, "QTWB-31", "QTWB-30")));
			
			response = keljuController.findTransitiveClosureOfElement("QTWB-30-mock", 5);
			
			tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
			//System.out.println(tc.getModel().getElements());
			
			//assertTrue(tc.getModel().getElements().isEmpty());
			//assertTrue(tc.getLayers().isEmpty());
			
			inputFileName = "importNewProjectTest.txt";
			jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
			
			keljuController.importModelAndUpdateGraph(jsonText);
	
			response = keljuController.findTransitiveClosureOfElement("QTWB-6", 10);
			
			tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
			assertFalse(tc == null);
			assertTrue(tc.getLayers().get(1).contains("QTBUG-51306") && tc.getLayers().get(1).contains("QTBUG-63307") && tc.getLayers().get(0).contains("QTWB-6"));
			assertTrue(tc.getLayers().get(2).contains("QTWB-987") && tc.getLayers().get(1).contains("QTWB-23") && tc.getLayers().get(3).contains("QTBUG-0"));
			assertTrue(tc.getLayers().get(6) == null);
			assertTrue(tc.getModel().getElements().keySet().size() == 10);
			assertTrue(tc.getModel().getRelations().size() == 9);
			
			boolean decompTestHelp = false;
			
			for (Parts parts : tc.getModel().getElements().get("QTBUG-63307").getParts()) {
				if (parts.getRole().equals("decomposition")) {
					if (parts.getParts().contains("QTWB-6")) {
						decompTestHelp = true;
					}
				}
			}
			
			assertTrue(decompTestHelp);
			
			response = keljuController.findTransitiveClosureOfElement("QTBUG-0", 1);
			tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
			System.out.println(tc.getLayers());
			
			assertFalse(tc.getLayers().get(0).contains("QTBUG-0-mock"));
			assertTrue(tc.getLayers().size() == 2);
			assertTrue(tc.getNumberOfOutpointingRelations() == 4);
			
			response = keljuController.findTransitiveClosureOfElement("QTUNREAL-0", 5);
			tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
			assertTrue(tc.getLayers().isEmpty());
			
			inputFileName = "newupdate.txt";
			jsonText = readTestJson(inputFileName);
			if (jsonText == null)
				fail("Could not read input string from '" + inputFileName +"'.");
			
			keljuController.updateModel(jsonText);
	
			response = keljuController.findTransitiveClosureOfElement("QTWB-6", 5);
			
			tc = gson.fromJson(response.getBody().toString(), TransitiveClosure.class);
			
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
		@SuppressWarnings("unused")
		public void setDiagnosis(List<String> diagnosis) {
			this.diagnosis = diagnosis;
		}
		public boolean isConsistent() {
			return consistent;
		}
		@SuppressWarnings("unused")
		public void setConsistent(boolean consistent) {
			this.consistent = consistent;
		}
	}
}
