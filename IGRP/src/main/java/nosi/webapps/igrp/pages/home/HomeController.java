package nosi.webapps.igrp.pages.home;
/*---- Import your packages here... ----*/

import java.io.IOException;

import org.apache.xml.security.stax.ext.XMLSecurityConstants.Action;

import nosi.core.exception.ServerErrorHttpException;
import nosi.core.webapp.Controller;
import nosi.core.webapp.Core;
import nosi.core.webapp.Igrp;
import nosi.core.webapp.Response;
import nosi.core.webapp.helpers.Permission;

/*---- End ----*/
public class HomeController extends Controller {		

	public Response actionIndex() throws IOException{
		
		System.out.println("dad: "+Core.getParam("dad"));
		String dad=Core.getParam("dad");
		if(Core.isNotNull(dad) && !dad.equals("igrp")) {
			nosi.webapps.igrp.dao.Action ac = Core.findApplicationByDad(dad).getAction();
			String page = "tutorial/DefaultPage/index&title=";
			page = (ac != null && ac.getPage() != null) ? ac.getPage() : page;
			page = ac.getApplication().getDad().toLowerCase() + "/" + page;
			this.addQueryString("app", dad);
			this.addQueryString("page", page+"/index&title="+ac.getAction_descr());
			return redirect("igrp_studio", "env", "openApp",this.queryString());
		}		
		
		String destination = Igrp.getInstance().getRequest().getParameter("_url");
		if(destination != null ) {
			try {
				String []aux = destination.split("/");
				if(aux.length != 3)
					throw new ServerErrorHttpException();
				new Permission().changeOrgAndProfile(aux[0]);
			return redirect(aux[0], aux[1], aux[2]);
			}catch(Exception e) {
				
			}
		}
		
		try { // Eliminar 
			new Permission().changeOrgAndProfile("igrp");
		}catch(Exception e) {
			
		}
		
		HomeView view = new HomeView();
		view.title = "Home";
		return this.renderView(view,true);
	}
}
