package nosi.webapps.igrp.pages.login;

import static nosi.core.i18n.Translator.gt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.carbon.um.ws.service.RemoteUserStoreManagerService;
import org.wso2.carbon.um.ws.service.dao.xsd.ClaimDTO;
import nosi.core.config.Config;
import nosi.core.ldap.LdapPerson;
import nosi.core.webapp.Controller;
import nosi.core.webapp.Core;
import nosi.core.webapp.FlashMessage;
import nosi.core.webapp.Igrp;
import nosi.core.webapp.Response;
import nosi.core.webapp.helpers.Route;
import nosi.core.webapp.security.Permission;
import nosi.webapps.igrp.dao.Organization;
import nosi.webapps.igrp.dao.Profile;
import nosi.webapps.igrp.dao.ProfileType;
import nosi.webapps.igrp.dao.Session;
import nosi.webapps.igrp.dao.User;
import service.client.WSO2UserStub;
/**
 * Marcel Iekiny Oct 4, 2017 
 */
/*----#start-code(packages_import)----*/

/*----#end-code----*/

public class LoginController extends Controller {

	/*----#start-code(custom_actions)----*/

	private Properties settings = loadConfig("common", "main.xml");

	public Response actionLogin() throws Exception { 
		
		Response r = createResponseForRetrieveAccount(); 
		if(r != null) return r; 
		
		Login model = new Login();
		LoginView view = new LoginView(model);
		
		
		r = createResponseIfIsAuthenticated();
		if(r != null) return r; 
		
		
		r = createResponseIfNotAuthenticated_nhaLogin(); 
		if(r != null) return r; 
		
		
		r = oAuth2Wso2(); 
		if(r != null) return r; 
		
		r = createResponseApplyingActivation(); 
		if(r != null) return r;  
		
		
		r = createResponseOauth2OpenIdWso2(); 
		if(r != null) return r; 
		
		
		if(Igrp.getInstance().getRequest().getMethod().equalsIgnoreCase("POST")) { 
			
			model.load(); 
			
			r = mainAuthentication(model.getUser(), model.getPassword()); 
			if(r != null) return r; 
			
			return redirect("igrp", "login", "login", this.queryString()); 
		} 
		
		String aux = settings.getProperty("igrp.authentication.govcv.enbaled");
		boolean isDb = this.getConfig().getAutenticationType().equals("db");
		if ((aux != null && !aux.isEmpty() && aux.equals("true")) || isDb) {
			view.user.setLabel("Username");
			view.user.propertie().setProperty("type", "text");
		}
		
		return this.renderView(view, true);
	}

	public Response actionLogout() throws IOException { 
		String currentSessionId = Igrp.getInstance().getRequest().getRequestedSessionId(); 
		
		User user = Core.getCurrentUser(); 
		String oidcIdToken = user.getOidcIdToken(); 
		String oidcState = user.getOidcState();
		
		user.setIsAuthenticated(0); 
		user = user.update();
		if (Igrp.getInstance().getUser().logout() && user != null && !user.hasError()) {
			if (!Session.afterLogout(currentSessionId))
				Igrp.getInstance().getFlashMessage().addMessage(FlashMessage.ERROR,
						gt("Ooops !!! Ocorreu um erro com registo session ..."));
		} else
			Igrp.getInstance().getFlashMessage().addMessage(FlashMessage.ERROR, gt("Ocorreu um erro no logout.")); 
		
		
		
		if (settings.getProperty("igrp.env.isNhaLogin") != null
				&& !settings.getProperty("igrp.env.isNhaLogin").equals("true")
				&& settings.getProperty("igrp.env.nhaLogin.url") != null
				&& !settings.getProperty("igrp.env.nhaLogin.url").isEmpty()) {
			String _url = settings.getProperty("igrp.env.nhaLogin.url").replace("igrp/login/login", "igrp/login/logout");
			return redirectToUrl(_url);
		}
		
		// Clear the cookies 
		for (Cookie c : Igrp.getInstance().getRequest().getCookies()) {
			if (c.getName().equals("igrp_lang"))
				continue;
			c.setMaxAge(0);
			c.setValue(null);
			Igrp.getInstance().getResponse().addCookie(c);
		}
		
		String r = settings.getProperty("ids.wso2.oauth2-openid.enabled"); 
		if(r != null && r.equalsIgnoreCase("true")) {
			String oidcLogout = settings.getProperty("ids.wso2.oauth2.endpoint.logout"); 
			if(oidcLogout != null && !oidcLogout.isEmpty()) {
				String aux = oidcLogout + "?id_token_hint=" + oidcIdToken + "&state=" + oidcState; 
				String redirect_uri = settings.getProperty("ids.wso2.oauth2.endpoint.redirect_uri"); 
				aux = redirect_uri != null && !redirect_uri.isEmpty() ? aux + "&post_logout_redirect_uri=" + redirect_uri : aux;
				return redirectToUrl(aux); 
			}
			return redirectToUrl(createUrlForOAuth2OpenIdRequest()); 
		}
		
		return this.redirect("igrp", "login", "login");
	}

