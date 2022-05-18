package com.huntercodexs.sample.apidocguard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class ApiDocGuardService {

    @Value("${apidocguard.server-name:localhost}")
    String apiDocServerName;

    @Value("${apidocguard.data.crypt.type:}")
    String dataCryptTpe;

    @Value("${apidocguard.type:swagger}")
    String apiDocGuardType;

    @Value("${springdoc.api-docs.path:/api-docs-guard}")
    String apiDocsPath;

    @Value("${apidocguard.url.show:true}")
    String showUrlApiDocs;

    @Value("${springdoc.swagger-ui.layout:StandaloneLayout}")
    String swaggerLayout;

    @Autowired
    ApiDocGuardRepository apiDocGuardRepository;

    public String sentinel(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
        if (req.getServletPath().equals("/doc-protect/logout")) {

            res.setHeader("Api-Doc-Guard-User", null);

            if (ses.getAttribute("ApiDocGuardFormSecret") != null) {
                ses.removeAttribute("ApiDocGuardFormSecret");
                System.out.println("[FORM SESSION REMOVED] ApiDocGuardFormSecret: "+ ses.getAttribute("ApiDocGuardFormSecret"));
            }

            if (ses.getAttribute("ApiDocGuardUser") != null) {
                ses.removeAttribute("ApiDocGuardUser");
                System.out.println("[USER SESSION REMOVED] ApiDocGuardUser: "+ ses.getAttribute("ApiDocGuardUser"));
            }

            return "redirect:/doc-protect/sentinel";
        }
        return "redirect:/doc-protect/logout";
    }

    public boolean login(HttpServletRequest req, HttpServletResponse res, Map<String, String> body) {

        try {

            String username = body.get("username");
            String password = body.get("password");

            if (username.equals("") || password.equals("")) {
                System.out.println("MISSING DATA TO LOGIN");
                return false;
            }

            String passwordCrypt = crypt(password);
            ApiDocGuardEntity apiDocGuardEntity = apiDocGuardRepository.findByLogin(username, passwordCrypt);

            if (apiDocGuardEntity != null) {
                System.out.println("LOGIN SUCCESS: " + body.get("username"));
                return true;
            }

        } catch (RuntimeException re) {
            System.out.println("LOGIN FAIL: " + re.getMessage());
        }

        return false;

    }

    public ModelAndView protect(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {

        ModelAndView modelAndView = new ModelAndView("apidocguard/login");

        switch (apiDocGuardType) {
            case "swagger":
                modelAndView.addObject("api_doc_guard_type", "/swagger-ui/protector");
                modelAndView.addObject("api_doc_guard_sec",
                        DigestUtils.md5DigestAsHex(
                                ses.getAttribute("ApiDocGuardFormSecret").toString().getBytes()
                        ));
                break;
            case "adobe":
                modelAndView.addObject("api_doc_guard_type", "/adobe-aem/protector");
                break;
            case "authentiq":
                modelAndView.addObject("api_doc_guard_type", "/authentiq-api/protector");
                break;
        }

        return modelAndView;
    }

    public ModelAndView index(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {
        System.out.println("GUARD TYPE: " + apiDocGuardType);

        switch (apiDocGuardType) {
            case "swagger":
                ModelAndView modelAndView = new ModelAndView("apidocguard/swagger-ui/index");
                modelAndView.addObject("api_docs_path", apiDocsPath);
                modelAndView.addObject("swagger_layout", swaggerLayout);
                modelAndView.addObject("show_url_api_docs", showUrlApiDocs);
                return modelAndView;
            case "adobe":
                return new ModelAndView("apidocguard/adobe-aem/index");
            case "authentiq":
                return new ModelAndView("apidocguard/authentiq-api/index");
        }

        throw new RuntimeException("Error on application, invalid Guard Type: " + apiDocGuardType);
    }

    public ModelAndView protector(HttpServletRequest req, HttpServletResponse res, HttpSession ses, Map<String, String> body) {

        secret(ses, body, null);
        firewall(req, ses, body);

        if (ses.getAttribute("ApiDocGuardUser") != null) {
            return index(req, res, ses);
        }

        if (login(req, res, body)) {
            res.setHeader("Api-Doc-Guard-User", body.get("username"));
            secret(ses, body, body.get("username"));
            return index(req, res, ses);
        }

        return protect(req, res, ses);
    }

    public void firewall(HttpServletRequest req, HttpSession ses, Map<String, String> body) {

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("FIREWALL IS RUNNING");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        String secretForm = null;

        String headerRequestHost = null; //localhost:31303, 192.168.15.14:31303
        String headerRequestUserAgent = null; //Mozilla Firefox, PostmanRuntime/7.28.4
        String headerSecFetchDest = null; //document
        String headerSecFetchMode = null; //navigate
        String headerRequestContentType = null; //application/x-www-form-urlencoded
        String headerRequestOrigin = null; //http://localhost:31303, http://192.168.15.14:31303
        String headerRequestReferer = null; //http://localhost:31303/doc-protect/sentinel, http://192.168.15.14:31303/doc-protect/sentinel

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
            System.out.println("[HEADER] Host: " + headerRequestHost);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [HEADER] Host " + re.getMessage());
        }

        try {
            headerRequestUserAgent = req.getHeader("User-Agent");
            System.out.println("[HEADER] User-Agent: " + headerRequestUserAgent);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [HEADER] User-Agent " + re.getMessage());
        }

        try {
            headerSecFetchDest = req.getHeader("Sec-Fetch-Dest");
            System.out.println("[HEADER] Sec-Fetch-Dest: " + headerSecFetchDest);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [HEADER] Sec-Fetch-Dest " + re.getMessage());
        }

        try {
            headerSecFetchMode = req.getHeader("Sec-Fetch-Mode");
            System.out.println("[HEADER] Sec-Fetch-Mode: " + headerSecFetchMode);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [HEADER] Sec-Fetch-Mode " + re.getMessage());
        }

        try {
            headerRequestContentType = req.getHeader("Content-Type");
            System.out.println("[HEADER] Content-Type: " + headerRequestContentType);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [HEADER] Content-Type " + re.getMessage());
        }

        try {
            headerRequestOrigin = req.getHeader("Origin");
            System.out.println("[HEADER] Origin: " + headerRequestOrigin);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [HEADER] Origin " + re.getMessage());
        }

        try {
            headerRequestReferer = req.getHeader("Referer");
            System.out.println("[HEADER] Referer: " + headerRequestReferer);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [HEADER] Referer " + re.getMessage());
        }

        try {
            requestMethod = req.getMethod();
            System.out.println("getMethod: "+requestMethod);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [SERVLET] getMethod " + re.getMessage());
        }

        try {
            remoteAddr = req.getRemoteAddr();
            System.out.println("getRemoteAddr: "+remoteAddr);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [SERVLET] getRemoteAddr " + re.getMessage());
        }

        try {
            remoteHost = req.getRemoteHost();
            System.out.println("getRemoteHost: "+remoteHost);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [SERVLET] getRemoteHost " + re.getMessage());
        }

        try {
            serverName = req.getServerName();
            System.out.println("getServerName: "+serverName);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [SERVLET] getServerName" + re.getMessage());
        }

        try {
            servletPath = req.getServletPath();
            System.out.println("getServletPath: "+servletPath);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [SERVLET] getServletPath " + re.getMessage());
        }

        try {
            authorization = req.getHeader("Authorization");
            System.out.println("Authorization: " + authorization);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [SERVLET] Authorization " + re.getMessage());
        }

        try {
            userAgent = req.getHeader("User-Agent");
            System.out.println("User-Agent: " + userAgent);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [SERVLET] User-Agent " + re.getMessage());
        }

        try {
            postmanToken = req.getHeader("Postman-Token");
            System.out.println("Postman-Token: " + postmanToken);
        } catch (RuntimeException re) {
            System.out.println("ERROR: [SERVLET] Postman-Token " + re.getMessage());
        }

        if (requestMethod != null && requestMethod.equals("POST")) {

            try {
                secretForm = body.get("apidocguard_sec");
                System.out.println("[SECRET] FORM: " + secretForm);
            } catch (RuntimeException re) {
                System.out.println("[SECRET] FORM: " + re.getMessage());
                throw new RuntimeException("Unauthorized");
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

            if (headerSecFetchDest != null && !headerSecFetchDest.equals("document")) {
                throw new RuntimeException("Unauthorized");
            }

            if (headerSecFetchMode != null && !headerSecFetchMode.equals("navigate")) {
                throw new RuntimeException("Unauthorized");
            }

            secretForm = DigestUtils.md5DigestAsHex(ses.getAttribute("ApiDocGuardFormSecret").toString().getBytes());
            authorization = DigestUtils.md5DigestAsHex(ses.getAttribute("ApiDocGuardFormSecret").toString().getBytes());
        }

        System.out.println("SESSION IS: "+ses.getAttribute("ApiDocGuardFormSecret").toString());
        if (!ses.getAttribute("ApiDocGuardFormSecret").toString().equals("")) {
            System.out.println("Authorization Checker: " + authorization);

            if (!authorization.equals(secretForm)) {
                throw new RuntimeException("Unauthorized");
            }
        }
    }

    public String crypt(String data) {
        if (dataCryptTpe.equals("md5")) {
            return DigestUtils.md5DigestAsHex(data.getBytes());
        } else if (dataCryptTpe.equals("bcrypt")) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            return bCryptPasswordEncoder.encode(data);
        }
        return data;
    }

    public String generator(Map<String, String> body) {
        String result = null;

        try {

            String username = body.get("username");
            String password = body.get("password");
            String name = body.get("name");
            String email = body.get("email");
            String level = body.get("level");
            String active = body.get("active");

            if (
                    username.equals("") ||
                            password.equals("") ||
                            name.equals("") ||
                            email.equals("") ||
                            level.equals("") ||
                            active.equals("")
            ) {
                return "Missing data, check your request";
            }

            Date now = new Date();
            String currentDate = new SimpleDateFormat("dd/MM/yyy HH:mm:ss").format(now);
            String passwordCrypt = crypt(password);

            result = "INSERT INTO api_doc_guard (active,createdAt,deletedAt,email,level,name,password,updatedAt,username) " +
                    "VALUES ('"+active+"','"+currentDate+"',NULL,'"+email+"','"+level+"','"+name+"','"+passwordCrypt+"',NULL,'"+username+"');";

        } catch (RuntimeException re) {
            return "Exception, " + re.getMessage();
        }

        return result;
    }

    private void secret(HttpSession ses, Map<String, String> body, String user) {
        if (body == null || body.size() <= 0) {
            ses.setAttribute("ApiDocGuardFormSecret", UUID.randomUUID().toString());
            System.out.println("[FORM SESSION CREATE] ApiDocGuardFormSecret: " + ses.getAttribute("ApiDocGuardFormSecret"));
        } else {
            System.out.println("[FORM SESSION EXISTS] ApiDocGuardFormSecret: " + ses.getAttribute("ApiDocGuardFormSecret"));

            if (user != null) {
                String userSession = "{\"username\": \"" + user + "\", \"id\": \"" + ses.getAttribute("ApiDocGuardFormSecret") + "\"}";
                ses.setAttribute("ApiDocGuardUser", userSession);
                System.out.println("[USER SESSION CREATE] ApiDocGuardUser: " + ses.getAttribute("ApiDocGuardUser"));
            }
        }
    }
}
