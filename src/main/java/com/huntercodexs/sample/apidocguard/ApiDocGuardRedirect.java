package com.huntercodexs.sample.apidocguard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class ApiDocGuardRedirect {

    @Value("${apidocguard.type:swagger}")
    String apiDocGuardType;

    @Autowired
    ApiDocGuardHelper apiDocGuardHelper;

    public String sentinel(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
        /*Session is active*/
        if (ses.getAttribute("ApiDocGuardUser") != null) {
            return "redirect:/doc-protect/router";
        }
        return "redirect:/doc-protect/protector";
    }

    public String router(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
        /*Session is active*/
        if (ses.getAttribute("ApiDocGuardUser") != null) {
            ses.setAttribute("refresh", "1");
            switch (apiDocGuardType) {
                case "swagger":
                    return "redirect:/swagger-ui/protector";
                case "adobe":
                    return "redirect:/adobe-aem/protector";
                case "authentiq":
                    return "redirect:/authentiq-api/protector";
            }
        }
        ses.setAttribute("refresh", null);
        return "redirect:/doc-protect/protector";
    }

    public String logout(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
        res.setHeader("Api-Doc-Guard-User", null);

        if (ses.getAttribute("ApiDocGuardFormSecret") != null) {
            ses.removeAttribute("ApiDocGuardFormSecret");
            apiDocGuardHelper.debug("[FORM SESSION REMOVED] ApiDocGuardFormSecret: "+ ses.getAttribute("ApiDocGuardFormSecret"), "");
        }

        if (ses.getAttribute("ApiDocGuardUser") != null) {
            ses.removeAttribute("ApiDocGuardUser");
            apiDocGuardHelper.debug("[USER SESSION REMOVED] ApiDocGuardUser: "+ ses.getAttribute("ApiDocGuardUser"), "");
        }

        return "redirect:/doc-protect/login";
    }

}