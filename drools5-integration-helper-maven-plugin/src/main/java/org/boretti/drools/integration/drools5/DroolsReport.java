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
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * This goal analyse report from xml log of the various plugin.
 * 
 * The result is a readable log, computed from the report XML file, integrated into the site.
 * 
 * @author mbo
 * @goal drools-report
 * @phase site
 * @requiresDependencyResolution runtime
 */
public class DroolsReport extends AbstractMavenReport {

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		if (getLog().isDebugEnabled()) getLog().debug("Start of report for drools5");
		ResourceBundle bundle = getBundle(locale);
		Sink sink = getSink();
		createHead(bundle,locale,sink);
		createBody(bundle,locale,sink);
	}
	
	private void createHead(ResourceBundle bundle,Locale locale,Sink sink) {
		sink.head();
		sink.title();
		sink.text(bundle.getString(REPORT_HEADER));
		sink.title_();
		sink.head_();
	}
	
	private void createBody(ResourceBundle bundle,Locale locale,Sink sink) 
	throws MavenReportException{
		if (!reportInputDirectory.exists()) {
			getLog().warn("Skipping not existing directory "+reportInputDirectory);
			sink.body();
			sink.paragraph();
			sink.text("No report folder found");
			sink.paragraph_();
			sink.body_();			
			return;
		}
		sink.body();
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(reportInputDirectory);
		scanner.setIncludes(new String[] { "*.xml"});
		scanner.scan();
		sink.section1();
		sink.sectionTitle1();
		sink.text(bundle.getString(REPORT_TOC_NAME));
		sink.sectionTitle1_();
		sink.list();
		for (String file : scanner.getIncludedFiles()) {
			sink.listItem();
			sink.link("#s_"+file);
			sink.text(""+file);
			sink.link_();
			sink.listItem_();
		}
		sink.list_();
		sink.section1_();
		for (String file : scanner.getIncludedFiles()) {
			File input = new File(reportInputDirectory,file);
			createOneFile(bundle,locale,sink,input,file);
		}
		sink.body_();
	}
	
	private void createOneFile(ResourceBundle bundle,Locale locale,Sink sink,File input,String file) 
	throws MavenReportException{
		try {
			if (getLog().isDebugEnabled()) getLog().debug("Start of report for drools5 for "+input.getAbsolutePath());
			DroolsGoalExecutionLogs logs = DroolsGoalExecutionLogs.readLogs(input);
			sink.section1();
			sink.anchor("s_"+file);
			sink.sectionTitle1();
			sink.anchor_();
			sink.text(""+file);
			sink.sectionTitle1_();
			sink.paragraph();
			sink.italic();
			sink.text(String.format(locale,bundle.getString(REPORT_FILE_HEADER),file));
			sink.italic_();
			sink.paragraph_();
			List<DroolsGoalExecutionLog> logsList = logs.getLogs();
			boolean full=false;
			if (logsList!=null) {
				if (logsList.size()>0) {
					sink.definitionList();
					for(DroolsGoalExecutionLog entry : logsList) createOneLog(bundle,locale,sink,input,file,entry);
					sink.definitionList_();
					full=true;
				}
			}
			if (!full) {
				sink.paragraph();
				sink.text(bundle.getString(REPORT_NO_LOG_FOUND));
				sink.paragraph_();
			}
			sink.section1_();
		} catch (Exception e) {
			String msg = "Unable to read file :"+e.getMessage();
			getLog().error(msg);
			throw new MavenReportException(msg,e);
		}
	}
	
	private void createOneLog(ResourceBundle bundle,Locale locale,Sink sink,File input,String file,DroolsGoalExecutionLog entry) 
	throws MavenReportException{
		if (getLog().isDebugEnabled()) getLog().debug("Start of report for drools5 for "+input.getAbsolutePath()+":"+entry);
		String action = entry.getAction();
		String comments = entry.getComments();
		String fileName = entry.getFileName();
		long timeStamp = entry.getTimestamp();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,locale);
		List<DroolsGoalExecutionLog> logsList = entry.getLogs();
		sink.definitionListItem();
		sink.definedTerm();
		sink.text(""+df.format(new Date(timeStamp))+" - ");
		sink.italic();
		sink.text(""+action);
		sink.italic_();
		sink.definedTerm_();
		sink.definition();
		sink.table();
		sink.tableRow();
		sink.tableHeaderCell();
		sink.text(bundle.getString(REPORT_FILE_NAME_HEADER));
		sink.tableHeaderCell_();
		sink.tableHeaderCell();
		sink.text(bundle.getString(REPORT_COMMENTS_NAME_HEADER));
		sink.tableHeaderCell_();
		sink.tableRow_();
		sink.tableRow();
		sink.tableCell();
		sink.text(fileName);
		sink.tableCell_();
		sink.tableCell();
		sink.text(comments);
		sink.tableCell_();
		sink.tableRow_();		
		sink.table_();
		if (logsList!=null) {
			if (logsList.size()>0) {
				sink.definitionList();
				for(DroolsGoalExecutionLog entry2 : logsList) createOneLog(bundle,locale,sink,input,file,entry2);
				sink.definitionList_();
			}
		}
		sink.definition_();
		sink.definitionListItem_();
	}
	
	private static final String REPORT_HEADER = "report.header";
	
	private static final String REPORT_DESCRIPTION = "report.description";
	
	private static final String REPORT_NAME = "report.name";
	
	private static final String REPORT_FILE_HEADER = "report.file.header";
	
	private static final String REPORT_FILE_NAME_HEADER = "report.file.header.name";
	
	private static final String REPORT_COMMENTS_NAME_HEADER = "report.file.header.comments";
	
	private static final String REPORT_NO_LOG_FOUND = "report.no.logs";
	
	private static final String REPORT_TOC_NAME = "report.toc.name";
	

	/**
	 * Directory where reports will go.
	 * 
	 * @parameter expression="${project.reporting.outputDirectory}"
	 * @required
	 */
	protected File reportOutputDirectory;

	/**
	 * Directory from where to read logs.
	 * 
	 * @parameter expression="${project.basedir}/target/drools-report"
	 * @required
	 */
	protected File reportInputDirectory;

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	@Override
	protected String getOutputDirectory() {
		return reportOutputDirectory.getAbsolutePath();
	}

	@Override
	protected MavenProject getProject() {
		return project;
	}

	@Override
	protected Renderer getSiteRenderer() {
		return renderer;
	}

	@Override
	public String getDescription(Locale locale) {
		return getBundle(locale).getString(REPORT_DESCRIPTION);
	}

	@Override
	public String getName(Locale locale) {
		return getBundle(locale).getString(REPORT_NAME);
	}

	@Override
	public String getOutputName() {
		return "drools-report";
	}

	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("drools-report", locale, this
				.getClass().getClassLoader());
	}

	/**
	 * <i>Maven Internal</i>: The Doxia Site Renderer.
	 * 
	 * @component
	 */
	protected Renderer renderer;
}
