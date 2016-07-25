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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

/**
 * This goal post process class to instrumentalize the classes.
 * 
 * @author mbo
 * @since 1.0.0
 * @goal drools-postprocessor
 * @phase process-classes
 * @requiresDependencyResolution runtime
 */
public class DroolsClassPostProcessor extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isDebugEnabled())
			getLog().debug("starting drools post-compilation");
		DroolsClassProcessor.postProcess(getLog(),inputDirectory,extension,reportDirectory,reportFile,includes,excludes);
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
	 * The input directory from where to copy the rules.
	 * 
	 * @parameter expression="${project.basedir}/target/classes"
	 * @required
	 */
	private File inputDirectory;

	/**
	 * The default extension for class file.
	 * 
	 * @parameter expression=".class"
	 * @required
	 */
	private String extension;

	/**
	 * This is an optional list of includes pattern.
	 * 
	 * If this parameter is not used, the includes files are all file ending
	 * with the extension defined by the extension parameter. In any case, file
	 * must have the right extension.
	 * 
	 * @parameter
	 * @since 1.2.0
	 */
	private String[] includes;

	/**
	 * This is an optional list of excludes pattern.
	 * 
	 * If this parameter is not used, the excludes files are all file not ending
	 * with the extension defined by the extension parameter.
	 * 
	 * @parameter
	 * @since 1.2.0
	 */
	private String[] excludes;

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
	 * @parameter expression="drools-postprocessor.xml"
	 * @required
	 * @since 1.1.0
	 */
	private String reportFile;

	/**
	 * 
	 * @component role="org.apache.maven.shared.filtering.MavenResourcesFiltering"
	 *            role-hint="default"
	 * @required
	 */
	protected MavenResourcesFiltering mavenResourcesFiltering;

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

	/**
	 * @return the reportDirectory
	 */
	public File getReportDirectory() {
		return reportDirectory;
	}

	/**
	 * @param reportDirectory
	 *            the reportDirectory to set
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
	 * @param reportFile
	 *            the reportFile to set
	 */
	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	/**
	 * @return the includes
	 */
	public String[] getIncludes() {
		return includes;
	}

	/**
	 * @param includes the includes to set
	 */
	public void setIncludes(String[] includes) {
		this.includes = includes;
	}

	/**
	 * @return the excludes
	 */
	public String[] getExcludes() {
		return excludes;
	}

	/**
	 * @param excludes the excludes to set
	 */
	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

}
