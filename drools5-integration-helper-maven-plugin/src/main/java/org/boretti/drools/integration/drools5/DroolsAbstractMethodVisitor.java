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

import org.boretti.drools.integration.drools5.annotations.DroolsPostCondition;
import org.boretti.drools.integration.drools5.annotations.DroolsPreCondition;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @author mbo
 * @since 1.1.0
 */
abstract class DroolsAbstractMethodVisitor extends AdviceAdapter {
	
	private DroolsClassVisitor visitor;
	
	private String parent;

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitAnnotation(java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String name, boolean arg1) {
		final AnnotationVisitor p = super.visitAnnotation(name, arg1);
		if (Type.getType(name).getClassName().equals(Type.getType(DroolsPreCondition.class).getClassName())) {
			havePreCondition=true;
			return new AnnotationVisitor() {

				@Override
				public void visit(String name, Object value) {
					if (name.equals("resourceName")) preResourceName=(String)value;
					else if (name.equals("error")) preError=(Type)value;
				}

				@Override
				public AnnotationVisitor visitAnnotation(String arg0,String arg1) {
					return this;
				}

				@Override
				public AnnotationVisitor visitArray(String name) {
					return this;
				}

				@Override
				public void visitEnd() {
					p.visitEnd();
				}

				@Override
				public void visitEnum(String name, String desc, String value) {
					if (name.equals("type")) preType=value;			
				}
				
			};
		} else if (Type.getType(name).getClassName().equals(Type.getType(DroolsPostCondition.class).getClassName())) {
			havePostCondition=true;
			return new AnnotationVisitor() {

				@Override
				public void visit(String name, Object value) {
					if (name.equals("resourceName")) postResourceName=(String)value;
					else if (name.equals("error")) postError=(Type)value;
					else if (name.equals("onException"))  postOnException=(Boolean)value;
				}

				@Override
				public AnnotationVisitor visitAnnotation(String arg0,String arg1) {
					return this;
				}

				@Override
				public AnnotationVisitor visitArray(String name) {
					return this;
				}

				@Override
				public void visitEnd() {
					p.visitEnd();
				}

				@Override
				public void visitEnum(String name, String desc, String value) {
					if (name.equals("type")) postType=value;			
				}
				
			};
		}
		return p;
	}
	
	private boolean havePreCondition=false;
	
	private String preResourceName=null;
	
	private String preType=null;
	
	private Type preError = null;
	
	private boolean havePostCondition=false;
	
	private String postResourceName=null;
	
	private String postType=null;
	
	private Type postError = null;
	