	// Dont delete this method
	public Response actionGoToLogin() throws IOException {
		return this.redirect("igrp", "login", "login");
	}

	

	private Properties loadConfig(String filePath, String fileName) {
		String path = new Config().getBasePathConfig() + File.separator + filePath;
		File file = new File(getClass().getClassLoader().getResource(path + File.separator + fileName).getPath()
				.replaceAll("%20", " "));

		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(file)) {
			props.loadFromXML(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
	
	private Response createResponseIfIsAuthenticated() {
		if (Igrp.getInstance().getUser().isAuthenticated()) {
			
			User u = Core.getCurrentUser();
			if(u.getIsAuthenticated() == 0) { 
				try {
					return redirect("igrp", "login", "logout");
				} catch (IOException e) {
				}
			}

			if (settings.getProperty("igrp.env.isNhaLogin") != null
					&& settings.getProperty("igrp.env.isNhaLogin").equals("true")) {
				String url = Igrp.getInstance().getRequest().getRequestURL().toString();
				url = url.replace("app/webapps", "mylinks.jsp");
				return redirectToUrl(url);
			}

			String destination = Route.previous();
			if (destination != null) {
				String qs = URI.create(destination).getQuery();
				qs = qs.substring(qs.indexOf("r=") + "r=".length());
				String param[] = qs.split("/");
				new Permission().changeOrgAndProfile(param[0]);
				return this.redirectToUrl(destination);
			}
			try {
				return this.redirect("igrp", "home", "index");
			}catch (Exception e) {
			}
		}
		return null;
	}
	
	
	public Response createResponseApplyingActivation() {
		// Activation key 
		String activation_key = Igrp.getInstance().getRequest().getParameter("activation_key");
		if (activation_key != null && !activation_key.trim().isEmpty()) {
			try {
				User user = new User().find().andWhere("activation_key", "=", activation_key).one();
				activation_key = new String(Base64.getUrlDecoder().decode(activation_key));
				if (user != null && activation_key.compareTo(System.currentTimeMillis() + "") > 0
						&& user.getStatus() == 0) {
					user.setStatus(1);
					user = user.update();
					Core.setMessageSuccess("Ativação bem sucedida. Faça o login !!!");
				} else {
					Core.setMessageError("Ooops !!! Ocorreu um erro na activação.");
				}
			} catch (Exception e) {
				Core.setMessageError("Ooops !!! Ocorreu um erro na activação.");
			}
			try { 
				String oidc = settings.getProperty("ids.wso2.oauth2-openid.enabled"); 
				if(oidc != null && oidc.equalsIgnoreCase("true")) 
					return redirectToUrl(createUrlForOAuth2OpenIdRequest());
				
				return redirect("igrp", "login", "login", this.queryString());
			} catch (Exception e) {
			}
		}
		
		return null;
	}
	
	private Response createResponseIfNotAuthenticated_nhaLogin() {
		if (!Igrp.getInstance().getUser().isAuthenticated() && settings.getProperty("igrp.env.isNhaLogin") != null
				&& !settings.getProperty("igrp.env.isNhaLogin").equals("true")
				&& settings.getProperty("igrp.env.nhaLogin.url") != null
				&& !settings.getProperty("igrp.env.nhaLogin.url").isEmpty()) {
			return redirectToUrl(settings.getProperty("igrp.env.nhaLogin.url"));
		}
		return null;
	}
	
	private Response mainAuthentication(String username, String password) {
		switch (this.getConfig().getAutenticationType()) {
			case "db": 
				if (loginWithDb(username, password)) { 
					String destination = Route.previous(); 
					if (destination != null) {
						String qs = URI.create(destination).getQuery();
						qs = qs.substring(qs.indexOf("r=") + "r=".length());
						String param[] = qs.split("/");
						new Permission().changeOrgAndProfile(param[0]);
						
						return this.redirectToUrl(destination);
					}
					try {
						return this.redirect("igrp", "home", "index"); // By default go to home index url 
					}catch (Exception e) {
					}
			}
			break; 
			case "ldap": 
				if (this.loginWithLdap(username, password)) {
					if (settings.getProperty("igrp.env.isNhaLogin") != null
							&& settings.getProperty("igrp.env.isNhaLogin").equals("true")) {
						return checkEnvironments_nhaLogin(username);
					}
					// TODO by Marcos: must decrypt de URL when you do Route.remenber()
					String destination = Route.previous(); 
					if (destination != null) {
						String qs = URI.create(destination).getQuery();
						qs.indexOf("r=");
						qs = qs.substring(qs.indexOf("r=") + "r=".length());
						String param[] = qs.split("/");
						new Permission().changeOrgAndProfile(param[0]);
						return this.redirectToUrl(destination);
					}
					try {
						return this.redirect("igrp", "home", "index"); // By default go to home index url 
					}catch (Exception e) {
					}
				}
			break; 
		}
		
		return null; 
	}
	
	private boolean loginWithDb(String username, String password) { 
		boolean success = false;
		User user = (User) new User().findIdentityByUsername(username);
		if (user != null && user.validate(nosi.core.webapp.User.encryptToHash(username + "" + password, "SHA-256")) && userIsAuthenticatedFlag(user)) {
			if (user.getStatus() == 1) {
				Profile profile = new Profile().getByUser(user.getId());
				if (profile != null && Igrp.getInstance().getUser().login(user, 60 * 60/* 1h */)) { // 3600 * 24 * 30
					if (!Session.afterLogin(profile))
						Core.setMessageError("Ooops !!! Error no registo session ...");
					// String backUrl = Route.previous(); // remember the last url that was 
					success = true;
				} else
					Core.setMessageError("Ooops !!! Ocorreu um INTERNAL_ERROR ... Login inválido.");
			} else
				Core.setMessageError("Utilizador desativado. Por favor contacte o Administrador.");
		} else 
			Core.setMessageError("A sua conta ou palavra-passe está incorreta. Se não se lembra da sua palavra-passe, contacte o Administrador.");
			
		return success;
	}
	
	private Response checkEnvironments_nhaLogin(String uid) {
		try {
			User user = new User().find().andWhere("user_name", "=", uid).one();
			String token = Base64.getEncoder()
					.encodeToString((user.getUser_name() + ":" + user.getValid_until()).getBytes());
			URL url = new URL(settings.getProperty("ids.wso2.RemoteUserStoreManagerService-wsdl-url"));
			WSO2UserStub.disableSSL();
			WSO2UserStub stub = new WSO2UserStub(new RemoteUserStoreManagerService(url));
			stub.applyHttpBasicAuthentication(settings.getProperty("ids.wso2.admin-usn"),
					settings.getProperty("ids.wso2.admin-pwd"), 2);

			List<String> roles = stub.getOperations().getRoleListOfUser(uid);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("token", token);
			JSONArray jsonArray = new JSONArray();
			jsonObject.put("myLinks", jsonArray);

			roles.forEach(obj -> {
				jsonArray.put(obj);
			});

			Igrp.getInstance().getRequest().getSession().setAttribute("__links", jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		String url = Igrp.getInstance().getRequest().getRequestURL().toString();
		url = url.replace("app/webapps", "mylinks.jsp");

		return redirectToUrl(url);
	}
	
	
	
	
	private boolean loginWithLdap(String username, String password) {
		
		boolean success = false;
		ArrayList<LdapPerson> personArray = new ArrayList<LdapPerson>();

		if (settings.getProperty("ids.wso2.enabled") != null && settings.getProperty("ids.wso2.enabled").equalsIgnoreCase("true")) {
			success = authenticateThroughWso2(username, password, personArray);
		} 

		if (success) {
			// Verify if this credentials exist in DB
			User user = (User) new User().findIdentityByUsername(username);
			if (user != null) {
				/*
				 * password = nosi.core.webapp.User.encryptToHash(password, "SHA-256");
				 * if((user.getPass_hash() == null) || (user.getPass_hash() != null &&
				 * !user.getPass_hash().equals(password))) { user.setPass_hash(password); //
				 * Anyway !!! update the user's password and encrypt it ... user.update(); }
				 */
				/** Begin create user session **/ 
				
				success = createSessionLdapAuthentication(user) && userIsAuthenticatedFlag(user); 
				
				sso(username, password, user); 

				/** End create user session **/ 

			} else {
				if (this.getConfig().getEnvironment().equals("dev")
						|| (settings.getProperty("igrp.env.isNhaLogin") != null
								&& settings.getProperty("igrp.env.isNhaLogin").equals("true"))) { // Active Directory
																									// Ldap Server ...
																									// autoinvite the
																									// user for
																									// IgrpStudio 
					User newUser = new User();
					newUser.setUser_name(username.trim().toLowerCase());

					if (personArray != null && personArray.size() > 0)
						for (int i = 0; i < personArray.size(); i++) {
							LdapPerson p = personArray.get(i);

							if (p.getName() != null && !p.getName().isEmpty())
								newUser.setName(p.getName());
							else if (p.getDisplayName() != null && !p.getDisplayName().isEmpty())
								newUser.setName(p.getDisplayName());
							else
								newUser.setName(p.getFullName());

							newUser.setEmail(p.getMail().toLowerCase());
						}

					newUser.setStatus(1);
					newUser.setCreated_at(System.currentTimeMillis());
					newUser.setUpdated_at(System.currentTimeMillis());
					newUser.setAuth_key(nosi.core.webapp.User.generateAuthenticationKey());
					newUser.setActivation_key(nosi.core.webapp.User.generateActivationKey());
					newUser.setIsAuthenticated(1);

					newUser = newUser.insert();

					if (newUser != null) {

						sso(username, password, newUser);

						if(createPerfilWhenAutoInvite(newUser))
							return createSessionLdapAuthentication(newUser);
						
					}

				} else {
					success = false;
					Core.setMessageError(gt("Esta conta não tem acesso ao IGRP. Por favor, contacte o Administrador."));
				}
			}
		} else 
			Core.setMessageError(gt("A sua conta ou palavra-passe está incorreta."));

		return success;
	}
	
	private boolean authenticateThroughWso2(String username, String password, List<LdapPerson> personArray) {
		boolean flag = false;
			try {
				URL url = new URL(settings.getProperty("ids.wso2.RemoteUserStoreManagerService-wsdl-url"));
				WSO2UserStub.disableSSL();
				WSO2UserStub stub = new WSO2UserStub(new RemoteUserStoreManagerService(url));
				stub.applyHttpBasicAuthentication(settings.getProperty("ids.wso2.admin-usn"),
						settings.getProperty("ids.wso2.admin-pwd"), 2);
				
				flag = stub.getOperations().authenticate(username, password);

				String v = settings.getProperty("igrp.authentication.govcv.enbaled");

				if (v.equalsIgnoreCase("true"))
					username = "gov.cv/" + username;

				// Pesquisar user from Ids
				List<ClaimDTO> result = stub.getOperations().getUserClaimValues(username, "");
				LdapPerson ldapPerson = new LdapPerson();
				result.forEach(obj -> {
					switch (obj.getClaimUri().getValue()) {
					case "http://wso2.org/claims/displayName":
						ldapPerson.setDisplayName(obj.getValue().getValue());
						break;
					case "http://wso2.org/claims/givenname":
						ldapPerson.setGivenName(obj.getValue().getValue());
						break;
					case "http://wso2.org/claims/emailaddress":
						ldapPerson.setUid(obj.getValue().getValue());
						ldapPerson.setMail(obj.getValue().getValue());
						break;
					case "http://wso2.org/claims/fullname":
						ldapPerson.setFullName(obj.getValue().getValue());
						break;
					case "http://wso2.org/claims/lastname":
						ldapPerson.setLastName(obj.getValue().getValue());
						break;

					}
				});
				personArray.add(ldapPerson);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		return flag;
	}
	
	
	private boolean sso(String username, String password, User dao) {
		// boolean flag = true;
		String client_id = settings.getProperty("ids.wso2.oauth2.client_id");
		String client_secret = settings.getProperty("ids.wso2.oauth2.client_secret");
		String endpoint = settings.getProperty("ids.wso2.oauth2.endpoint.token");
		String postData = "grant_type=password" + "&username=" + username + "&password=" + password + "&client_id="
				+ client_id + "&client_secret=" + client_secret + "&scope=openid";
		try {

			HttpURLConnection curl = (HttpURLConnection) URI.create(endpoint).toURL().openConnection();
			curl.setDoOutput(true);
			curl.setDoInput(true);
			curl.setInstanceFollowRedirects(false);
			curl.setRequestMethod("POST");
			curl.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			curl.setRequestProperty("charset", "utf-8");
			curl.setRequestProperty("Content-Length", (postData.length()) + "");
			curl.setUseCaches(false);                           
			curl.getOutputStream().write(postData.getBytes());

			curl.connect(); 

			int code = curl.getResponseCode();

			if (code != 200) {
				return false;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(curl.getInputStream(), "UTF-8"));

			String result = "";
			String token = "";

			result = br.lines().collect(Collectors.joining());

			JSONObject jToken = new JSONObject(result);

			token = (String) jToken.get("access_token");

			dao.setValid_until(token);
			dao = dao.update();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean createSessionLdapAuthentication(User user) {
		boolean result = true;
		if (user.getStatus() == 1) {
			Profile profile = new Profile().getByUser(user.getId());
			
			if (profile != null && Igrp.getInstance().getUser().login(user, 3600 * 24 * 30)) {
				if (!Session.afterLogin(profile)) {
					result = false;
					Core.setMessageError(gt("Ooops !!! Error no registo session. "));
					// String backUrl = Route.previous(); // remember the last url that was 
					// requested by the user
				}
			} else {
				result = false;
				Core.setMessageError(gt("Ooops !!! Login inválido. "));
			}
		} else {
			result = false;
			Core.setMessageError("Utilizador desativado. Por favor contacte o Administrador.");
		}
		return result;
	}
	
	
	private Response createResponseForRetrieveAccount() {
		// Go to password recover page ... 
		if(Igrp.getInstance().getRequest().getMethod().equalsIgnoreCase("POST")) {
			String p_button2 = Igrp.getInstance().getRequest().getParameter("p_button2");
			if (p_button2 != null && p_button2.equals("p_button2")) {
				String url = Igrp.getInstance().getRequest().getRequestURL().toString()
						+ "?r=igrp/Resetbyemail/index&target=_blank&isPublic=1";
				return redirectToUrl(url);
			}
		}
		
		return null;
	}
	
	private Map<String, String> oAuth2Wso2Swap() {
		
		try {
			String authCode = Core.getParam("code"); 
			String session_state = Core.getParam("session_state"); 
			
			if(authCode == null || authCode.isEmpty()) return null; 
			
			String client_id = settings.getProperty("ids.wso2.oauth2.client_id");
			String client_secret = settings.getProperty("ids.wso2.oauth2.client_secret");
			String endpoint = settings.getProperty("ids.wso2.oauth2.endpoint.token");
			String redirect_uri = settings.getProperty("ids.wso2.oauth2.endpoint.redirect_uri");
			
			Form postData = new Form(); 
			postData.param("grant_type", "authorization_code"); 
			postData.param("code", authCode); 
			postData.param("redirect_uri", redirect_uri); 
			postData.param("scope", "openid email profile");
			
			Client curl = ClientBuilder.newClient();
			Invocation.Builder ib = curl.target(endpoint).request("application/x-www-form-urlencoded");
			ib.header("Accept", "application/json");
			ib.header("Authorization",  "Basic " + Base64.getEncoder().encodeToString((client_id + ":" + client_secret).getBytes()));
			javax.ws.rs.core.Response r = ib.post(Entity.form(postData), javax.ws.rs.core.Response.class);
			
			
			String resultPost = r.readEntity(String.class); 
			
			curl.close();
			
			int code = r.getStatus(); 
			
			if (code != 200) return null;

			JSONObject jToken = new JSONObject(resultPost);

			String token = (String) jToken.get("access_token");
			String id_token = (String) jToken.get("id_token");
			
			Map<String, String> m = new HashMap<String, String>(); 
			m.put("token", token);
			m.put("id_token", id_token);
			m.put("session_state", session_state);
			
			return m;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Map<String, String> oAuth2Wso2GetUserInfoByToken(String token) {
		Map<String, String> uid = null;
		try {
			
			String endpoint = settings.getProperty("ids.wso2.oauth2.endpoint.user");
			
			Client curl = ClientBuilder.newClient();
			javax.ws.rs.core.Response r = curl.target(endpoint)
											.request()
											.header("Accept", "application/json")
											.header("Authorization", "Bearer " + token) 
											.get(javax.ws.rs.core.Response.class);  
			
			int code = r.getStatus();
			
			if(code != 200) return uid; 
			
			String result = r.readEntity(String.class); 
			
			curl.close();

			JSONObject jToken = new JSONObject(result); 
			
			uid = new HashMap<String, String>();
			
			uid.put("sub", jToken.getString("sub")); 
			uid.put("email", jToken.getString("email")); 
			
			System.out.println("jToken: " + jToken); 

		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		return uid;
	}
	
	private Response oAuth2Wso2() {
		
		String error = Core.getParam("error"); 
		String r = settings.getProperty("ids.wso2.oauth2-openid.enabled"); 
		String authCode = Core.getParam("code"); 
		
		
		if(r != null && r.equalsIgnoreCase("true")) {
			
			if(error != null && !error.isEmpty() && !error.equalsIgnoreCase("null")) {
				Core.setMessageError("Ocorreu o seguinte erro: (" + error + ").");
				return redirectToUrl(createUrlForOAuth2OpenIdRequest());
			}
			
			String token = null;
			String id_token = null;
			String session_state = null;
			
			Map<String, String> m = oAuth2Wso2Swap();
			if(m != null) {
				token = m.get("token");
				id_token = m.get("id_token");
				session_state = m.get("session_state");
			}
			
			
			if(token != null) {
				
				Map<String, String> _r = oAuth2Wso2GetUserInfoByToken(token);
				
				if(_r != null && _r.containsKey("email") && _r.containsKey("sub")) {
					
					String email = _r.get("email") != null ? _r.get("email").trim().toLowerCase() : _r.get("email"); 
					String uid = _r.get("sub"); 
					
					User user = new User().find().andWhere("email", "=", email).one(); 
					
					if (user != null) {
						
						if(createSessionLdapAuthentication(user)) {
							try {
								user.setValid_until(token);
								user.setOidcIdToken(id_token);
								user.setOidcState(session_state);
								user.setIsAuthenticated(1);
								user = user.update();
								return redirect("igrp", "home", "index"); 
							} catch (Exception e) {
							}
						}
						
					}else {
						
						// Caso o utilizador não existir na base de dados fazer auto-invite no quando env=dev ... 
						if(new Config().getEnvironment().equalsIgnoreCase("dev")) {
							
							try {
								User newUser = new User();
								newUser.setUser_name(uid);
								newUser.setEmail(email); 
								newUser.setName(uid);
								newUser.setStatus(1);
								newUser.setIsAuthenticated(1);
								newUser.setCreated_at(System.currentTimeMillis());
								newUser.setUpdated_at(System.currentTimeMillis());
								newUser.setAuth_key(nosi.core.webapp.User.generateAuthenticationKey());
								newUser.setActivation_key(nosi.core.webapp.User.generateActivationKey());
			
								newUser = newUser.insert(); 
								
								if(newUser != null && createPerfilWhenAutoInvite(newUser) && createSessionLdapAuthentication(newUser)) { 
									newUser.setValid_until(token);
									newUser.setOidcIdToken(id_token);
									newUser.setOidcState(session_state);
									newUser.update();
									return redirect("igrp", "home", "index"); 
								}
							} catch (Exception e) {
								e.printStackTrace();
								Core.setMessageError("Ocorreu um erro no auto-invite.");
								return redirectToUrl(createUrlForOAuth2OpenIdRequest());
							}
						}else {
							Core.setMessageWarning("Utilizador não convidado nesse ambiente."); 
							return redirectToUrl(createUrlForOAuth2OpenIdRequest());
						}
						
					}
					
				}else {
						if(authCode != null && !authCode.trim().isEmpty()) {
							Core.setMessageError("Ocorreu o seguinte erro: (Uid não encontrado).");
						return redirectToUrl(createUrlForOAuth2OpenIdRequest());
					}
				}
			}else {
				if(authCode != null && !authCode.trim().isEmpty()) {
					Core.setMessageError("Ocorreu o seguinte erro: (Token não encontrado).");
					return redirectToUrl(createUrlForOAuth2OpenIdRequest());
				}
			}
			
			if((error == null || error.isEmpty()) && 
					(authCode == null || authCode.isEmpty()) && 
					(r != null && r.equalsIgnoreCase("true"))) {
				return createResponseOauth2OpenIdWso2();
			}
			
		}
		
		return null;
	}
	
	private Response createResponseOauth2OpenIdWso2() {
		String r = settings.getProperty("ids.wso2.oauth2-openid.enabled"); 
		String url = settings.getProperty("ids.wso2.oauth2.endpoint.authorize"); 
		if(r != null && r.equalsIgnoreCase("true") && url != null && !url.isEmpty()) {
			String redirect_uri = settings.getProperty("ids.wso2.oauth2.endpoint.redirect_uri"); 
			String client_id = settings.getProperty("ids.wso2.oauth2.client_id"); 
			url += "?response_type=code&client_id=" + client_id + "&scope=openid+email+profile&state=TWILIGHT10&redirect_uri=" + redirect_uri;

			return redirectToUrl(url); 
			
		}
		return null; 
	}
	
	private String createUrlForOAuth2OpenIdRequest() { 
		String aux = Igrp.getInstance().getRequest().getRequestURL().toString();
		aux += "?r=igrp/Oauth2openidwso2/index&target=_blank&isPublic=1&lang=pt_PT"; 
		return aux; 
	}
	
	private boolean createPerfilWhenAutoInvite(User user) { 
		Profile p1 = new Profile(); 
		p1.setUser(user);
		p1.setOrganization(new Organization().findOne(3));
		p1.setProfileType(new ProfileType().findOne(4));
		p1.setType("PROF");
		p1.setType_fk(4);

		Profile p2 = new Profile();
		p2.setUser(user);
		p2.setOrganization(new Organization().findOne(3));
		p2.setProfileType(new ProfileType().findOne(4));
		p2.setType("ENV");
		p2.setType_fk(3);

		Profile tutorialApp = new Profile();
		tutorialApp.setUser(user);
		tutorialApp.setOrganization(new Organization().findOne(2));
		tutorialApp.setProfileType(new ProfileType().findOne(3));
		tutorialApp.setType("ENV");
		tutorialApp.setType_fk(2);

		return p1.insert() != null && p2.insert() != null && tutorialApp.insert() != null;
	}
	
	private boolean userIsAuthenticatedFlag(User u) {
		u.setIsAuthenticated(1);
		u = u.update();
		return u != null && !u.hasError();
	}

	/*----#end-code----*/
}
