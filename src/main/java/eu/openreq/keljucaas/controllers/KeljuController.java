package eu.openreq.keljucaas.controllers;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import fi.helsinki.ese.murmeli.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@Controller
@RequestMapping("/")
public class KeljuController {
	
	@ApiOperation(value = "Return Hello World",
		    notes = "Return Hello World for testing or keepalive purposes")
	@RequestMapping(value = "/test", method = RequestMethod.GET)
    public @ResponseBody String greeting() {
        return "Hello World";
    }

	@ApiOperation(value = "Import Murmeli JSON model",
			notes = "Import a model in JSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@RequestMapping(value = "requirementsToChoco", method = RequestMethod.POST)
	public ResponseEntity<?> importMurmeli(@RequestBody String json) throws Exception {
		
		ElementModel model = new eu.openreq.keljucaas.services.MurmeliModelParser().parseMurmeliModel(json);
		
		System.out.println("Requirements received from Mulperi");
		try {
			return new ResponseEntity<>("Requirements received: " + model, HttpStatus.ACCEPTED);
		}
		catch (Exception e) {
			return new ResponseEntity<>("Error", HttpStatus.EXPECTATION_FAILED); //change to something else?
		}
	}
}
