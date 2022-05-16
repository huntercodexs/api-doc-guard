package com.huntercodexs.sample.apidocguard;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class ApiDocGuardAuthentiqApi {

	@Autowired
    ApiDocGuardService apiDocGuardService;

	@Operation(hidden = true)
	@GetMapping(path = {
			/*Authentiq*/
			"/authentiq-api",
			"/authentiq-api/",
			"/authentiq-api/login",
			"/authentiq-api/sign",
			"/authentiq-api/viewer",
			"/authentiq-api/logout",
			"/authentiq-api/protector",
			"/authentiq-api/doc-protected",
			"/authentiq-api/index",
			"/authentiq-api/index.html"
	})
	public String sentinelAuthentiqRoute(HttpServletRequest request, HttpServletResponse response) {
		if (request.getServletPath().equals("/doc-protect/logout")) {
			response.setHeader("Api-Doc-Guard-User", null);
			return "redirect:/doc-protect/sentinel";
		}
		return "redirect:/doc-protect/logout";
	}

	@Operation(hidden = true)
	@PostMapping(path = "/authentiq-api/protector", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public ModelAndView protector(
			HttpServletRequest req,
			HttpServletResponse res,
			@Valid @RequestParam Map<String, String> body
	) {
		return apiDocGuardService.protector(req, res, body);
	}

}
