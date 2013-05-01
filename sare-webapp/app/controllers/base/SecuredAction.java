/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package controllers.base;

import static controllers.base.LoggedAction.*;

import java.io.*;

import org.apache.commons.io.*;

import play.*;
import play.mvc.Action.Simple;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class SecuredAction
		extends Simple {

	public boolean hasVirus(File file) {
		if (file == null) {
			return false;
		}
		
		try {
			Process process = Runtime.getRuntime()
				.exec(Play.application().configuration().getString("application.virusChecker.command"));
			byte[] buffer = new byte[2048];
			InputStream stream = FileUtils.openInputStream(file);
			while (IOUtils.read(stream, buffer, 0, 2048) != 0) {
				process.getOutputStream().write(buffer);
			}
			process.getOutputStream().close();
			try {
				int result = process.waitFor();
				if (result == 0) {
					return false;
				} else if (result == 1) {
					return true;
				} else {
					throw new InternalError();
				}
			} catch (InterruptedException e) {
				return hasVirus(file);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Result call(Context ctx) throws Throwable {
		if (Play.application().configuration().getBoolean("application.virusChecker.enabled") == true) {
			MultipartFormData mfd = ctx.request().body().asMultipartFormData();
			if (mfd != null) {
				for (FilePart filePart : mfd.getFiles()) {
					Logger.info(getLogEntry(ctx, String.format("checking uploaded file: '%s' for viruses", filePart.getFilename())));
					if (hasVirus(filePart.getFile())) {
						filePart.getFile().delete();
						throw new IllegalArgumentException(String.format("virus found in uploaded file: '%s'", filePart.getFilename()));
					}
					
					Logger.info(getLogEntry(ctx, String.format("file: '%s' is safe", filePart.getFilename())));
				}
			}
		}
		
		return delegate.call(ctx);
	}
}