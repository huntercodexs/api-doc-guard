package com.huntercodexs.sample.apidocguard;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class ApiDocGuardSwagger {

	@Autowired
	ApiDocGuardHelper apiDocGuardHelper;

	@Autowired
	ApiDocGuardViewer apiDocGuardViewer;

	@Autowired
	ApiDocGuardRedirect apiDocGuardRedirect;

	@Operation(hidden = true)
	@GetMapping(path = {
			/*Swagger*/
			"/swagger",
			"/swagger/",
			"/swagger/login",
			"/swagger/sign",
			"/swagger/viewer",
			"/swagger/logout",
			/*"/swagger/protector",*/
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
			/*"/swagger-ui/protector",*/
			"/swagger-ui/doc-protected",
			"/swagger-ui/index",
			"/swagger-ui/index.html"
	})
	public String routes(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		return apiDocGuardRedirect.sentinel(req, res, ses);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/swagger-ui/protector")
	public ModelAndView swagger(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		Map<String, String> body = new HashMap<>();
		try {
			body.put("refresh", ses.getAttribute("refresh").toString());
		} catch (RuntimeException re) {
			apiDocGuardHelper.debug("SWAGGER-UI -> PROTECTOR FOUND AN ERROR !!!", "warn");
		}
		return apiDocGuardViewer.protector(req, res, ses, body);
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"${springdoc.swagger-ui.path}/swagger-ui/{page}",
			"${springdoc.swagger-ui.path:/fake-prefix/fake-path}/{page}"
	})
	public String custom(
			HttpServletRequest req,
			HttpServletResponse res,
			HttpSession ses,
			@PathVariable(required = false) String page
	) {
		return apiDocGuardRedirect.sentinel(req, res, ses);
	}

	@Operation(hidden = true)
	@PostMapping(path = "/swagger-ui/protector", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public ModelAndView protector(
			HttpServletRequest req,
			HttpServletResponse res,
			HttpSession ses,
			@Valid @RequestParam Map<String, String> body
	) {
		return apiDocGuardViewer.protector(req, res, ses, body);
	}
}
