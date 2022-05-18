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
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class ApiDocGuard {

	@Autowired
	ApiDocGuardHelper apiDocGuardHelper;

	@Autowired
	ApiDocGuardViewer apiDocGuardViewer;

	@Autowired
	ApiDocGuardRedirect apiDocGuardRedirect;

	@Operation(hidden = true)
	@GetMapping(path = {
			"${springdoc.api-docs.path:/api/docs}",
			"${springdoc.api-docs.path:/api/docs}/swagger-config",
			"/api-docs",
			"/api-docs/swagger-config",
			"/api-doc-guard",
			"/api-doc-guard/swagger-config",
			"/api-docs-guard",
			"/api-docs-guard/swagger-config"
	})
	public String apiDocsRoute(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		return apiDocGuardRedirect.logout(req, res, ses);
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"/",
			"/error",
			"/doc-protect",
			"/doc-protect/",
			"/doc-protect/sign",
			"/doc-protect/viewer",
			/*"/doc-protect/protector",*/
			"/doc-protect/doc-protected",
			"/doc-protect/index.html",
			/*Swagger*/
			"/doc-protect/swagger",
			"/doc-protect/swagger-ui",
			/*Adobe*/
			"/doc-protect/adobe",
			"/doc-protect/adobe-aem",
			/*Authentiq*/
			"/doc-protect/authentiq",
			"/doc-protect/authentiq-api"
	})
	public String docProtectRoute(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		return apiDocGuardRedirect.sentinel(req, res, ses);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/login")
	public ModelAndView login(HttpSession ses) {
		return apiDocGuardViewer.protect(null, null, ses, "--login");
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/logout")
	public String logout(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		return apiDocGuardRedirect.logout(req, res, ses);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/router")
	public String router(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		return apiDocGuardRedirect.router(req, res, ses);
	}

	@Operation(hidden = true)
	@GetMapping(path = "/doc-protect/protector")
	public ModelAndView protector(HttpSession ses) {
		return apiDocGuardViewer.protector(null, null, ses, null);
	}

	@Operation(hidden = true)
	@PostMapping(path = "/doc-protect/generator/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ResponseBody
	public String generator(@Valid @RequestParam Map<String, String> body) {
		return apiDocGuardHelper.generator(body);
	}

}
