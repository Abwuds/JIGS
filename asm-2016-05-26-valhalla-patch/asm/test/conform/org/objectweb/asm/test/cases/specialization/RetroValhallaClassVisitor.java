package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 *
 * Created by Jefferson Mangue on 09/06/2016.
 */
class RetroValhallaClassVisitor extends ClassVisitor {

    public static final int API = Opcodes.ASM5;
    public static final String ANY_PACKAGE = "java/any/";
    public static final String BACK_FACTORY_NAME = "_BackFactory";
    private static final int COMPILER_VERSION = 52;
    public static final String BACK_FIELD = "_back__";
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
        if (!name.contains("<")) {
            super.visit(COMPILER_VERSION, access, name, signature, superName, interfaces);
            return;
        }
        // At this step, only anyfied classes contain the token '<'.
        // The inheritance is not handled yet.
        if (superName != null && !superName.equals("java/lang/Object")) {
            throw new IllegalStateException("Not inheritance allowed.");
        }
        // Cleaning the class name into the raw name.
        String rawName = name.substring(0, name.indexOf('<'));
        super.visit(COMPILER_VERSION, access, rawName, signature, superName, interfaces);

        // Creating the back class inside the any package, by concatenating "_BackFactory".
        // Now creating a back factory class, placed inside the package java/any".
        backFactoryName = ANY_PACKAGE + rawName + BACK_FACTORY_NAME;
        backClassVisitor.visit(version, Opcodes.ACC_PUBLIC, backFactoryName, null, "java/lang/Object", null);

        // Creating an Object field inside the class. It will be used to store the back class at runtime.
        super.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL, BACK_FIELD, "Ljava/lang/Object;", null, null);

        // Creating the constructor storing the back object.
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/Void;Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        mv.visitInsn(Opcodes.ALOAD_0);
        mv.visitInsn(Opcodes.ALOAD_2);
        mv.visitFieldInsn(Opcodes.PUTFIELD, rawName, BACK_FIELD, "Ljava/lang/Object;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        // Each fields are moved inside the back class.
        return backClassVisitor.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // If we have not created a back class, we only copy the method.
        // TODO the calling code can have special invokeDynamic to detect.
        if (!hasBackFactory()) { return super.visitMethod(access, name, desc, signature, exceptions); }
        // We have to turn every method into static method inside the back class.
        int backAccess = access + (name.equals("<init>") ? 0 : Opcodes.ACC_STATIC);
        MethodVisitor bmv = backClassVisitor.visitMethod(backAccess, name, desc, signature, exceptions);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        // Special method visitor visiting the initial class and splitting informations inside
        // the front and the back class.
        return new RetroValhallaMethodVisitor(API, mv, bmv);
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
