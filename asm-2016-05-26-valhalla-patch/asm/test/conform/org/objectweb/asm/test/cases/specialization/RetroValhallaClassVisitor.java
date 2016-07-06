package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * Created by Jefferson Mangue on 09/06/2016.
 */
class RetroValhallaClassVisitor extends ClassVisitor {

    public static final int API = Opcodes.ASM5;
    public static final String ANY_PACKAGE = "java/any/";
    public static final String BACK_FACTORY_NAME = "_BackFactory";
    private static final int COMPILER_VERSION = 52;
    private final ClassWriter backClassWriter;
    private final RetroValhallaBackClassVisitor backClassVisitor;
    private String backFactoryName;

    public RetroValhallaClassVisitor(ClassVisitor cv) {
        super(API, cv);
        backClassWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.backClassVisitor = new RetroValhallaBackClassVisitor(backClassWriter);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        // The inheritance is not handled yet.
        if (superName != null && !superName.equals("java/lang/Object")) {
            throw new IllegalStateException("Not inheritance allowed.");
        }
        // Delegating to the classWriter.
        super.visit(COMPILER_VERSION, access, name, signature, superName, interfaces);
        // If the class contains any, creating the back class. At this step, only anyfied class contain the token '<'.
        if (!name.contains("<")) {
            return;
        }
        // The class is a "anyfied" one. Now creating a back factory class, placed inside the package java/any".
        backFactoryName = ANY_PACKAGE + name.substring(0, name.indexOf('<')) + BACK_FACTORY_NAME;
        backClassVisitor.visit(version, Opcodes.ACC_PUBLIC, backFactoryName, null, "java/lang/Object", null);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new RewriterMethodVisitor(API, super.visitMethod(access, name, desc, signature, exceptions));
    }

    public byte[] getBackFactoryBytes() {
        return backClassWriter.toByteArray();
    }

    public boolean hasBackFactory() {
        return backFactoryName != null;
    }

    public String getBackFactoryName() {
        return backFactoryName;
    }
}
