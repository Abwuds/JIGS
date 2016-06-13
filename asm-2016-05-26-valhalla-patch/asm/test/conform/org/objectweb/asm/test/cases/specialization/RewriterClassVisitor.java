package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.*;

/**
 * Created by Jefferson Mangue on 09/06/2016.
 */
public class RewriterClassVisitor extends ClassVisitor {

    public static final int API = Opcodes.ASM5;

    public RewriterClassVisitor(ClassVisitor cv) {
        super(API, cv);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new RewriterMethodVisitor(API, super.visitMethod(access, name, desc, signature, exceptions));
    }
}
