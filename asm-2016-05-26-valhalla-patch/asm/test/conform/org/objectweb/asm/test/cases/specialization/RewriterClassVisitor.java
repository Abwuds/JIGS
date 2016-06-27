package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.*;

/**
 *
 * Created by Jefferson Mangue on 09/06/2016.
 */
class RewriterClassVisitor extends ClassVisitor {

    public static final int API = Opcodes.ASM5;
    private static final int COMPILER_VERSION = 52;

    RewriterClassVisitor(ClassVisitor cv) {
        super(API, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(COMPILER_VERSION, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new RewriterMethodVisitor(API, super.visitMethod(access, name, desc, signature, exceptions));
    }
}
