/**
 *
 */
package org.simon.pascal.controller;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.simon.pascal.helper.PrismicConfig;
import org.simon.pascal.helper.PrismicContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author nsimonin1
 *
 */
@Controller
public class PrismicOAuthController {

  @Autowired
  private PrismicConfig config;

  @Autowired
  private PrismicContext prismic;

  @GetMapping("/signin")
  public String signin(HttpServletRequest request) throws Exception {
    final StringBuilder url = new StringBuilder();
    url.append(prismic.getApi().getOAuthInitiateEndpoint());
    url.append("?");
    url.append("client_id=");
    url.append(URLEncoder.encode(config.getClientId(), "utf-8"));
    url.append("&redirect_uri=");
    url.append(URLEncoder.encode(host(request) + "/auth_callback", "utf-8"));
    url.append("&scope=");
    url.append(URLEncoder.encode("master+releases", "utf-8"));
    return "redirect:" + url;
  }

  @GetMapping("/auth_callback")
  public String callback(@RequestParam(value="code", required=true) String code, HttpSession session, HttpServletRequest request) throws Exception {
    final StringBuilder body = new StringBuilder();
    body.append("grant_type=");
    body.append(URLEncoder.encode("authorization_code", "utf-8"));
    body.append("&code=");
    body.append(URLEncoder.encode(code, "utf-8"));
    body.append("&redirect_uri=");
    body.append(URLEncoder.encode(host(request) + "/auth_callback", "utf-8"));
    body.append("&client_id=");
    body.append(URLEncoder.encode(config.getClientId(), "utf-8"));
    body.append("&client_secret=");
    body.append(URLEncoder.encode(config.getClientSecret(), "utf-8"));

    final HttpURLConnection connection = (HttpURLConnection)new URL(prismic.getApi().getOAuthTokenEndpoint()).openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    connection.setDoOutput(true);
    connection.getOutputStream().write(body.toString().getBytes("utf-8"));
    connection.getOutputStream().flush();
    connection.getOutputStream().close();

    final String accessToken = new ObjectMapper().readTree(connection.getInputStream()).path("access_token").asText();
    session.setAttribute(PrismicContext.ACCESS_TOKEN, accessToken);

    return "redirect:/";
  }

  @PostMapping("/signout")
  public String signout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
  }

  // --

  public String host(HttpServletRequest request) {
    return "http://" + request.getServerName() + ":" + request.getServerPort() + "" + request.getContextPath();
  }

}