	private boolean postOnException = false;

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitAnnotationDefault()
	 */
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return super.visitAnnotationDefault();
	}
	
	private DroolsGoalExecutionLog droolsGoalExecutionLog;
	
	private String name;
	
	private String desc;

	public DroolsAbstractMethodVisitor(MethodVisitor mv, 
			int access,
			String name, 
			String desc,
			String parent,
			DroolsClassVisitor visitor, DroolsGoalExecutionLog droolsGoalExecutionLog) {
		super(mv, access, name, desc);
		this.name=name;
		this.desc=desc;
		this.parent=parent;
		this.visitor=visitor;
		this.droolsGoalExecutionLog=droolsGoalExecutionLog;
	}

	/**
	 * @return the havePreCondition
	 */
	protected boolean isHavePreCondition() {
		return havePreCondition;
	}

	/**
	 * @param havePreCondition the havePreCondition to set
	 */
	protected void setHavePreCondition(boolean havePreCondition) {
		this.havePreCondition = havePreCondition;
	}
	
	private void addCondition(boolean preCondition,String type,String resourceName,Type error) {
		droolsGoalExecutionLog.getLogs().add(new DroolsGoalExecutionLog(droolsGoalExecutionLog.getFileName(),droolsGoalExecutionLog.getAction(),"Method instrumentalization for "+((preCondition)?"pre":"post")+"-condition "+name+"/"+desc));
		visitor.setMethodChange(true);
		Type types[] = Type.getArgumentTypes(super.methodDesc);
		//runPreCondition
		this.visitVarInsn(Opcodes.ALOAD,0);
		this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getObjectType(parent).getInternalName(), "getClass", "()Ljava/lang/Class;");
		if (type==null) type = "COMPILED";
		this.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(DroolsServiceType.class), type, Type.getDescriptor(DroolsServiceType.class));
		this.visitLdcInsn(resourceName);
		this.visitLdcInsn(error);
		this.visitVarInsn(Opcodes.ALOAD,0);
		this.visitIntInsn(Opcodes.BIPUSH,types.length);
		this.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
		int position=1;
		for(int i=0;i<types.length;i++) {
			this.visitInsn(Opcodes.DUP);
			this.visitIntInsn(Opcodes.BIPUSH,i);
			if (types[i].equals(Type.BOOLEAN_TYPE)) {
				this.visitVarInsn(Opcodes.ILOAD, position);
				this.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;");
				position+=1;
			} else if (types[i].equals(Type.BYTE_TYPE)) {
				this.visitTypeInsn(Opcodes.NEW,"java/lang/Byte");
				this.visitInsn(Opcodes.DUP);
				this.visitVarInsn(Opcodes.ILOAD, position);
				this.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Byte.class), "<init>", "(B)V");
				position+=1;
			} else if (types[i].equals(Type.CHAR_TYPE)) {
				this.visitTypeInsn(Opcodes.NEW,"java/lang/Character");
				this.visitInsn(Opcodes.DUP);
				this.visitVarInsn(Opcodes.ILOAD, position);
				this.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Character.class), "<init>", "(C)V");
				position+=1;
			} else if (types[i].equals(Type.DOUBLE_TYPE)) {
				this.visitTypeInsn(Opcodes.NEW,"java/lang/Double");
				this.visitInsn(Opcodes.DUP);
				this.visitVarInsn(Opcodes.DLOAD, position);
				this.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Double.class), "<init>", "(D)V");
				position+=2;
			} else if (types[i].equals(Type.FLOAT_TYPE)) {
				this.visitTypeInsn(Opcodes.NEW,"java/lang/Float");
				this.visitInsn(Opcodes.DUP);
				this.visitVarInsn(Opcodes.FLOAD, position);
				this.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Float.class), "<init>", "(F)V");
				position+=1;
			} else if (types[i].equals(Type.INT_TYPE)) {
				this.visitTypeInsn(Opcodes.NEW,"java/lang/Integer");
				this.visitInsn(Opcodes.DUP);
				this.visitVarInsn(Opcodes.ILOAD, position);
				this.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Integer.class), "<init>", "(I)V");
				position+=1;
			} else if (types[i].equals(Type.LONG_TYPE)) {
				this.visitTypeInsn(Opcodes.NEW,"java/lang/Long");
				this.visitInsn(Opcodes.DUP);
				this.visitVarInsn(Opcodes.LLOAD, position);
				this.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Long.class), "<init>", "(J)V");
				position+=2;
			} else if (types[i].equals(Type.SHORT_TYPE)) {
				this.visitTypeInsn(Opcodes.NEW,"java/lang/Short");
				this.visitInsn(Opcodes.DUP);
				this.visitVarInsn(Opcodes.ILOAD, position);
				this.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Short.class), "<init>", "(S)V");
				position+=1;
			} else {
				this.visitVarInsn(Opcodes.ALOAD, position);
				position+=1;
			}
			this.visitInsn(Opcodes.AASTORE);
		}
		if (preCondition) this.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(DroolsProvider.class), "runPreCondition", "(Ljava/lang/Class;Lorg/boretti/drools/integration/drools5/DroolsServiceType;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/Object;[Ljava/lang/Object;)V");
		else this.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(DroolsProvider.class), "runPostCondition", "(Ljava/lang/Class;Lorg/boretti/drools/integration/drools5/DroolsServiceType;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/Object;[Ljava/lang/Object;)V");
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodEnter()
	 */
	@Override
	protected void onMethodEnter() {
		if (isHavePreCondition()) addCondition(true, preType, preResourceName, preError);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodExit(int)
	 */
	@Override
	protected void onMethodExit(int opcodes) {
		if (isHavePostCondition()) {
			if ((opcodes==Opcodes.ATHROW && postOnException) || opcodes!=Opcodes.ATHROW) addCondition(false,postType,postResourceName,postError);
		}
	}

	/**
	 * @return the havePostCondition
	 */
	public boolean isHavePostCondition() {
		return havePostCondition;
	}

	/**
	 * @param havePostCondition the havePostCondition to set
	 */
	public void setHavePostCondition(boolean havePostCondition) {
		this.havePostCondition = havePostCondition;
	}
}
