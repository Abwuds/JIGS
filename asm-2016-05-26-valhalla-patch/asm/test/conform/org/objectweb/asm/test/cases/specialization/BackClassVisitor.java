package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 *
 * Created by Jefferson Mangue on 09/06/2016.
 */
public class BackClassVisitor extends ClassVisitor {

    public static final String ANY_PACKAGE = "any/";
    public static final String BACK_FACTORY_NAME = "$BackFactory";

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
        super.visit(version, access, this.name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, null, value); // No parameterized signature.
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        int methodAccess;
        String methodDescriptor;
        if (name.equals("<init>")) {
            // We want to insert the front class lookup in each Back class species.
            methodAccess = access;
            methodDescriptor = insertMethodArgumentType(desc, Type.getType(MethodHandles.Lookup.class));
        } else {
            // For each method of the back but the constructor, transforming it into a static method taking
            // in first parameter the front class.
            methodAccess = access + Opcodes.ACC_STATIC;
            methodDescriptor =  insertMethodArgumentType(desc, Type.getType('L' + frontName + ';'));
        }
        return new BackMethodVisitor(api, name, frontName, this.name, super.visitMethod(methodAccess, name, methodDescriptor, null, exceptions));
    }

    public String getName() {
        return name;
    }

    private String insertMethodArgumentType(String desc, Type insertedType) {
        Type mType = Type.getMethodType(desc);
        Type[] argumentTypes = mType.getArgumentTypes();
        Type[] parameterTypes = new Type[argumentTypes.length + 1];
        parameterTypes[0] = insertedType;
        for (int i = 1; i < parameterTypes.length; i++) { parameterTypes[i] = argumentTypes[i - 1]; }
        return Type.getMethodDescriptor(mType.getReturnType(), parameterTypes);
    }

}
