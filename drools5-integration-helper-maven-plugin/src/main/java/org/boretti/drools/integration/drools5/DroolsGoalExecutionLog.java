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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

/**
 * @author mbo
 * @since 1.1.0
 */
@XmlType
class DroolsGoalExecutionLog implements Serializable{

	/**
	 * @param timestamp
	 * @param fileName
	 * @param action
	 * @param comments
	 */
	public DroolsGoalExecutionLog(long timestamp, String fileName,
			String action, String comments) {
		super();
		this.timestamp = timestamp;
		this.fileName = fileName;
		this.action = action;
		this.comments = comments;
	}
	
	/**
	 * @param fileName
	 * @param action
	 * @param comments
	 */
	public DroolsGoalExecutionLog(String fileName,
			String action, String comments) {
		super();
		this.timestamp = System.currentTimeMillis();
		this.fileName = fileName;
		this.action = action;
		this.comments = comments;
	}

	/**
	 * 
	 */
	public DroolsGoalExecutionLog() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5998453839779669213L;
	
	private long timestamp;
	
	private String fileName;
	
	private String action;
	
	private String comments;
	
	private List<DroolsGoalExecutionLog> logs = new ArrayList<DroolsGoalExecutionLog>();

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the logs
	 */
	public List<DroolsGoalExecutionLog> getLogs() {
		return logs;
	}

	/**
	 * @param logs the logs to set
	 */
	public void setLogs(List<DroolsGoalExecutionLog> logs) {
		this.logs = logs;
	}
}
