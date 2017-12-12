package nosi.core.webapp.import_export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Part;
import javax.xml.transform.TransformerConfigurationException;
import nosi.core.config.Config;
import nosi.core.webapp.helpers.FileHelper;
import nosi.core.webapp.helpers.StringHelper;
import nosi.core.webapp.helpers.ZipUnzipFile;
import nosi.core.xml.XMLTransform;
import nosi.core.xml.XMLWritter;
import nosi.webapps.igrp.dao.Action;
import nosi.webapps.igrp.dao.Application;

/**
 * @author: Emanuel Pereira
 * 5 Nov 2017
 * Importa aplica��es/pag�nas de IGRP PLSQL
 */
public class ImportAppZip extends ImportAppJar{

	private Map<String,FileImportAppOrPage> filesConfigPagePlsql = new HashMap<>();
	List<FileImportAppOrPage> filesToCompile = new ArrayList<>();
	public ImportAppZip(Part fileName){
		super(null);
		this.un_jar_files = ZipUnzipFile.getZipFiles(fileName);
		for(FileImportAppOrPage file:this.un_jar_files){
			if(file.getConteudo()!=null && !file.getConteudo().equals("")  && (file.getNome().startsWith("FTP/IGRP") || file.getNome().startsWith("FTP/app"))){
				String part[] = file.getNome().split("/");
				if(!"navigation.xml".equals(part[part.length-1]) && !"slide-menu.xml".equals(part[part.length-1])) {
					this.filesConfigPagePlsql.put(part[part.length-1], file);
				}
			}
			if(file.getConteudo()!=null && !file.getConteudo().equals("")  && file.getNome().startsWith("SQL/CONFIG")   && file.getNome().endsWith(".json.xml")){
				String part[] = file.getNome().split("/");
				this.filesConfigPagePlsql.put(part[part.length-1], file);
			}
		}
	}

	
	@Override
	public boolean importApp() {
		boolean result = true;		
		for(FileImportAppOrPage file:this.un_jar_files){
			if(file.getNome().startsWith("SQL/CONFIG")  && file.getNome().endsWith("_ENV.xml")){
				this.app = this.saveApp(file);
				if(this.app==null){
					result = false;
					break;
				}
				FileImportAppOrPage f = new FileImportAppOrPage("page/"+this.app.getDad()+"/DefaultPage/index", "", 1);
				this.filesToCompile.add(f);
			}
			if(file.getNome().startsWith("SQL/CONFIG")  && file.getNome().endsWith("_ACTION.xml")){
				List<Action> pages = this.savePagePlsql(file,this.app);
				if(pages.isEmpty()){
					result = false;
					break;
				}else{
					result = this.saveConfigFilesPlsql(pages);
				}
			}
		}
		result = this.compileFiles(this.filesToCompile,this.app);
		return result;
	}

	
	private boolean saveConfigFilesPlsql(List<Action> pages) {
		boolean result = false;
		for(Action page:pages)
			result = this.saveConfigFilesPlsql(page.getSrc_xsl_plsql(), page.getApplication(), page);
		return result;
	}

	private boolean saveConfigFilesPlsql(String fileName, Application app, Action page) {
		boolean result = false;
		if(fileName!=null && !fileName.equals("")) {
			String part[] = fileName.split("/");
			String xsl = StringHelper.removeSpace(part[part.length-1]);
			String xml = xsl.replace(".xsl", ".xml");
			String json = "UI"+page.getId_plsql()+".json.xml";
			if(page.getVersion_src()!=null && page.getVersion_src().equalsIgnoreCase("IGRP2.3")) {
				if(this.filesConfigPagePlsql.get(xsl)!=null){
					String content = this.filesConfigPagePlsql.get(xsl).getConteudo();
					if(content!=null){
						content = content.replaceAll("../../xsl", "../../../xsl");
						FileImportAppOrPage file = new FileImportAppOrPage("configs/"+app.getDad()+"/"+page.getPage()+"/"+page.getAction()+"/"+page.getPage()+".xsl", content, 1);
						result = this.saveFiles(file , app);	
					}
				}
				
				if(this.filesConfigPagePlsql.get(json)!=null){
					String content = this.filesConfigPagePlsql.get(json).getConteudo();
					if(content!=null){
						FileImportAppOrPage file = new FileImportAppOrPage("configs/"+app.getDad()+"/"+page.getPage()+"/"+page.getAction()+"/"+page.getPage()+".json", content, 1);
						result = this.saveFiles(file , app);	
					}
				}
			}
			if(this.filesConfigPagePlsql.get(xml)!=null){
				String content = this.filesConfigPagePlsql.get(xml).getConteudo();
				if(content!=null && page.getVersion_src()!=null && page.getVersion_src().equalsIgnoreCase("IGRP2.3")){
					this.saveConfigFilesPlsql2_3(page,content);
				}else {
					this.saveConfigFilesPlsql2_1(page,content);
				}
			}
		}
		return result;		
	}

