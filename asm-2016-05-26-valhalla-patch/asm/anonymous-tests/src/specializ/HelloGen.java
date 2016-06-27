package specializ;

import org.objectweb.asm.*;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelloGen {
    public static void main(String[] args) throws Exception, Throwable {
        String outputClassName = "HelloDynamicGen";
        FileOutputStream fos = new FileOutputStream(new File("output/production/anonymous-tests/" + outputClassName + ".class"));
        byte[] bytes = dump(outputClassName, "bsm_new", "()Ljava/lang/String;");

        // instanciateRetroClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class<?> helloDynamicGen = new FunctionClassLoader().createClass("HelloDynamicGen", bytes);
        MethodHandle main = lookup.findStatic(helloDynamicGen, "script", MethodType.methodType(void.class, String[].class));
        main.invoke(null);
    }

    public static byte[] dump(String outputClassName, String bsmName, String descritpor) throws Exception {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        MethodVisitor mv;

        // Setup the basic metadata for the bootstrap class.
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, outputClassName, null, "java/lang/Object", null);

        // Create a standard void constructor.
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Create a standard main method.
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "script", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
        Handle bsm_new = new Handle(Opcodes.H_INVOKESTATIC, "rt/RT", "bsm_new", mt.toMethodDescriptorString(), false);
        mv.visitInvokeDynamicInsn("new", descritpor, bsm_new);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();

        return cw.toByteArray();
    }


    private static void instanciateRetroClass() throws IOException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        byte[] codes = Files.readAllBytes(Paths.get("ContainerJava10.class"));
        ClassReader reader = new ClassReader(codes);

        ClassWriter writer = new ClassWriter(reader, 0);
        int index = writer.newConst("Hello");

        Class<?> unsafeClass = Unsafe.class;
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);

        Object[] pool = new Object[index + 1];
        pool[index] = "Hello specialized";

        Class<?> frontClass = ClassLoader.getSystemClassLoader().loadClass("ContainerJava10");
        Class<?> backClass = unsafe.defineAnonymousClass(frontClass, codes, new Object[0]);
        Constructor<?> constructor = backClass.getConstructor(Object.class, Object.class, Object.class);
        try {
            Object o = constructor.newInstance("X", "U", "Y");
            System.out.println(o);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
