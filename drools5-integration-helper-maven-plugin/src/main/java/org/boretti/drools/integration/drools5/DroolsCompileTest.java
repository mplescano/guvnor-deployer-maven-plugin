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
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.codehaus.plexus.util.DirectoryScanner;


/**
 * This goal compile drools for test.
 * @author mbo
 * @goal drools-compile-test
 * @phase test-compile
 * @requiresDependencyResolution runtime
 */
public class DroolsCompileTest extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isDebugEnabled()) getLog().debug("starting drools compilation");
		DroolsGoalExecutionLogs dgel = new DroolsGoalExecutionLogs();
		DirectoryScanner scanner = new DirectoryScanner();
		if (!getInputDirectory().exists()) {
			getLog().warn("Skipping not existing directory "+getInputDirectory());
			return;
		}
		scanner.setBasedir(getInputDirectory());
		scanner.setIncludes(new String[] { "**/*"+getExtension(),"*"+getExtension() });
		scanner.scan();
		boolean ok=true;
		getOutputDirectory().mkdirs();
		for (String file : scanner.getIncludedFiles()) {
			File sfile = new File(getInputDirectory(), file);
			File dfile = new File(getOutputDirectory(),file.replaceFirst(getExtension()+"$", getCompiledExtension()));
			dfile.getParentFile().mkdirs();
			if (!DroolsHelper.compileSourceFile(getLog(), project, sfile,dfile,false,dgel)) ok=false;
		}
		scanner = new DirectoryScanner();
		if (!getInputDirectory().exists()) {
			getLog().warn("Skipping not existing directory "+getInputDirectory());
			return;
		}
		scanner.setBasedir(getInputDirectory());
		scanner.setIncludes(new String[] { "**/*"+getXmlExtension(),"*"+getXmlExtension() });
		scanner.scan();
		getOutputDirectory().mkdirs();
		for (String file : scanner.getIncludedFiles()) {
			File sfile = new File(getInputDirectory(), file);
			File dfile = new File(getOutputDirectory(),file.replaceFirst(getXmlExtension()+"$", getCompiledExtension()));
			dfile.getParentFile().mkdirs();
			if (!DroolsHelper.compileSourceFile(getLog(), project, sfile,dfile,true,dgel)) ok=false;
		}
		reportDirectory.mkdirs();
		try {
			dgel.writeMeToFile(new File(reportDirectory,reportFile));
		} catch (IOException e) {
			getLog().error("Unable to write report :"+e.getMessage(),e);
			throw new MojoExecutionException(""+e.getMessage(),e);
		} catch (JAXBException e) {
			getLog().error("Unable to write report :"+e.getMessage(),e);
			throw new MojoExecutionException(""+e.getMessage(),e);
		}
		if (!ok) {
			throw new MojoExecutionException("Validation error of the drools");
		}
	}

	/**
	 * @parameter expression="${session}"
	 * 
	 * @readonly
	 * @required
	 */
	protected MavenSession session;
	
	/**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /**
     * The output directory into which to copy the rules.
     *
     * @parameter expression="${project.basedir}/target/test-classes"
     * @required
     */
    private File outputDirectory;
    
    /**
     * The input directory from where to copy the rules.
     * 
     * @parameter expression="${project.basedir}/src/test/drools"
     * @required
     */
    private File inputDirectory;
    
    /**
     * The default extension for drools file
     * 
     * @parameter expression=".drl"
     * @required
     */
    private String extension;
    
    /**
     * The default extension for xml drools file
     * 
     * @parameter expression=".xml"
     * @required
     */
    private String xmlExtension;
    
    /**
     * The default extension for compiled drools file
     * 
     * @parameter expression=".cdrl"
     * @required
     */
    private String compiledExtension;
    
    /**
     * The output directory into which to write report file.
     *
     * @parameter expression="${project.basedir}/target/drools-report"
     * @required
     * @since 1.1.0
     */
    private File reportDirectory;
    
    /**
     * The output file name for the report file.
     *
     * @parameter expression="drools-compile-test.xml"
     * @required
     * @since 1.1.0
     */
    private String reportFile;
    
    /**
     * 
     * @component role="org.apache.maven.shared.filtering.MavenResourcesFiltering" role-hint="default"
     * @required
     */    
    protected MavenResourcesFiltering mavenResourcesFiltering;

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public File getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(File inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getCompiledExtension() {
		return compiledExtension;
	}

	public void setCompiledExtension(String compiledExtension) {
		this.compiledExtension = compiledExtension;
	}

	/**
	 * @return the xmlExtension
	 */
	public String getXmlExtension() {
		return xmlExtension;
	}

	/**
	 * @param xmlExtension the xmlExtension to set
	 */
	public void setXmlExtension(String xmlExtension) {
		this.xmlExtension = xmlExtension;
	}

	/**
	 * @return the reportDirectory
	 */
	public File getReportDirectory() {
		return reportDirectory;
	}

	/**
	 * @param reportDirectory the reportDirectory to set
	 */
	public void setReportDirectory(File reportDirectory) {
		this.reportDirectory = reportDirectory;
	}

	/**
	 * @return the reportFile
	 */
	public String getReportFile() {
		return reportFile;
	}

	/**
	 * @param reportFile the reportFile to set
	 */
	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	} 

}
