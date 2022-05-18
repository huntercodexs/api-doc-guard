package com.huntercodexs.sample.apidocguard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ApiDocGuardViewer {

    @Value("${apidocguard.type:swagger}")
    String apiDocGuardType;

    @Value("${springdoc.api-docs.path:/api-docs-guard}")
    String apiDocsPath;

    @Value("${apidocguard.url.show:true}")
    String showUrlApiDocs;

    @Value("${springdoc.swagger-ui.layout:StandaloneLayout}")
    String swaggerLayout;

    @Autowired
    ApiDocGuardFirewall apiDocGuardFirewall;

    @Autowired
    ApiDocGuardHelper apiDocGuardHelper;

    public ModelAndView protect(HttpServletRequest req, HttpServletResponse res, HttpSession ses, String flag) {

        Map<String, String> body = new HashMap<>();
        body.put("condition", flag);

        apiDocGuardHelper.secret(ses, null, null);
        apiDocGuardHelper.debug("FIREWALL[1]", "info");
        apiDocGuardFirewall.run(req, ses, body);

        if (apiDocGuardType.equals("swagger")) {
            ModelAndView modelAndView = new ModelAndView("apidocguard/login");

            modelAndView.addObject("api_doc_guard_type", "/swagger-ui/protector");
            modelAndView.addObject("api_doc_guard_sec",
                    DigestUtils.md5DigestAsHex(
                            ses.getAttribute("ApiDocGuardFormSecret").toString().getBytes()
                    ));
            return modelAndView;
        }

        return null;
    }

    public ModelAndView index(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
        apiDocGuardHelper.debug("GUARD TYPE: " + apiDocGuardType, "info");

        if (apiDocGuardType.equals("swagger")) {
            ModelAndView modelAndView = new ModelAndView("apidocguard/swagger-ui/index");
            modelAndView.addObject("api_docs_path", apiDocsPath);
            modelAndView.addObject("swagger_layout", swaggerLayout);
            modelAndView.addObject("show_url_api_docs", showUrlApiDocs);
            return modelAndView;
        }

        throw new RuntimeException("Error on application, invalid Guard Type: " + apiDocGuardType);
    }

    public ModelAndView protector(HttpServletRequest req, HttpServletResponse res, HttpSession ses, Map<String, String> body) {

        try {
            if (body != null && body.get("refresh").equals("1")) {
                apiDocGuardHelper.debug("FIREWALL[2]", "info");
                apiDocGuardFirewall.run(req, ses, body);
                return index(req, res, ses);
            }
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("Exception: " + re.getMessage(), "warn");
            apiDocGuardHelper.debug("Continue...", "info");
        }

        apiDocGuardHelper.secret(ses, body, null);
        apiDocGuardHelper.debug("FIREWALL[3]", "info");
        apiDocGuardFirewall.run(req, ses, body);

        if (ses.getAttribute("ApiDocGuardUser") != null) {
            apiDocGuardHelper.debug("[SESSION EXISTS IN PROTECTOR] " + ses.getAttribute("ApiDocGuardUser"), "info");
            return index(req, res, ses);
        }

        if (apiDocGuardHelper.login(req, res, body)) {
            res.setHeader("Api-Doc-Guard-User", Objects.requireNonNull(body).get("username"));
            apiDocGuardHelper.secret(ses, body, body.get("username"));
            return index(req, res, ses);
        }

        return protect(req, res, ses, "--login-fail");
    }

}
