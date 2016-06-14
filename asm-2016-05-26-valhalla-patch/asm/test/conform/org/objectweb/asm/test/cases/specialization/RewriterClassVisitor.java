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
        // Changing the descriptor to contain only object values and no TypeVar. Taking their bounds instead.
        String normalizedDesc = translateMethodDescriptor(desc);
        System.out.println(normalizedDesc);
        // TODO foreach TypeVar --> Write it in the special attribute as a value to substitute during specialization.
        return new RewriterMethodVisitor(API, super.visitMethod(access, name, normalizedDesc, signature, exceptions));
    }


    @Override
    public void visitEnd() {
        // TODO create attribute typeIndexes.
        super.visitEnd();
    }


    /**
     * Translate type var types contained inside a method descriptor to objects.
     *
     * @param desc the method descriptor to transform.
     * @return the method descriptor transformed.
     */
    private static String translateMethodDescriptor(String desc) {
        Type[] argumentTypes = Type.getMethodType(desc).getArgumentTypes();
        Type returnType = Type.getMethodType(desc).getReturnType();
        Type[] resultTypes = new Type[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            resultTypes[i] = typeVarToObject(argumentTypes[i]);
        }
        return Type.getMethodDescriptor(typeVarToObject(returnType), resultTypes);
    }

    /**
     * Transform a {@link @Type.TYPE_VAR} to a {link @Type.OBJECT}.
     *
     * @param type the type var instance to transform to Object.
     * @return the object built from the type var instance passed.
     */
    private static Type typeVarToObject(Type type) {
        if (type.getSort() != Type.TYPE_VAR) {
            return type;
        }
        String descriptor = type.getDescriptor();
        return Type.getType(descriptor.substring(descriptor.indexOf('/') + 1));
    }
}
