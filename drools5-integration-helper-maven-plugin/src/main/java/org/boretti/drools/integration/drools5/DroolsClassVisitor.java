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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.maven.plugin.logging.Log;
import org.boretti.drools.integration.drools5.annotations.DroolsService;
import org.drools.RuleBase;
import org.drools.StatelessSession;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * @author mbo
 * @since 1.0.0
 *
 */
class DroolsClassVisitor extends ClassAdapter {
		
	private Log logger;
	
	private String me;
	
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		if (version<Opcodes.V1_6) {
			allowed=false;
			logger.warn("Version of the class "+name+" is not enough to be supported");
		}
		else if ((access & Opcodes.ACC_INTERFACE)>0) {
			allowed=false;
		}
		me = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean arg1) {
		if (allowed){ 
			if (Type.getType(desc).getClassName().equals(Type.getType(DroolsService.class).getClassName())) {
				annotation_ok=true;
				droolsGoalExecutionLog.getLogs().add(new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"DroolsService annotation found. Field annotation will be used."));
			}
		}
		return super.visitAnnotation(desc, arg1);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		if (isNeedChangeForBoth()) {
			if (name.equals(DROOLS_FIELD_NAME)) allowed=false;
			//fieldType
			return new DroolsCheckFieldVisitor(logger, super.visitField(access, name, desc, signature, value),this,name);
		}
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] value) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, value);
		if (isNeedChangeForField() && (access & Opcodes.ACC_STATIC)==0) {
			if (name.equals("<init>")) {
				DroolsGoalExecutionLog dgl = new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Constructor instrumentalization "+desc);
				droolsGoalExecutionLog.getLogs().add(dgl);
				return new DroolsAddConstructorMethodVisitor(mv, access, name, desc,me,DROOLS_FIELD_NAME,DROOLS_FIELD_RULE,dgl);
			} else if (name.startsWith("get")){
				String fieldName = name.substring(3).toUpperCase();
				if (fieldType.containsKey(fieldName)) {
					byte b = fieldType.get(fieldName);
					if (b==0) {
						DroolsGoalExecutionLog dgl = new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Method instrumentalization "+name+"/"+desc);
						droolsGoalExecutionLog.getLogs().add(dgl);
						return new DroolsAddGeneratedGetMethodVisitor(mv, access, name, desc, me,this, DROOLS_FIELD_NAME, DROOLS_FIELD_RULE,DROOLS_METHOD_RUN,dgl);
					}
				}
			} else if (name.startsWith("set")){
				String fieldName = name.substring(3).toUpperCase();
				if (!fieldType.containsKey(fieldName)) {
					DroolsGoalExecutionLog dgl = new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Method instrumentalization "+name+"/"+desc);
					droolsGoalExecutionLog.getLogs().add(dgl);
					return new DroolsAddGeneratedSetMethodVisitor(mv, access, name, desc, me,this, DROOLS_FIELD_NAME,dgl);
				}
			} else if (name.startsWith("is")){
				String fieldName = name.substring(2).toUpperCase();
				if (fieldType.containsKey(fieldName)) {
					byte b = fieldType.get(fieldName);
					if (b==0) {
						DroolsGoalExecutionLog dgl = new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Method instrumentalization "+name+"/"+desc);
						droolsGoalExecutionLog.getLogs().add(dgl);
						return new DroolsAddGeneratedGetMethodVisitor(mv, access, name, desc, me,this, DROOLS_FIELD_NAME, DROOLS_FIELD_RULE,DROOLS_METHOD_RUN,dgl);
					}
				}
			}
		}
		if (isNeedChangeForMethod() && (access & Opcodes.ACC_STATIC)==0) {
			if (!name.equals("<init>")) {
				return new DroolsAddGeneralMethodVisitor(mv,access,name,desc,me,this,DROOLS_FIELD_NAME, DROOLS_FIELD_RULE,DROOLS_METHOD_RUN,droolsGoalExecutionLog);
			}
		}
		return mv;
	}

	@Override
	public void visitEnd() {
		FieldVisitor fv=null;
		if (isNeedChangeForBoth()) {
			fv = super.visitField(Opcodes.ACC_PRIVATE, DROOLS_FIELD_NAME, Type.BOOLEAN_TYPE.getDescriptor(),null, null);
			if (fv !=null) {
				AnnotationVisitor av = fv.visitAnnotation(Type.getType(Generated.class).getDescriptor(), true);
				AnnotationVisitor value = av.visitArray("value");
				value.visit("","Generated by Drools5IntegrationHelper Maven plugin");
				value.visitEnd();
				av.visit("date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(current));
				av.visitEnd();
				fv.visitEnd();
			}
		}
		if (isNeedChangeForField()) {
			fv = super.visitField(Opcodes.ACC_PRIVATE, DROOLS_FIELD_RULE, Type.getType(RuleBase.class).getDescriptor(),null, null);
			if (fv !=null) {
				AnnotationVisitor av = fv.visitAnnotation(Type.getType(Generated.class).getDescriptor(), true);
				AnnotationVisitor value = av.visitArray("value");
				value.visit("","Generated by Drools5IntegrationHelper Maven plugin");
				value.visitEnd();
				av.visit("date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(current));
				av.visitEnd();
				fv.visitEnd();
			}
			MethodVisitor mv= super.visitMethod(Opcodes.ACC_PRIVATE, DROOLS_METHOD_RUN, "()V", null, null);
			AnnotationVisitor av = mv.visitAnnotation(Type.getType(Generated.class).getDescriptor(), true);
			AnnotationVisitor value = av.visitArray("value");
			value.visit("","Generated by Drools5IntegrationHelper Maven plugin");
			value.visitEnd();
			av.visit("date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(current));
			av.visitEnd();
			mv.visitCode();
			Label start = new Label();
			mv.visitLabel(start);
			Label doIt = new Label();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, Type.getObjectType(me).getInternalName(), DROOLS_FIELD_NAME, Type.BOOLEAN_TYPE.getDescriptor());
			mv.visitJumpInsn(Opcodes.IFEQ, doIt);
			mv.visitInsn(Opcodes.RETURN);
			mv.visitLabel(doIt);
			mv.visitFrame(Opcodes.F_SAME, 1,new Object[]{Type.getObjectType(me).getInternalName()}, 0, new Object[]{});
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, Type.getObjectType(me).getInternalName(), DROOLS_FIELD_RULE, Type.getType(RuleBase.class).getDescriptor());
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getType(RuleBase.class).getInternalName(), "newStatelessSession", "()Lorg/drools/StatelessSession;");
			mv.visitInsn(Opcodes.DUP);
			mv.visitLdcInsn(FIELD_NAME_LOGGER);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getType(Object.class).getInternalName(), "getClass", "()Ljava/lang/Class;");
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getType(org.apache.log4j.Logger.class).getInternalName(), "getLogger", "(Ljava/lang/Class;)Lorg/apache/log4j/Logger;");
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getType(StatelessSession.class).getInternalName(), "setGlobal", "(Ljava/lang/String;Ljava/lang/Object;)V");
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getType(StatelessSession.class).getInternalName(), "execute", "(Ljava/lang/Object;)V");
			mv.visitVarInsn(Opcodes.ALOAD, 0);			
			mv.visitInsn(Opcodes.ICONST_1);
			mv.visitFieldInsn(Opcodes.PUTFIELD, Type.getObjectType(me).getInternalName(), DROOLS_FIELD_NAME, Type.BOOLEAN_TYPE.getDescriptor());
			mv.visitInsn(Opcodes.RETURN);
			Label end = new Label();
			mv.visitLabel(end);
			mv.visitLocalVariable("this", Type.getObjectType(me).getDescriptor(), null, start, end, 0);
			mv.visitMaxs(4, 1);
			mv.visitEnd();
		}
		super.visitEnd();
	}
	
	private static final String FIELD_NAME_LOGGER = "droolsLogger";
	
	private static final String DROOLS_FIELD_NAME="$drools";
	
	private static final String DROOLS_FIELD_RULE="$drools_rule";
	
	private static final String DROOLS_METHOD_RUN="$drools_run";
				
	private boolean allowed = true;
	
	private boolean annotation_ok=false;
	
	private boolean methodChange=false;
	
	private Map<String,Byte> fieldType = new HashMap<String,Byte>();
	
	private Date current = new Date();
	
	private DroolsGoalExecutionLog droolsGoalExecutionLog;

	public DroolsClassVisitor(Log logger,ClassVisitor arg0, DroolsGoalExecutionLog droolsGoalExecutionLog) {
		super(arg0);
		this.logger=logger;
		this.droolsGoalExecutionLog=droolsGoalExecutionLog;
	}
	
	public boolean isNeedChange() {
		return isNeedChangeForField() || (isNeedChangeForMethod()&&methodChange);
	}

	public boolean isNeedChangeForField() {
		return allowed && annotation_ok;
	}
	
	public boolean isNeedChangeForMethod() {
		return allowed;
	}
	
	public boolean isNeedChangeForBoth() {
		return allowed;
	}

	public boolean isAllowed() {
		return allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	/**
	 * @return the fieldType
	 */
	public Map<String, Byte> getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(Map<String, Byte> fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * @return the methodChange
	 */
	public boolean isMethodChange() {
		return methodChange;
	}

	/**
	 * @param methodChange the methodChange to set
	 */
	public void setMethodChange(boolean methodChange) {
		this.methodChange = methodChange;
	}

}
