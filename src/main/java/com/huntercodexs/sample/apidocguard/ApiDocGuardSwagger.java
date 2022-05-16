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
public class ApiDocGuardSwagger {

	@Autowired
    ApiDocGuardService apiDocGuardService;

	@Operation(hidden = true)
	@GetMapping(path = {
			/*Swagger*/
			"/swagger",
			"/swagger/",
			"/swagger/login",
			"/swagger/sign",
			"/swagger/viewer",
			"/swagger/logout",
			"/swagger/protector",
			"/swagger/doc-protected",
			"/swagger/index",
			"/swagger/index.html",

			/*Swagger-UI*/
			"/swagger-ui",
			"/swagger-ui/",
			"/swagger-ui/login",
			"/swagger-ui/sign",
			"/swagger-ui/viewer",
			"/swagger-ui/logout",
			"/swagger-ui/protector",
			"/swagger-ui/doc-protected",
			"/swagger-ui/index",
			"/swagger-ui/index.html"
	})
	public String sentinelSwaggerRoute(HttpServletRequest request, HttpServletResponse response) {
		if (request.getServletPath().equals("/doc-protect/logout")) {
			response.setHeader("Api-Doc-Guard-User", null);
			return "redirect:/doc-protect/sentinel";
		}
		return "redirect:/doc-protect/logout";
	}

	@Operation(hidden = true)
	@PostMapping(path = "/swagger-ui/protector", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public ModelAndView protector(
			HttpServletRequest req,
			HttpServletResponse res,
			@Valid @RequestParam Map<String, String> body
	) {
		return apiDocGuardService.protector(req, res, body);
	}

}
