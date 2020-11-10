package cv.nosi.core.webapp.ie.import_export_v2.exports.application;

import cv.nosi.core.config.Config;
import cv.nosi.core.webapp.ie.import_export_v2.common.OptionsImportExport;
import cv.nosi.core.webapp.ie.import_export_v2.common.serializable.application.ApplicationSerializable;
import cv.nosi.core.webapp.ie.import_export_v2.exports.Export;
import cv.nosi.core.webapp.ie.import_export_v2.exports.IExport;
import cv.nosi.core.webapp.util.Core;
import cv.nosi.webapps.igrp.dao.Application;

/**
 * Emanuel
 * 5 Nov 2018
 */
public class ApplicationExport implements IExport{

	private Application application;
	private ApplicationSerializable applicationSerializable;
	
	public ApplicationExport(Application application) {
		super();
		this.application = application;
		this.applicationSerializable = new ApplicationSerializable();
	}

	@Override
	public String getFileName() {
		return OptionsImportExport.APP.getFileName();
	}

	@Override
	public String serialization() {
		Core.mapper(this.application, this.applicationSerializable);
		this.applicationSerializable.setVersion(new Config().VERSION);
		return Core.toJsonWithJsonBuilder(this.applicationSerializable);
	}

	@Override
	public void export(Export export, String[] ids) {
		export.add(this);
	}

	@Override
	public void add(String id) {
		
	}

}
