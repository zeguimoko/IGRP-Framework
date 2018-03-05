/*-------------------------*/

/*Create Controller*/

package nosi.webapps.igrp_studio.pages.bpmndesigner;

/*----#START-PRESERVED-AREA(PACKAGES_IMPORT)----*/
import nosi.core.webapp.Controller;
import nosi.core.webapp.Core;
import nosi.core.webapp.FlashMessage;
import nosi.core.webapp.Igrp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import org.apache.commons.text.StringEscapeUtils;
import nosi.core.webapp.Response;
import nosi.core.webapp.activit.rest.DeploymentService;
import nosi.core.webapp.activit.rest.ProcessDefinitionService;
import nosi.core.webapp.activit.rest.ResourceService;
import nosi.core.webapp.helpers.Permission;
import nosi.webapps.igrp.dao.Application;
import nosi.core.config.Config;
/*----#END-PRESERVED-AREA----*/

public class BPMNDesignerController extends Controller {		


	public Response actionIndex() throws IOException, IllegalArgumentException, IllegalAccessException{
		/*----#START-PRESERVED-AREA(INDEX)----*/
		BPMNDesigner model = new BPMNDesigner();
		model.load();
		BPMNDesignerView view = new BPMNDesignerView(model);
		view.env_fk.setValue(new Application().getListApps());
		Application app = new Application().find().andWhere("dad", "=", Permission.getCurrentEnv()).one();		
		List<BPMNDesigner.Gen_table> data = new ArrayList<>();
		for(ProcessDefinitionService process: new ProcessDefinitionService().getProcessDefinitionsAtivos(Core.isNotNull(model.getEnv_fk())?new Integer(model.getEnv_fk()):app.getId())){
			BPMNDesigner.Gen_table processo = new BPMNDesigner.Gen_table();
			processo.setId(process.getId());
			processo.setTitle(process.getName());
			processo.setLink("igrp_studio", "BPMNDesigner", "get-bpmn-design&p_id="+process.getId());
			processo.setId(process.getId());
			data.add(processo);
		}
		view.gen_table.addData(data);
//		Config.LINK_HOME ="webapps?r=igrp_studio/ListaPage/index";
		view.id.setParam(true);
		return this.renderView(view);
		
		/*----#END-PRESERVED-AREA----*/
	}


	public Response actionGravar() throws IOException, ServletException, IllegalArgumentException, IllegalAccessException{
		/*----#START-PRESERVED-AREA(GRAVAR)----*/
		BPMNDesigner model = new BPMNDesigner();
		model.load();
		Part data = Igrp.getInstance().getRequest().getPart("p_data");
		DeploymentService deploy = new DeploymentService();
		Application app = new Application().find().andWhere("dad", "=", Permission.getCurrentEnv()).one();
		deploy = deploy.create(data,Core.isNotNull(model.getEnv_fk())?new Integer(model.getEnv_fk()):app.getId());
		if(deploy!=null && Core.isNotNull(deploy.getId())){
			return this.renderView("<messages><message type=\"success\">" + StringEscapeUtils.escapeXml10(FlashMessage.MESSAGE_SUCCESS) + "</message></messages>");
		}
		return this.renderView("<messages><message type=\"error\">" + StringEscapeUtils.escapeXml10(deploy.hashError()?deploy.getError().getException():"") + "</message></messages>");
		/*----#END-PRESERVED-AREA----*/
	}
	

	public Response actionPublicar() throws IOException{
		/*----#START-PRESERVED-AREA(PUBLICAR)----*/
		return this.redirect("igrp_studio","BPMNDesigner","index");
		/*----#END-PRESERVED-AREA----*/
	}
	

	public Response actionExporar_imagem() throws IOException{
		/*----#START-PRESERVED-AREA(EXPORTAR_IMAGEM)----*/
		return this.redirect("igrp_studio","BPMNDesigner","index");
		/*----#END-PRESERVED-AREA----*/
	}
	
	/*----#START-PRESERVED-AREA(CUSTOM_ACTIONS)----*/
	
	public Response actionGetBpmnDesign() {
		String id = Core.getParam("p_id");
		ProcessDefinitionService process = new ProcessDefinitionService().getProcessDefinition(id);
		String link = process.getResource().replace("/resources/", "/resourcedata/");
		String resource = new ResourceService().getResourceData(link);
		resource = resource.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version='1.0' encoding='UTF-8'?>");
		return this.renderView(resource);
	}
	
	/*----#END-PRESERVED-AREA----*/
}
