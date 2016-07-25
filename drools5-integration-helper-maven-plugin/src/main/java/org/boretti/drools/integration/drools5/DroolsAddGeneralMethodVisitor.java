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

import org.objectweb.asm.MethodVisitor;


/**
 * @author mbo
 * @since 1.1.0
 */
class DroolsAddGeneralMethodVisitor extends DroolsAbstractMethodVisitor {
	
	//private String parent;
	
	//private String droolsName;
	
	//private String droolsRule;

	//private String droolsRun;
	

	public DroolsAddGeneralMethodVisitor(MethodVisitor mv, 
			int access,
			String name, 
			String desc,
			String parent,
			DroolsClassVisitor visitor,
			String droolsName,
			String droolsRule,
			String droolsRun, DroolsGoalExecutionLog droolsGoalExecutionLog) {
		super(mv, access, name, desc,parent,visitor,droolsGoalExecutionLog);
		//this.parent=parent;
		//this.droolsName=droolsName;
		//this.droolsRule=droolsRule;	
		//this.droolsRun=droolsRun;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodEnter()
	 */
	@Override
	protected void onMethodEnter() {
		super.onMethodEnter();
	}
	
	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.LocalVariablesSorter#visitMaxs(int, int)
	 */
	@Override
	public void visitMaxs(int arg0, int arg1) {
		if (isHavePreCondition()||isHavePostCondition()) super.visitMaxs(arg0+15, arg1);
		else super.visitMaxs(arg0+0, arg1);
	}

}
