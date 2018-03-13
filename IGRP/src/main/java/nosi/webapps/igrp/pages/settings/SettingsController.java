
package nosi.webapps.igrp.pages.settings;
/*----#START-PRESERVED-AREA(PACKAGES_IMPORT)----*/
import nosi.core.i18n.I18nManager;
import nosi.core.webapp.Controller;
import nosi.core.webapp.FlashMessage;
import nosi.core.webapp.Igrp;
import nosi.core.webapp.Response;
import nosi.core.webapp.helpers.Permission;
import java.io.IOException;
import java.util.HashMap;
import nosi.core.webapp.Core;
import javax.servlet.http.Cookie;
import nosi.webapps.igrp.dao.Organization;
import nosi.webapps.igrp.dao.ProfileType;
import nosi.webapps.igrp.dao.User;
import static nosi.core.i18n.Translator.gt;
/*----#END-PRESERVED-AREA----*/

public class SettingsController extends Controller {		


	public Response actionIndex() throws IOException, IllegalArgumentException, IllegalAccessException{
		/*----#START-PRESERVED-AREA(INDEX)----*/
		Settings model = new Settings();
		String ichange = Igrp.getInstance().getRequest().getParameter("ichange");
		model.load();

		
		if (Igrp.getInstance().getRequest().getMethod().toUpperCase().equals("POST")) {
			if (Core.isNotNull(ichange)) {
				if(Core.isNotNull(model.getPerfil())){
				// String data = model.getOrganica()+"-"+model.getPerfil();
				String data = new ProfileType().findOne(model.getPerfil()).getOrganization().getId()+"-" + model.getPerfil();
				Igrp.getInstance().getResponse().addCookie(new Cookie(Permission.getCurrentEnv(), data));
				}
			if (Core.isNotNull(model.getIdioma())) {
				Igrp.getInstance().getI18nManager().newIgrpCoreLanguage(model.getIdioma());
				Cookie cookie = new Cookie("igrp_lang", model.getIdioma());
				cookie.setMaxAge(I18nManager.cookieExpire);
				Igrp.getInstance().getResponse().addCookie(cookie);				
				Core.setMessageSuccess("Dados gravados com sucesso!");				
				}
			}
		}else
		// Fetch all cookies
		for (Cookie cookie : Igrp.getInstance().getRequest().getCookies()) {
			if (cookie.getName().equals("igrp_lang")) {
				model.setIdioma(cookie.getValue());
			}
			if (cookie.getName().equals(Core.getCurrentDad())) {
				try {
					String[] aux = cookie.getValue().split("-");
					// model.setOrganica(aux[0]);
					model.setPerfil(aux[1]);
				} catch (Exception e) {
					// Do nothing
				}
			}
		}
		if (Core.isNull(model.getPerfil()))
			model.setPerfil(Permission.getCurrentPerfilId() + "");
		// if(Core.isNotNull(model.getOrganica()))
		// model.setOrganica(Permission.getCurrentOrganization() + "");

		User user = (User) Igrp.getInstance().getUser().getIdentity();

		SettingsView view = new SettingsView(model);

		view.btn_alterar_senha.setLink("igrp", "ChangePassword", "index&target=_blank");

		view.nome.setValue(user.getName());
		view.email.setValue(user.getEmail());
		view.username.setValue(user.getUser_name());
		view.sectionheader_1_text.setValue(gt("Área Pessoal") + ": " + user.getName());
		view.telefone.setValue(user.getPhone());
		view.telemovel.setValue(user.getMobile());
		view.password_expira_em.setValue(user.getValid_until());

		// HashMap<String,String> organizations = new
		// Organization().getListMyOrganizations();
		// view.organica.setValue(organizations);

		HashMap<String, String> profiles = new ProfileType().getListMyProfiles();
		view.perfil.setValue(profiles);

		HashMap<String, String> idioma = new HashMap<String, String>();
		idioma.put("", gt("-- Selecionar --"));
		idioma.put("pt_PT", gt("Português"));
		idioma.put("en_US", gt("Inglês"));
		idioma.put("fr_FR", gt("Francês"));
		idioma.put("es_ES", gt("Espanhol"));
		view.idioma.setValue(idioma);

		return this.renderView(view);
		/*----#END-PRESERVED-AREA----*/
	}


	public Response actionAlterar_senha() throws IOException, IllegalArgumentException, IllegalAccessException{
		/*----#START-PRESERVED-AREA(ALTERAR_SENHA)----*/
		Settings model = new Settings();
		if (Igrp.getMethod().equalsIgnoreCase("post")) {
			model.load();
			if (model.save(model)) {
				Core.setMessageSuccess();
			} else {
				Core.setMessageError();
				return this.forward("igrp", "ChangePassword", "index");
			}
		}
		return this.redirect("igrp", "ChangePassword", "index");
		/*----#END-PRESERVED-AREA----*/
	}
	
	/*----#START-PRESERVED-AREA(CUSTOM_ACTIONS)----*/

	/*----#END-PRESERVED-AREA----*/
}