	private void saveConfigFilesPlsql2_1(Action page,String content){
		try {
			content = this.addClassAndPackage(content,page);
			String pathServer = Config.getBaseServerPahtXsl(page);			
			FileHelper.save(pathServer, page.getPage()+".xml", content);
			String json = XMLTransform.xmlTransformWithXSL(pathServer+File.separator+page.getPage()+".xml", Config.getBasePathXsl()+"images/IGRP/IGRP2.3/core/formgen/util/jsonConverter.xsl");
			FileHelper.save(pathServer, page.getPage()+".json", json);
			if(FileHelper.fileExists(Config.getWorkspace())) {
				String pathWorkSpace = Config.getBasePahtXsl(page);
				FileHelper.save(pathWorkSpace, page.getPage()+".xml", content);
				FileHelper.save(pathWorkSpace, page.getPage()+".json", json);
			}	
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void saveConfigFilesPlsql2_3(Action page,String content) {
		System.out.println("Page:"+page.getPage());
		//Verifica se o xml possui package_html que ser� o nome da classe
		//Caso n�o exista, assumi o nome do ficheiro como nome da classe
		content = this.addClassAndPackage(content,page);
		FileImportAppOrPage file = new FileImportAppOrPage("configs/"+app.getDad()+"/"+page.getPage()+"/"+page.getAction()+"/"+page.getPage()+".xml", content, 1);
		this.saveFiles(file , app);
		try {
			String path = Config.getBasePathXsl()+Config.getResolvePathXsl(app.getDad(), page.getPage(), page.getVersion())+File.separator+page.getPage()+".xml";
			//Gera codigo MVC a partir de xml, usando gerador xsl
			String modelViewController = XMLTransform.xmlTransformWithXSL(path, Config.getBasePathXsl()+"images/IGRP/IGRP2.3/core/formgen/util/plsql_import_to_java/XSL_GENERATOR.xsl");
			String[] partsJavaCode = modelViewController.toString().split(" END ");
			if(partsJavaCode.length > 2){
				String model = partsJavaCode[0]+"*/";
				String view = "/*"+partsJavaCode[1]+"*/";
				String controller = "/*"+partsJavaCode[2];
				FileImportAppOrPage fileM = new FileImportAppOrPage("pages/"+app.getDad()+"/"+page.getPage()+"/"+page.getPage()+".java",model,1);
				FileImportAppOrPage fileV = new FileImportAppOrPage("pages/"+app.getDad()+"/"+page.getPage()+"/"+page.getPage()+"View.java",view,1);
				FileImportAppOrPage fileC = new FileImportAppOrPage("pages/"+app.getDad()+"/"+page.getPage()+"/"+page.getPage()+"Controller.java",controller,1);
				this.filesToCompile.add(fileM);
				this.filesToCompile.add(fileV);
				this.filesToCompile.add(fileC);
			}
		} catch (TransformerConfigurationException e) {
		}
	}


	//Adiciona Class e Pacote no XML
	private String addClassAndPackage(String content,Action page) {
		int index =  content.indexOf("<package_html>");
		if(index >= 0){
			content = content.substring(0, content.indexOf("<package_html>")+"<package_html>".length())+ page.getPage()+content.substring(content.indexOf("</package_html>"));
			String package_name = Config.getBasePackage(app.getDad()).contains(".pages")?Config.getBasePackage(app.getDad()):Config.getBasePackage(app.getDad())+".pages";
			content = content.substring(0, content.indexOf("<package_db>")+"<package_db>".length())+package_name +content.substring(content.indexOf("</package_db>"));
		}else{
			content = this.configurePackageAndClass(content,page);
		}
		return content;
	}


	private String configurePackageAndClass(String content,Action page) {
		String aux = content.substring(0,content.indexOf("</app>")+"</app>".length());
		XMLWritter xml = new XMLWritter();
		xml.startElement("plsql");
			xml.setElement("action", page.getPage());
			xml.setElement("package_db", page.getPackage_name());//PackageName
			xml.setElement("package_html", page.getPage());//ClassName
		xml.endElement();
		aux += xml.toString() + content.substring(content.indexOf("</app>")+"</app>".length());
		return aux;
	}

}
