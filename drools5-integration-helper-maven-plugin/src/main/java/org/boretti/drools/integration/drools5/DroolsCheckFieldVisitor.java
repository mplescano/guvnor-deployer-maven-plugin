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

import org.apache.maven.plugin.logging.Log;
import org.boretti.drools.integration.drools5.annotations.DroolsGenerated;
import org.boretti.drools.integration.drools5.annotations.DroolsIgnored;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckFieldAdapter;

/**
 * @author mbo
 *
 */
class DroolsCheckFieldVisitor extends CheckFieldAdapter {
	
	private DroolsClassVisitor visitor;
	
	@SuppressWarnings("unused")
	private Log logger;
	
	private String fieldName;

	public DroolsCheckFieldVisitor(Log logger,FieldVisitor parent,DroolsClassVisitor visitor,String fieldName) {
		super(parent);
		this.visitor=visitor;
		this.logger=logger;
		this.fieldName=fieldName;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.util.CheckFieldAdapter#visitAnnotation(java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String name, boolean arg1) {
		if (Type.getType(name).getClassName().equals(Type.getType(DroolsGenerated.class).getClassName())) {
			visitor.getFieldType().put(fieldName.toUpperCase(), (byte)0);
		} else if (Type.getType(name).getClassName().equals(Type.getType(DroolsIgnored.class).getClassName())) {
			visitor.getFieldType().put(fieldName.toUpperCase(), (byte)1);
		}
		return super.visitAnnotation(name, arg1);
	}

}
