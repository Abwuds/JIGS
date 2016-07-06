package org.objectweb.asm.test.cases.specialization;


import org.objectweb.asm.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Created by Jefferson Mangue on 12/06/2016.
 * Visits a method of an anyfied class. Its role is to create compatibility and
 * specialization method bridges to a back class containing the specializable
 * code.
 */
class RetroValhallaMethodVisitor extends MethodVisitor {

    private final CompatibilityMethodVisitor cmv;
    private final BackMethodVisitor bmv;

    RetroValhallaMethodVisitor(int api, MethodVisitor methodVisitor, MethodVisitor backMethodVisitor) {
        super(api);
        cmv = new CompatibilityMethodVisitor(api, methodVisitor);
        bmv = new BackMethodVisitor(api, backMethodVisitor);
    }

    @Override
    public void visitCode() {
        cmv.visitCode();
        bmv.visitCode();
    }

    @Override
    public void visitParameter(String name, int access) {
        cmv.visitParameter(name, access);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        bmv.visitAttribute(attr);
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        bmv.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        bmv.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        bmv.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        bmv.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        bmv.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitTypedInsn(String name, int typedOpcode) {
        bmv.visitTypedInsn(name, typedOpcode);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        bmv.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        bmv.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        bmv.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        bmv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        bmv.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        bmv.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        bmv.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        bmv.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        bmv.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        bmv.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        bmv.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        bmv.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        bmv.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        bmv.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        bmv.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        bmv.visitEnd();
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return bmv.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return bmv.visitAnnotation(desc, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return bmv.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return bmv.visitParameterAnnotation(parameter, desc, visible);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return bmv.visitInsnAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return bmv.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        return bmv.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
    }
}
