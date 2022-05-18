package com.huntercodexs.sample.apidocguard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class ApiDocGuardFirewall {

    @Value("${apidocguard.server-name:localhost}")
    String apiDocServerName;

    @Autowired
    ApiDocGuardHelper apiDocGuardHelper;

    public void run(HttpServletRequest req, HttpSession ses, Map<String, String> body) {

        apiDocGuardHelper.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", "");
        apiDocGuardHelper.debug("FIREWALL IS RUNNING", "");
        apiDocGuardHelper.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", "");

        String secretForm = null;

        String headerRequestHost = null; //localhost:31303, 192.168.15.14:31303
        String headerRequestUserAgent = null; //Mozilla Firefox, PostmanRuntime/7.28.4
        String headerSecFetchDest = null; //document
        String headerSecFetchMode = null; //navigate
        String headerRequestContentType = null; //application/x-www-form-urlencoded
        String headerRequestOrigin = null; //http://localhost:31303, http://192.168.15.14:31303
        String headerRequestReferer = null; //http://localhost:31303/doc-protect/uri, http://192.168.15.14:31303/doc-protect/uri

        String requestMethod = null; //POST, GET
        String remoteAddr = null; //127.0.0.1, 0:0:0:0:0:0:0:1, 192.168.15.13
        String remoteHost = null; //127.0.0.1, 0:0:0:0:0:0:0:1, 192.168.15.13
        String serverName = null; //localhost, 192.168.15.14
        String servletPath = null; //swagger-ui/protector
        String authorization = null; //Bearer F1F2F3F34F4F5F6F7FF8...
        String userAgent = null; //Mozilla Firefox, PostmanRuntime/7.28.4
        String postmanToken = null; //96f17e89-cacf-41d2-be8d-96aafd870b3a

        try {
            headerRequestHost = req.getHeader("Host");
            apiDocGuardHelper.debug("(HEADER) Host: " + headerRequestHost, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(HEADER) Host " + re.getMessage(), "error");
        }

        try {
            headerRequestUserAgent = req.getHeader("User-Agent");
            apiDocGuardHelper.debug("(HEADER) User-Agent: " + headerRequestUserAgent, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(HEADER) User-Agent " + re.getMessage(), "error");
        }

        try {
            headerSecFetchDest = req.getHeader("Sec-Fetch-Dest");
            apiDocGuardHelper.debug("(HEADER) Sec-Fetch-Dest: " + headerSecFetchDest, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(HEADER) Sec-Fetch-Dest " + re.getMessage(), "error");
        }

        try {
            headerSecFetchMode = req.getHeader("Sec-Fetch-Mode");
            apiDocGuardHelper.debug("(HEADER) Sec-Fetch-Mode: " + headerSecFetchMode, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(HEADER) Sec-Fetch-Mode " + re.getMessage(), "error");
        }

        try {
            headerRequestContentType = req.getHeader("Content-Type");
            apiDocGuardHelper.debug("(HEADER) Content-Type: " + headerRequestContentType, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(HEADER) Content-Type " + re.getMessage(), "error");
        }

        try {
            headerRequestOrigin = req.getHeader("Origin");
            apiDocGuardHelper.debug("(HEADER) Origin: " + headerRequestOrigin, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(HEADER) Origin " + re.getMessage(), "error");
        }

        try {
            headerRequestReferer = req.getHeader("Referer");
            apiDocGuardHelper.debug("(HEADER) Referer: " + headerRequestReferer, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(HEADER) Referer " + re.getMessage(), "error");
        }

        try {
            requestMethod = req.getMethod();
            apiDocGuardHelper.debug("getMethod: "+requestMethod, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(SERVLET) getMethod " + re.getMessage(), "error");
        }

        try {
            remoteAddr = req.getRemoteAddr();
            apiDocGuardHelper.debug("getRemoteAddr: "+remoteAddr, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(SERVLET) getRemoteAddr " + re.getMessage(), "error");
        }

        try {
            remoteHost = req.getRemoteHost();
            apiDocGuardHelper.debug("getRemoteHost: "+remoteHost, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(SERVLET) getRemoteHost " + re.getMessage(), "error");
        }

        try {
            serverName = req.getServerName();
            apiDocGuardHelper.debug("getServerName: "+serverName, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(SERVLET) getServerName" + re.getMessage(), "error");
        }

        try {
            servletPath = req.getServletPath();
            apiDocGuardHelper.debug("getServletPath: "+servletPath, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(SERVLET) getServletPath " + re.getMessage(), "error");
        }

        try {
            authorization = req.getHeader("Authorization");
            apiDocGuardHelper.debug("Authorization: " + authorization, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(SERVLET) Authorization " + re.getMessage(), "error");
        }

        try {
            userAgent = req.getHeader("User-Agent");
            apiDocGuardHelper.debug("User-Agent: " + userAgent, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(SERVLET) User-Agent " + re.getMessage(), "error");
        }

        try {
            postmanToken = req.getHeader("Postman-Token");
            apiDocGuardHelper.debug("Postman-Token: " + postmanToken, "info");
        } catch (RuntimeException re) {
            apiDocGuardHelper.debug("(SERVLET) Postman-Token " + re.getMessage(), "error");
        }

        if (requestMethod != null && requestMethod.equals("POST")) {

            try {
                secretForm = body.get("apidocguard_sec");
                apiDocGuardHelper.debug("[FORM] Secret: " + secretForm, "info");
            } catch (RuntimeException re) {
                apiDocGuardHelper.debug("[FORM] Secret: " + re.getMessage(), "error");
            }

            if (headerRequestHost != null && !headerRequestHost.contains("localhost") && !headerRequestHost.contains(apiDocServerName)) {
                throw new RuntimeException("Unauthorized");
            }
            if (headerRequestUserAgent != null && headerRequestUserAgent.contains("PostmanRuntime")) {
                throw new RuntimeException("Unauthorized");
            }
            if (headerRequestContentType != null && !headerRequestContentType.equals("application/x-www-form-urlencoded")) {
                throw new RuntimeException("Wrong Request");
            }
            if (
                    headerRequestOrigin != null &&
                    !headerRequestOrigin.contains("http://localhost") &&
                    !headerRequestOrigin.contains("http://"+apiDocServerName) &&
                    !headerRequestOrigin.contains("https://"+apiDocServerName)
            ) {
                throw new RuntimeException("Unauthorized");
            }
            if (
                    headerRequestReferer != null &&
                    !headerRequestReferer.contains("http://localhost") &&
                    !headerRequestReferer.contains("http://"+apiDocServerName) &&
                    !headerRequestReferer.contains("https://"+apiDocServerName)
            ) {
                throw new RuntimeException("Unauthorized");
            }

            if (remoteAddr != null && remoteAddr.equals("0:0:0:0:0:0:0:1")) {
                if (headerRequestUserAgent != null && headerRequestUserAgent.contains("PostmanRuntime")) {
                    throw new RuntimeException("Unauthorized");
                }
            }
            if (remoteHost != null && remoteHost.equals("0:0:0:0:0:0:0:1")) {
                if (headerRequestUserAgent != null && headerRequestUserAgent.contains("PostmanRuntime")) {
                    throw new RuntimeException("Unauthorized");
                }
            }
            if (serverName != null && !serverName.equals("localhost") && !serverName.equals(apiDocServerName)) {
                throw new RuntimeException("Unauthorized");
            }
            if (servletPath != null && !servletPath.contains("protector")) {
                throw new RuntimeException("Wrong Request");
            }
            if (userAgent != null && userAgent.contains("PostmanRuntime")) {
                throw new RuntimeException("Unauthorized");
            }
            if (postmanToken != null) {
                throw new RuntimeException("Unauthorized");
            }
            if (authorization == null) {
                authorization = DigestUtils.md5DigestAsHex(ses.getAttribute("ApiDocGuardFormSecret").toString().getBytes());
            }

        } else {

            if (
                headerSecFetchDest != null &&
                !headerSecFetchDest.equals("document") &&
                !headerSecFetchDest.equals("style") &&
                !headerSecFetchDest.equals("script")
            ) {
                throw new RuntimeException("Unauthorized");
            }

            if (
                headerSecFetchMode != null &&
                !headerSecFetchMode.equals("navigate") &&
                !headerSecFetchMode.equals("no-cors")) {
                throw new RuntimeException("Unauthorized");
            }

            secretForm = DigestUtils.md5DigestAsHex(ses.getAttribute("ApiDocGuardFormSecret").toString().getBytes());
            authorization = DigestUtils.md5DigestAsHex(ses.getAttribute("ApiDocGuardFormSecret").toString().getBytes());
        }

        apiDocGuardHelper.debug("SESSION IS: "+ses.getAttribute("ApiDocGuardFormSecret").toString(), "info");

        if (!ses.getAttribute("ApiDocGuardFormSecret").toString().equals("")) {

            apiDocGuardHelper.debug("Authorization Checker: " + authorization, "info");

            if (!authorization.equals(secretForm)) {
                if (body.get("condition") != null && !body.get("condition").equals("--login-fail")) {
                    throw new RuntimeException("Unauthorized");
                }
            }
        }
    }

}
