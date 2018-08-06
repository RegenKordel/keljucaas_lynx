package eu.openreq.keljucaas.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chocosolver.solver.Solution;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gson.Gson;

import eu.openreq.keljucaas.domain.ElementRelationTuple;
import eu.openreq.keljucaas.services.FormatTransformerService;
import eu.openreq.keljucaas.services.KeljuCSPPlanner;
import eu.openreq.keljucaas.services.KeljuService;
import eu.openreq.keljucaas.services.MurmeliModelParser;
import eu.openreq.keljucaas.services.ReleaseCSPPlanner;
import fi.helsinki.ese.murmeli.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@Controller
@RequestMapping("/")
public class KeljuController {
	
	private KeljuService service = new KeljuService();
	
	private FormatTransformerService transform = new FormatTransformerService();
	private List<ElementModel> savedModels = new ArrayList<>();
	private Gson gson = new Gson();
	
	@ApiOperation(value = "Return Hello World",
		    notes = "Return Hello World for testing or keepalive purposes")
	@RequestMapping(value = "/test", method = RequestMethod.GET)
    public @ResponseBody String greeting() {
        return "Hello World";
    }
	
	@ApiOperation(value = "Import Murmeli JSON model and save it",
			notes = "Import a model in JSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@RequestMapping(value = "/importModel", method = RequestMethod.POST)
	public ResponseEntity<?> importModel(@RequestBody String json) throws Exception {
		MurmeliModelParser parser = new MurmeliModelParser();
		ElementModel model = parser.parseMurmeliModel(json);
		savedModels.add(model);
		System.out.println(savedModels.get(0));
		return new ResponseEntity<>("Model saved", HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Find the transitive closure of an requirement",
			notes = "Accepts a Map containing a requirement id (String) as a key and the depth (int) as a value",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns a transitive closure of the requested requirement"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@RequestMapping(value = "/findTransitiveClosureOfElement", method = RequestMethod.POST)
	public ResponseEntity<?> findTransitiveClosureOfElement(@RequestBody Map<String, Integer> requested) throws Exception {
		System.out.println("In Kelju's findTransitiveClosureOfElement");

		ElementModel newModel = null;
		String reqId = null;
		for(String id : requested.keySet()) {
			reqId = id;
		}
		int depth = requested.get(reqId);
		
		System.out.println("ReqId is " + reqId + " depth is " + depth);
		
		for (ElementModel model : savedModels) {
			if (model.getElements().containsKey(reqId)) {
				Map<String, List<ElementRelationTuple>> graph = service.generateGraph(model);
				newModel = service.getTransitiveClosure(graph, reqId, depth);
			}
		}
		return new ResponseEntity<>(gson.toJson(newModel),HttpStatus.OK);
	}
	
//	@ApiOperation(value = "Find the transitive closure of an element",
//			notes = "Give an element nameID as a parameter",
//			response = String.class)
//	@ApiResponses(value = { 
//			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in Murmeli JSON format"),
//			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
//			@ApiResponse(code = 409, message = "Failure")}) 
//	@RequestMapping(value = "/findTransitiveClosureOfElement", method = RequestMethod.POST)
//	public ResponseEntity<?> findTransitiveClosureOfElement(@RequestBody String element) throws Exception {
//		ElementModel newModel = null;
//		for (ElementModel model : savedModels) {
//			if (model.getElements().containsKey(element)) {
//				Map<String, List<ElementRelationTuple>> graph = service.generateGraph(model);
//				newModel = service.getTransitiveClosure(graph, "QTWB-32", 5);
//			}
//		}
//		return new ResponseEntity<>(gson.toJson(newModel),HttpStatus.OK);
//	}
	
	
	
	@ApiOperation(value = "Import Murmeli JSON model",
			notes = "Import a model in JSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@RequestMapping(value = "/testKelju", method = RequestMethod.POST)
	public ResponseEntity<?> testKelju(@RequestBody String json) throws Exception {
		Gson gson = new Gson();
		
		MurmeliModelParser parser = new MurmeliModelParser();
		ElementModel model = parser.parseMurmeliModel(json);
		
		KeljuService service = new KeljuService();
		Map<String, List<ElementRelationTuple>> graph = service.generateGraph(model);
		
		
		
		ElementModel newModel = service.getTransitiveClosure(graph, "QTWB-32", 5);
		
		System.out.println(newModel.getElements());
		
			return new ResponseEntity<>(gson.toJson(newModel),HttpStatus.OK);
	}

	@ApiOperation(value = "Import Murmeli JSON model",
			notes = "Import a model in JSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@RequestMapping(value = "/uploadDataAndCheckForConsistency", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataAndCheckForConsistency(@RequestBody String json) throws Exception {
		
		System.out.println("Requirements received from Mulperi");
		
		MurmeliModelParser parser = new MurmeliModelParser();
		ElementModel model = parser.parseMurmeliModel(json);

		
		ReleaseCSPPlanner rcspGen = new ReleaseCSPPlanner(model);
		rcspGen.generateCSP();
		
		System.out.println("CSP done");
		
		boolean isConsistent = rcspGen.isReleasePlanConsistent();
		if (isConsistent) {
			return new ResponseEntity<>(
				transform.generateProjectJsonResponse(true, "Consistent", true),
				HttpStatus.OK);
		}
		
		return new ResponseEntity<>(
		transform.generateProjectJsonResponse(false, "Not consistent", false),
		HttpStatus.CONFLICT);
		
	}
	
	@ApiOperation(value = "Import Murmeli JSON model",
			notes = "Import a model in JSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@RequestMapping(value = "/uploadDataCheckForConsistencyAndDoDiagnosis", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataCheckForConsistencyAndDoDiagnosis(@RequestBody String json) throws Exception {
		
		System.out.println("Requirements received from Mulperi");
		
		MurmeliModelParser parser = new MurmeliModelParser();
		ElementModel model = parser.parseMurmeliModel(json);

		
		ReleaseCSPPlanner rcspGen = new ReleaseCSPPlanner(model);
		rcspGen.generateCSP();
		
		System.out.println("CSP done");
		
		boolean isConsistent = rcspGen.isReleasePlanConsistent();
		if (isConsistent) {
			return new ResponseEntity<>(
				transform.generateProjectJsonResponse(true, "Consistent", true),
				HttpStatus.OK);
		}
		
		String diagnosis = rcspGen.getDiagnosis();
		
		return new ResponseEntity<>(
				transform.generateProjectJsonResponse(false, diagnosis, true),
				HttpStatus.CONFLICT);
	}
	
//	private void printModel(ElementModel model) {
//      System.out.println("\nStatus name " + model.getAttributeValueTypes().get("status").getName());
//      System.out.println("Status ID " + model.getAttributeValueTypes().get("status").getID());
//      System.out.println("Status BaseType " + model.getAttributeValueTypes().get("status").getBaseType());
//      System.out.println("Status bound " + model.getAttributeValueTypes().get("status").getBound());
//      System.out.println("Status cardinality " + model.getAttributeValueTypes().get("status").getCardinality());
//      
//      System.out.println("\nBug nameID " + model.getElementTypes().get("bug").getNameID());
//      System.out.println("Bug attributedef 0 " + model.getElementTypes().get("bug").getAttributeDefinitions().get(0));
//      System.out.println("Bug potentialpart 0 " + model.getElementTypes().get("bug").getPotentialParts().get(0));
//      
//      System.out.println("\nREQ_1 name " + model.getElements().get("QTWB-24").getNameID());
//      System.out.println("REQ_1 type " + model.getElements().get("REQ_1").getType());
//      System.out.println("REQ_1 attributes " + model.getElements().get("REQ_1").getAttributes());
//      System.out.println("REQ_1 interfaces " + model.getElements().get("REQ_1").getProvidedInterfaces());
//      
//      System.out.println("\nSubcontainer nameID " + model.getsubContainers().get(0).getNameID());
//      System.out.println("Subcontainer children " + model.getsubContainers().get(0).getChildren());
//      System.out.println("Subcontainer attributes " + model.getsubContainers().get(0).getAttributes());
//      System.out.println("Subcontainer elements " + model.getsubContainers().get(0).getElements());
//      System.out.println("Subcontainer next " + model.getsubContainers().get(0).getNext());
//      
//      System.out.println("\nRootcontainer nameID " + model.getRootContainer().getNameID());
//      System.out.println("Rootcontainer attributes " + model.getRootContainer().getAttributes());
//      System.out.println("Rootcontainer children " + model.getRootContainer().getChildren());
//      System.out.println("Rootcontainer elements " + model.getRootContainer().getElements());
//      System.out.println("Rootcontainer next " + model.getRootContainer().getNext());
//      
//      System.out.println("\nRelation nameType " + model.getRelations().get(0).getNameType());
//      
//      System.out.println("\n" + model.getConstraints());
//      
//      System.out.println("\nAttributeValue name " + model.getAttributeValues().get(1).getName());
//      System.out.println("AttributeValue value " + model.getAttributeValues().get(1).getValue());
//      System.out.println("AttributeValue type " + model.getAttributeValues().get(1).getType());
//      System.out.println("AttributeValue source " + model.getAttributeValues().get(1).getSource());
//	}
	
	/**
	 * Check Consistency and compute diagnosis if not consistent 
	 * @param content XML with model and optionally configuration tags
	 * @return
	 */
	@ApiOperation(value = "Check Consistency and compute diagnosis if not consistent ",
		    notes = "Check Consistency and compute diagnosis if not consistent ")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, configuration returned"),
			@ApiResponse(code = 201, message = "Success, model saved"),
			@ApiResponse(code = 400, message = "Failure"),
			@ApiResponse(code = 409, message = "Failure, configuration with selected features is impossible")}) 
	@ResponseBody
	@RequestMapping(value = "/checkAndDiagnose", method = RequestMethod.POST)
	public ResponseEntity<String> checkAndDiagnose(@RequestBody String content) {
		return null; //service.checkConsistencyAndDiagnose(content);
	}
}
