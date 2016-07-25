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

import org.boretti.drools.integration.drools5.annotations.DroolsResourceName;
import org.boretti.drools.integration.drools5.annotations.DroolsResourceType;
import org.drools.RuleBase;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author mbo
 * @since 1.0.0
 */
class DroolsAddConstructorMethodVisitor extends AdviceAdapter {
	
	private String parent;
			
	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitParameterAnnotation(int, java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitParameterAnnotation(int idx, String name,
			boolean arg2) {
		if (Type.getType(name).getClassName().equals(Type.getType(DroolsResourceName.class).getClassName())) {
			if (arguments[idx].equals(Type.getType(String.class))) {
				if (overrideNameJVMIndex>=0) {
					droolsGoalExecutionLog.getLogs().add(new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Warning, @DroolsResourceName can't be used more than one time in the same constructor"));
				} else {
					droolsGoalExecutionLog.getLogs().add(new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"This constructor will use resource Location override"));
					overrideNameJVMIndex = getJVMIndex(idx);
				}
			} else {
				droolsGoalExecutionLog.getLogs().add(new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Warning, @DroolsResourceName must be used on String parameter"));
			}
		} else if (Type.getType(name).getClassName().equals(Type.getType(DroolsResourceType.class).getClassName())) {
			if (arguments[idx].equals(Type.getType(DroolsServiceType.class))) {
				if (overrideTypeJVMIndex>=0) {
					droolsGoalExecutionLog.getLogs().add(new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Warning, @DroolsResourceType can't be used more than one time in the same constructor"));
				} else {
					droolsGoalExecutionLog.getLogs().add(new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"This constructor will use resource Location override"));
					overrideTypeJVMIndex = getJVMIndex(idx);
				}
			} else {
				droolsGoalExecutionLog.getLogs().add(new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Warning, @DroolsResourceType must be used on DroolsServiceType parameter"));				
			}
		}
		
		return super.visitParameterAnnotation(idx, name, arg2);
	}

	private String droolsName;
	
	private String droolsRule;
	
	private int getJVMIndex(int idx) {
		int jvmIdx=1;
		for(int i=0;i<idx;i++) {
			if (arguments[i].equals(Type.LONG_TYPE) || arguments[i].equals(Type.DOUBLE_TYPE)) jvmIdx++;
			jvmIdx++;
		}
		return jvmIdx;
	}
	
	private Type arguments[];
	
	private DroolsGoalExecutionLog droolsGoalExecutionLog;
	
	private int overrideNameJVMIndex = -1;
	
	private int overrideTypeJVMIndex = -1;

	public DroolsAddConstructorMethodVisitor(
			MethodVisitor mv, 
			int access,
			String name, 
			String desc,
			String parent,
			String droolsName,
			String droolsRule,
			DroolsGoalExecutionLog droolsGoalExecutionLog) {
		super(mv, access, name, desc);
		this.parent=parent;
		this.droolsName=droolsName;
		this.droolsRule=droolsRule;
		arguments=Type.getArgumentTypes(desc);
		this.droolsGoalExecutionLog=droolsGoalExecutionLog;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodEnter()
	 */
	@Override
	protected void onMethodEnter() {
		this.visitVarInsn(Opcodes.ALOAD,0);
		this.visitInsn(Opcodes.DUP);
		this.visitInsn(Opcodes.DUP);
		this.visitInsn(Opcodes.ICONST_0);
		this.visitFieldInsn(Opcodes.PUTFIELD,Type.getObjectType(parent).getInternalName(), droolsName, Type.BOOLEAN_TYPE.getDescriptor());
		this.visitTypeInsn(Opcodes.NEW,Type.getType(DroolsProvider.class).getInternalName());
		this.visitInsn(Opcodes.DUP);		
		this.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getType(DroolsProvider.class).getInternalName(), "<init>","()V");
		this.visitInsn(Opcodes.SWAP);
		this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getObjectType(parent).getInternalName(), "getClass", "()Ljava/lang/Class;");
		if (overrideNameJVMIndex>=0) {
			this.visitVarInsn(Opcodes.ALOAD, overrideNameJVMIndex);
		}
		if (overrideTypeJVMIndex>=0) {
			this.visitVarInsn(Opcodes.ALOAD, overrideTypeJVMIndex);			
		}
		if (overrideNameJVMIndex<0 && overrideTypeJVMIndex<0) {
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getType(DroolsProvider.class).getInternalName(), "getRuleBase","(Ljava/lang/Class;)Lorg/drools/RuleBase;");			
		} else if (overrideNameJVMIndex<0 && overrideTypeJVMIndex>=0) {
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getType(DroolsProvider.class).getInternalName(), "getRuleBaseOverride","(Ljava/lang/Class;Lorg/boretti/drools/integration/drools5/DroolsServiceType;)Lorg/drools/RuleBase;");			
		} else if (overrideNameJVMIndex>=0 && overrideTypeJVMIndex<0) {
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getType(DroolsProvider.class).getInternalName(), "getRuleBaseOverride","(Ljava/lang/Class;Ljava/lang/String;)Lorg/drools/RuleBase;");			
		} else {
			this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getType(DroolsProvider.class).getInternalName(), "getRuleBaseOverride","(Ljava/lang/Class;Ljava/lang/String;Lorg/boretti/drools/integration/drools5/DroolsServiceType;)Lorg/drools/RuleBase;");			
		}
		this.visitFieldInsn(Opcodes.PUTFIELD,Type.getObjectType(parent).getInternalName(), droolsRule, Type.getType(RuleBase.class).getDescriptor());
	}


	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack+6, maxLocals);
	}

}
