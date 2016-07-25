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
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.rule.Package;

/**
 * @author mbo
 *
 */
class DroolsHelper {
	private DroolsHelper() {}
	
	public static PackageBuilder getPackageBuilder(Log logger,MavenProject project) 
	throws MojoExecutionException{
		if (logger.isDebugEnabled()) logger.debug("starting creation of package builder");
		ClassLoader loader = DroolsHelper.class.getClassLoader();
		List<?> classpathFiles = null;
		try {
			classpathFiles = project.getRuntimeClasspathElements();
		} catch (Exception e) {
			throw new MojoExecutionException("Error during build "+e.getMessage(), e);
		}
		URL[] urls = new URL[classpathFiles.size()];

		 for (int i = 0; i < classpathFiles.size(); ++i) {
			 try {
				urls[i] = new File((String)classpathFiles.get(i)).toURI().toURL();
			} catch (MalformedURLException e) {
				throw new MojoExecutionException("Error during build "+e.getMessage(), e);
			}
		}
		 
		URLClassLoader ucl = new URLClassLoader(urls, loader);
		
		PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
		conf.setClassLoader(ucl);
		return new PackageBuilder(conf);
	}
	
	public static boolean compileSourceFile(Log logger,MavenProject project,File src,File dest,boolean xml, DroolsGoalExecutionLogs dgel) 
	throws MojoExecutionException{
		if (logger.isDebugEnabled()) logger.debug("starting compilation of source file "+src);
		PackageBuilder builder = getPackageBuilder(logger,project);
		try {
			InputStreamReader instream = new InputStreamReader( new FileInputStream(src));
			if (xml) builder.addPackageFromXml(instream);
			else builder.addPackageFromDrl(instream);
			if(!validationError(logger,builder.getErrors(),src)) return false;
			dest.getParentFile().mkdirs();
			Package dpackage = builder.getPackage();
			ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(dest));
			outstream.writeObject(dpackage);
			if (outstream != null)
				outstream.close();
			dgel.getLogs().add(new DroolsGoalExecutionLog(src.getAbsolutePath(),"compile","Compiling file "+src.getAbsolutePath()+" to "+dest.getAbsolutePath()));
			return true;
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("Error because of file not found "+e.getMessage(),e);
		} catch (DroolsParserException e) {
			if (!validationError(logger,builder.getErrors(),src)) return false;
			throw new MojoExecutionException("Error because of unexpected drools error "+e.getMessage(),e);			
		} catch (IOException e) {
			throw new MojoExecutionException("Error because of IO Error "+e.getMessage(),e);
		}
	}
	
	public static boolean validateSourceFile(Log logger,MavenProject project,File src,boolean xml, DroolsGoalExecutionLogs dgel) 
	throws MojoExecutionException{
		if (logger.isDebugEnabled()) logger.debug("starting validation of source file "+src);
		PackageBuilder builder = getPackageBuilder(logger,project);
		try {
			InputStreamReader instream = new InputStreamReader( new FileInputStream(src));
			if (xml) builder.addPackageFromXml(instream);
			else builder.addPackageFromDrl(instream);
			dgel.getLogs().add(new DroolsGoalExecutionLog(src.getAbsolutePath(),"validate","Validate file "+src.getAbsolutePath()));
			return validationError(logger,builder.getErrors(),src);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("Error because of file not found "+e.getMessage(),e);
		} catch (DroolsParserException e) {
			if (!validationError(logger,builder.getErrors(),src)) return false;
			throw new MojoExecutionException("Error because of unexpected drools error "+e.getMessage(),e);			
		} catch (IOException e) {
			throw new MojoExecutionException("Error because of IO Error "+e.getMessage(),e);
		}
	}
	
	private static boolean validationError(Log logger,PackageBuilderErrors errors,File src) {
		if (errors!=null) {
			DroolsError err[] = errors.getErrors();
			if (err.length>0) {
				for(DroolsError e:err) {
					StringBuilder sp = new StringBuilder();
					sp.append(""+src.getAbsolutePath()).append(":");
					if (e.getErrorLines()!=null) for(int l:e.getErrorLines()) sp.append(l).append(":");
					sp.append(e.getMessage());
					logger.error("Drools Error : "+sp);
				}
				return false;
			}
		}
		return true;
	}
	
}
