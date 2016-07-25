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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author mbo
 * @since 1.1.0
 */
@XmlRootElement
class DroolsGoalExecutionLogs implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3323763700589674989L;
	
	private List<DroolsGoalExecutionLog> logs = new ArrayList<DroolsGoalExecutionLog>();

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
	
	public void writeMeToFile(File output) 
	throws IOException, JAXBException {
		output.getParentFile().mkdirs();
		JAXBContext ctx = JAXBContext.newInstance("org.boretti.drools.integration.drools5", this.getClass().getClassLoader());
		Marshaller m = ctx.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
		m.marshal(this, output);
	}
	
	public static DroolsGoalExecutionLogs readLogs(File input) throws JAXBException {
		JAXBContext ctx = JAXBContext.newInstance("org.boretti.drools.integration.drools5", DroolsGoalExecutionLogs.class.getClassLoader());
		Unmarshaller m = ctx.createUnmarshaller();
		return (DroolsGoalExecutionLogs)m.unmarshal(input);
	}
	
}
