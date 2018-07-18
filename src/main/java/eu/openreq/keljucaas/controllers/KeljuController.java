package eu.openreq.keljucaas.controllers;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

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

}
