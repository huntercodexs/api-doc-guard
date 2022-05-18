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
public class ApiDocGuardAuthentiq {

	@Autowired
	ApiDocGuardHelper apiDocGuardHelper;

	@Autowired
	ApiDocGuardViewer apiDocGuardViewer;

	@Autowired
	ApiDocGuardRedirect apiDocGuardRedirect;

	@Operation(hidden = true)
	@GetMapping(path = {
			/*Authentiq*/
			"/authentiq",
			"/authentiq/",
			"/authentiq/login",
			"/authentiq/sign",
			"/authentiq/viewer",
			"/authentiq/logout",
			"/authentiq/doc-protected",
			"/authentiq/index",
			"/authentiq/index.html",

			/*Authentiq-API*/
			"/authentiq-api",
			"/authentiq-api/",
			"/authentiq-api/login",
			"/authentiq-api/sign",
			"/authentiq-api/viewer",
			"/authentiq-api/logout",
			"/authentiq-api/doc-protected",
			"/authentiq-api/index",
			"/authentiq-api/index.html"
	})
	public String routes(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		return apiDocGuardRedirect.sentinel(req, res, ses);
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"${springdoc.authentiq-api.path}/authentiq-api/{page}",
			"${springdoc.authentiq-api.path:/fake-prefix/fake-path}/{page}"
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
	@GetMapping(path = "/authentiq-api/protector")
	public ModelAndView authentiq(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		Map<String, String> body = new HashMap<>();
		try {
			body.put("ApiDocGuardRefresh", ses.getAttribute("ApiDocGuardRefresh").toString());
		} catch (RuntimeException re) {
			apiDocGuardHelper.debug("AUTHENTIQ-API -> PROTECTOR FOUND AN ERROR !!!", "warn");
		}
		return apiDocGuardViewer.protector(req, res, ses, body);
	}

	@Operation(hidden = true)
	@PostMapping(path = "/authentiq-api/protector", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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
