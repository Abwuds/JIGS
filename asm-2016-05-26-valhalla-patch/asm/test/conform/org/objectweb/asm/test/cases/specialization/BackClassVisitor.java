package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.*;

/**
 *
 * Created by Jefferson Mangue on 09/06/2016.
 */
class BackClassVisitor extends ClassVisitor {

    public static final String ANY_PACKAGE = "java/any/";
    public static final String BACK_FACTORY_NAME = "_BackFactory";
    private static final int COMPILER_VERSION = 52; // Java 1.8

    // Sets when visiting the class prototype.
    private String name;
    private String frontName;

    BackClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        frontName = name;
        this.name = ANY_PACKAGE + name + BACK_FACTORY_NAME;
        super.visit(COMPILER_VERSION, access, this.name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, null, value); // No parameterized signature.
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        int methodAccess;
        String methodDescriptor;
        // For each method of the back but the constructor, transforming it into a static method taking
        // in first parameter the front class.
        if (!name.equals("<init>")) {
            methodAccess = access + Opcodes.ACC_STATIC;
            Type mType = Type.getType(desc);
            Type[] argumentTypes = mType.getArgumentTypes();
            Type[] parameterTypes = new Type[argumentTypes.length + 1];
            parameterTypes[0] = Type.getType('L' + frontName + ';');
            for (int i = 1; i < parameterTypes.length; i++) { parameterTypes[i] = argumentTypes[i - 1]; }
            methodDescriptor =  Type.getMethodDescriptor(mType.getReturnType(), parameterTypes);
        } else {
            methodAccess = access;
            methodDescriptor = desc;
        }
        return new BackMethodVisitor(api, name, frontName, this.name, super.visitMethod(methodAccess, name, methodDescriptor, null, exceptions));
    }

    public String getName() {
        return name;
    }
}
