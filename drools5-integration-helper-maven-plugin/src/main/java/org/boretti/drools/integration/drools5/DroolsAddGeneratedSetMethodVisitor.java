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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author mbo
 * @since 1.0.0
 */
class DroolsAddGeneratedSetMethodVisitor extends DroolsAbstractMethodVisitor {
	
	private String parent;
	
	private String droolsName;

	public DroolsAddGeneratedSetMethodVisitor(MethodVisitor mv, 
			int access,
			String name, 
			String desc,
			String parent,
			DroolsClassVisitor visitor,
			String droolsName, DroolsGoalExecutionLog droolsGoalExecutionLog) {
		super(mv, access, name, desc,parent,visitor,droolsGoalExecutionLog);
		this.parent=parent;
		this.droolsName=droolsName;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodEnter()
	 */
	@Override
	protected void onMethodEnter() {
		super.onMethodEnter();
		this.visitVarInsn(Opcodes.ALOAD,0);
		this.visitInsn(Opcodes.ICONST_0);
		this.visitFieldInsn(Opcodes.PUTFIELD,Type.getObjectType(parent).getInternalName(), droolsName, Type.BOOLEAN_TYPE.getDescriptor());
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.LocalVariablesSorter#visitMaxs(int, int)
	 */
	@Override
	public void visitMaxs(int arg0, int arg1) {
		if (isHavePreCondition()||isHavePostCondition()) super.visitMaxs(arg0+15, arg1);
		else super.visitMaxs(arg0+2, arg1);
	}

}
