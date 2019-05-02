package eu.openreq.keljucaas.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import eu.openreq.keljucaas.domain.ElementRelationTuple;
import eu.openreq.keljucaas.domain.TransitiveClosure;
import eu.openreq.keljucaas.domain.release.ReleasePlanAnalysisDefinition;
import eu.openreq.keljucaas.domain.release.ReleasePlanInfo;
import eu.openreq.keljucaas.services.CSPPlanner;
import eu.openreq.keljucaas.services.ConsistencyCheckService;
import eu.openreq.keljucaas.services.MurmeliModelParser;
import eu.openreq.keljucaas.services.TransitiveClosureService;
import fi.helsinki.ese.murmeli.Element;
import fi.helsinki.ese.murmeli.ElementModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@RestController
@RequestMapping("/")
public class KeljuController {

	@Autowired
    TransitiveClosureService service;
	@Autowired
	ConsistencyCheckService transform;
	@Autowired
	MurmeliModelParser parser;
	
	private Map<String, List<ElementRelationTuple>> graph = new HashMap<>();
	private Map<String, ElementModel> savedModels = new HashMap<>();
	private Gson gson = new Gson();
	/**
	 * Value for the transitive closure search
	 */
	private static int searchDepth = 5;

	@ApiOperation(value = "Import Murmeli JSON model and save it", notes = "Import a model in JSON format", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Success, rgiven model is saved to the list of saved models."),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure") })
	@RequestMapping(value = "/importModel", method = RequestMethod.POST)
	public ResponseEntity<?> importModel(@RequestBody String json) throws Exception {

	//	MurmeliModelParser parser = new MurmeliModelParser();
		ElementModel model = parser.parseMurmeliModel(json);

		savedModels.put(model.getRootContainer().getNameID(), model);

		return new ResponseEntity<>("Model saved", HttpStatus.OK);
	}

	@ApiOperation(value = "Update the graph of models", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Success, the graph is updated according the list of saved models"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure") })
	@RequestMapping(value = "/updateGraph", method = RequestMethod.POST)
	public ResponseEntity<?> updateGraph() throws Exception {

		Map<String, List<ElementRelationTuple>> graph = this.service.generateGraph(this.savedModels.values());
		this.graph = graph;

		return new ResponseEntity<>("Graph updated", HttpStatus.OK);
	}

	@ApiOperation(value = "Import Murmeli JSON model, save it and update graph of requirements", notes = "Import a model in JSON format", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure") })
	@RequestMapping(value = "/importModelAndUpdateGraph", method = RequestMethod.POST)
	public ResponseEntity<?> importModelAndUpdateGraph(@RequestBody String json) throws Exception {

		this.importModel(json);
		this.updateGraph();

		return new ResponseEntity<>("Model saved and graph updated", HttpStatus.OK);
	}

	@ApiOperation(value = "Find the transitive closure of an element", notes = "Accepts requirementId as a String, returns the transitive closure to depth 5 as a default (depth 0 if there are no dependencies)", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Success, returns a transitive closure of the requested element"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure") })
	@RequestMapping(value = "/findTransitiveClosureOfElement", method = RequestMethod.POST)
	public ResponseEntity<?> findTransitiveClosureOfElement(@RequestBody String requirementId, 
			@RequestParam(required = false) Integer layerCount) throws Exception {

		TransitiveClosure newModel = null;
		String reqId = null;
		int depth = 0;
		String response = "";

		if (layerCount != null) {
			depth = layerCount;
		} else {
			depth = searchDepth;
		}
		
		try {
			// Check if the wanted element is in the graph, if it is not then look for the
			// mock element.
			if (this.graph.containsKey(requirementId + "-mock")) {
				reqId = requirementId + "-mock";
			} else {
				reqId = requirementId;
			}

			newModel = service.getTransitiveClosure(graph, reqId, depth);
			if (newModel.getModel().getElements().isEmpty()) {
				
				Element requested = findRequestedFromModels(reqId); 
				
				if (requested != null) {
					newModel.getModel().addElement(requested);
					
					List<String> layer = new ArrayList<String>();
					layer.add(reqId);
					newModel.getLayers().put(0, layer);
				}
			}

			service.addAttributesToTransitiveClosure(this.savedModels.values(), newModel.getModel());
			response = gson.toJson(newModel);
			return new ResponseEntity<String>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	// Finds the requested element from saved models.
	private Element findRequestedFromModels(String reqId) {

		for (ElementModel model : this.savedModels.values()) {
			if (model.getElements().containsKey(reqId)) {
				return model.getElements().get(reqId);
			}
		}
		return null;
	}

	@ApiOperation(value = "Returns consistency of received model", notes = "Import a model in OpenReq JSON format", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Returns consistency of received model"),
			@ApiResponse(code = 400, message = "Failure ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure") })
	@RequestMapping(value = "/uploadDataAndCheckForConsistency", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataAndCheckForConsistency(@RequestBody String json) throws Exception {

		// System.out.println("Requirements received from Mulperi");

	//	MurmeliModelParser parser = new MurmeliModelParser();
		ElementModel model = parser.parseMurmeliModel(json);
		
		ReleasePlanAnalysisDefinition wanted = new ReleasePlanAnalysisDefinition(ConsistencyCheckService.submitted, false, false);
		List <ReleasePlanAnalysisDefinition> wanteds = new LinkedList<>(); 
		wanteds.add(wanted);

		CSPPlanner rcspGen = new CSPPlanner(model, wanteds);
		rcspGen.performDiagnoses();
		ReleasePlanInfo releasePlanInfo = rcspGen.getReleasePlan(ConsistencyCheckService.submitted);

		boolean isConsistent = releasePlanInfo.isConsistent();
		if (isConsistent) {
			return new ResponseEntity<>(transform.generateProjectJsonResponse(true, "Consistent", true), HttpStatus.OK);
		}

		return new ResponseEntity<>(transform.generateProjectJsonResponse(false, "Not consistent", false),
				HttpStatus.OK);

	}

	@ApiOperation(value = "Returns consistency and diagnosis of received model", notes = "Import a model in OpenReq JSON format", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Returns consistency and diagnosis of received model"),
			@ApiResponse(code = 400, message = "Failure ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure") })
	@RequestMapping(value = "/uploadDataCheckForConsistencyAndDoDiagnosis", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataCheckForConsistencyAndDoDiagnosis(@RequestBody String json) throws Exception {

		 //System.out.println("Requirements received from Mulperi");
		 //System.out.println(json);
		 

	//	MurmeliModelParser parser = new MurmeliModelParser();
		ElementModel model = parser.parseMurmeliModel(json);

		List <ReleasePlanAnalysisDefinition> wanteds = new LinkedList<>();
		
		wanteds.add(new ReleasePlanAnalysisDefinition(ConsistencyCheckService.submitted, false, false));
		ReleasePlanAnalysisDefinition wanted = new ReleasePlanAnalysisDefinition(ConsistencyCheckService.diagnoseRequirements, true, false);
		wanted.setAnalyzeOnlyIfIncosistentPlan(ConsistencyCheckService.submitted);
		wanteds.add(wanted);

		CSPPlanner rcspGen = new CSPPlanner(model, wanteds);
		rcspGen.performDiagnoses();
		ReleasePlanInfo originalReleasePlanInfo = rcspGen.getReleasePlan(ConsistencyCheckService.submitted);

		boolean isConsistent = originalReleasePlanInfo.isConsistent();
		if (isConsistent) {
			return new ResponseEntity<>(transform.generateProjectJsonResponse(true, "Consistent", true), HttpStatus.OK);
		}

		ReleasePlanInfo diagnosedReleasePlanInfo = rcspGen.getReleasePlan(ConsistencyCheckService.diagnoseRequirements);

		String diagnosis = diagnosedReleasePlanInfo.getDiagnosis();

		return new ResponseEntity<>(transform.generateProjectJsonResponse(false, diagnosis, true), HttpStatus.OK);
	}
	//TODO enable this functionality, 
 
	@ApiOperation(value = "Returns consistency and diagnosis of received model", notes = "Import a model in OpenReq JSON format", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Returns consistency and extended diagnosis of received model"),
			@ApiResponse(code = 400, message = "Failure ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure") })
	@RequestMapping(value = "/consistencyCheckAndDiagnosis", method = RequestMethod.POST)
	public ResponseEntity<?> consistencyCheckAndDiagnosis(@RequestBody String json) throws Exception {

		 //System.out.println("Requirements received from Mulperi");
		 //System.out.println(json);
		 

	//	MurmeliModelParser parser = new MurmeliModelParser();
		ElementModel model = parser.parseMurmeliModel(json);
		
		List <ReleasePlanAnalysisDefinition> wanteds = new LinkedList<>();
		
		wanteds.add(new ReleasePlanAnalysisDefinition(ConsistencyCheckService.submitted, false, false)); 
		wanteds.add(new ReleasePlanAnalysisDefinition(ConsistencyCheckService.diagnoseRequirements, true, false));
		wanteds.add(new ReleasePlanAnalysisDefinition(ConsistencyCheckService.diagnoseRelationships, false, true));
		wanteds.add(new ReleasePlanAnalysisDefinition(ConsistencyCheckService.diagnoseRequirementsAndRelationships, true, true));

		for (ReleasePlanAnalysisDefinition wanted : wanteds) {
			if (!wanted.getPlanName().equals(ConsistencyCheckService.submitted)) 
				wanted.setAnalyzeOnlyIfIncosistentPlan(ConsistencyCheckService.submitted);
		}


		CSPPlanner rcspGen = new CSPPlanner(model, wanteds);
		rcspGen.performDiagnoses();
		ReleasePlanInfo originalReleasePlanInfo = rcspGen.getReleasePlan(ConsistencyCheckService.submitted);
		
		List<ReleasePlanInfo> releasePlanstoReport = new LinkedList<>();
		releasePlanstoReport.add(originalReleasePlanInfo);
		

		boolean isConsistent = originalReleasePlanInfo.isConsistent();
		if (isConsistent) {
			return new ResponseEntity<>(transform.generateProjectJsonResponseDetailed(releasePlanstoReport), HttpStatus.OK);
		}
		
		releasePlanstoReport.add(rcspGen.getReleasePlan(ConsistencyCheckService.diagnoseRequirements));
		releasePlanstoReport.add(rcspGen.getReleasePlan(ConsistencyCheckService.diagnoseRelationships));
		releasePlanstoReport.add(rcspGen.getReleasePlan(ConsistencyCheckService.diagnoseRequirementsAndRelationships));
				
		return new ResponseEntity<>(transform.generateProjectJsonResponseDetailed(releasePlanstoReport), HttpStatus.OK);
		
	}


}
