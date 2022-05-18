package com.huntercodexs.sample.apidocguard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class ApiDocGuardHelper {

    private static final boolean API_DOC_GUARD_DEBUG = true;

    @Value("${apidocguard.data.crypt.type:}")
    String dataCryptTpe;

    @Autowired
    ApiDocGuardRepository apiDocGuardRepository;

    public void debug(String msg, String label) {
        if (API_DOC_GUARD_DEBUG) {
            String lab = "";
            switch (label) {
                case "error":
                    lab = "[ERROR]";
                    break;
                case "info":
                    lab = "[INFO]";
                    break;
                case "warn":
                    lab = "[WARNING]";
                    break;
                case "except":
                    lab = "[EXCEPTION]";
                    break;

            }
            System.out.println(lab + " " + msg);
        }
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

    public void secret(HttpSession ses, Map<String, String> body, String user) {
        if (body == null || body.size() <= 0) {
            ses.setAttribute("ApiDocGuardFormSecret", UUID.randomUUID().toString());
            debug("[FORM SESSION CREATE] ApiDocGuardFormSecret: " + ses.getAttribute("ApiDocGuardFormSecret"), "info");
        } else {
            debug("[FORM SESSION EXISTS] ApiDocGuardFormSecret: " + ses.getAttribute("ApiDocGuardFormSecret"), "info");

            if (user != null) {
                String userSession = "{\"username\": \"" + user + "\", \"id\": \"" + ses.getAttribute("ApiDocGuardFormSecret") + "\"}";
                ses.setAttribute("ApiDocGuardUser", userSession);
                debug("[USER SESSION CREATE] ApiDocGuardUser: " + ses.getAttribute("ApiDocGuardUser"), "info");
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

    public boolean login(HttpServletRequest req, HttpServletResponse res, Map<String, String> body) {

        try {

            String username = body.get("username");
            String password = body.get("password");

            if (username.equals("") || password.equals("")) {
                debug("MISSING DATA TO LOGIN", "error");
                return false;
            }

            String passwordCrypt = crypt(password);
            ApiDocGuardEntity apiDocGuardEntity = apiDocGuardRepository.findByLogin(username, passwordCrypt);

            if (apiDocGuardEntity != null) {
                debug("LOGIN SUCCESS: " + body.get("username"), "info");
                return true;
            }

        } catch (RuntimeException re) {
            debug("LOGIN FAIL: " + re.getMessage(), "except");
        }

        return false;

    }
    
}
