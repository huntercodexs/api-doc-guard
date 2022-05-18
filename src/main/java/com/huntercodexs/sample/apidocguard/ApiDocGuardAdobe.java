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
public class ApiDocGuardAdobe {

	@Autowired
	ApiDocGuardHelper apiDocGuardHelper;

	@Autowired
	ApiDocGuardViewer apiDocGuardViewer;

	@Autowired
	ApiDocGuardRedirect apiDocGuardRedirect;

	@Operation(hidden = true)
	@GetMapping(path = {
			/*Adobe*/
			"/adobe",
			"/adobe/",
			"/adobe/login",
			"/adobe/sign",
			"/adobe/viewer",
			"/adobe/logout",
			"/adobe/doc-protected",
			"/adobe/index",
			"/adobe/index.html",

			/*Adobe-AEM*/
			"/adobe-aem",
			"/adobe-aem/",
			"/adobe-aem/login",
			"/adobe-aem/sign",
			"/adobe-aem/viewer",
			"/adobe-aem/logout",
			"/adobe-aem/doc-protected",
			"/adobe-aem/index",
			"/adobe-aem/index.html"
	})
	public String routes(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		return apiDocGuardRedirect.sentinel(req, res, ses);
	}

	@Operation(hidden = true)
	@GetMapping(path = {
			"${springdoc.adobe-aem.path}/adobe-aem/{page}",
			"${springdoc.adobe-aem.path:/fake-prefix/fake-path}/{page}"
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
	@GetMapping(path = "/adobe-aem/protector")
	public ModelAndView adobe(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
		Map<String, String> body = new HashMap<>();
		try {
			body.put("ApiDocGuardRefresh", ses.getAttribute("ApiDocGuardRefresh").toString());
		} catch (RuntimeException re) {
			apiDocGuardHelper.debug("ADOBE-AEM -> PROTECTOR FOUND AN ERROR !!!", "warn");
		}
		return apiDocGuardViewer.protector(req, res, ses, body);
	}

	@Operation(hidden = true)
	@PostMapping(path = "/adobe-aem/protector", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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
