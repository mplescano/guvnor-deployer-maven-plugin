/*
    Drools5 Integration Helper
    Copyright (C) 2009  Mathieu Boretti mathieu.boretti@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package org.boretti.drools.integration.drools5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * @author mbo
 * @since 1.0.0
 * 
 */
class DroolsClassProcessor {
	private DroolsClassProcessor() {
	}

	public static void ClassProcessor(Log logger, File src,
			DroolsGoalExecutionLogs dgel) throws MojoExecutionException {
		if (src == null)
			return;
		try {
			DroolsGoalExecutionLog del = new DroolsGoalExecutionLog(src
					.getAbsolutePath(), "postprocessor",
					"Class defined by file " + src.getAbsolutePath()
							+ " has been rewritten by the plugin");
			FileInputStream fis = new FileInputStream(src);
			ClassReader cr = new ClassReader(fis);
			ClassWriter cw = new ClassWriter(cr, 0);
			DroolsClassVisitor dcv = new DroolsClassVisitor(logger, cw, del);
			cr.accept(dcv, ClassReader.EXPAND_FRAMES);
			byte clazz[] = cw.toByteArray();
			fis.close();
			if (dcv.isNeedChange()) {
				logger.info("File " + src + " must be rewritten.");
				fis.close();
				FileOutputStream fos = new FileOutputStream(src);
				fos.write(clazz);
				dgel.getLogs().add(del);
			}
		} catch (FileNotFoundException e) {
			logger.error("" + e.getMessage(), e);
			throw new MojoExecutionException("" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("" + e.getMessage(), e);
			throw new MojoExecutionException("" + e.getMessage(), e);
		}
	}

	public static void postProcess(Log log, File inputDirectory,
			String extension, File reportDirectory, String reportFile, String[] includes, String[] excludes)
			throws MojoExecutionException {
		DroolsGoalExecutionLogs dgel = new DroolsGoalExecutionLogs();
		DirectoryScanner scanner = new DirectoryScanner();
		if (!inputDirectory.exists()) {
			log.warn("Skipping not existing directory " + inputDirectory);
			return;
		}
		scanner.setBasedir(inputDirectory);
		if (includes==null || includes.length==0) {
			scanner.setIncludes(new String[] { "**/*" + extension, "*" + extension });
		} else {
			log.info("Included files are "+Arrays.toString(includes));
			scanner.setIncludes(includes);			
		}
		if (excludes!=null && excludes.length!=0) {
			log.info("Excluded files are "+Arrays.toString(excludes));
			scanner.setExcludes(excludes);
		}
		log.info("Files must ends with "+extension+" to be processed");
		scanner.scan();
		for (String file : scanner.getIncludedFiles()) {
			if (!file.endsWith(extension)) continue;
			File sfile = new File(inputDirectory, file);
			DroolsClassProcessor.ClassProcessor(log, sfile, dgel);
		}
		try {
			dgel.writeMeToFile(new File(reportDirectory, reportFile));
		} catch (IOException e) {
			log.error("Unable to write report :" + e.getMessage(), e);
			throw new MojoExecutionException("" + e.getMessage(), e);
		} catch (JAXBException e) {
			log.error("Unable to write report :" + e.getMessage(), e);
			throw new MojoExecutionException("" + e.getMessage(), e);
		}
	}
}
