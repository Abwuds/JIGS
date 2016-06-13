package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.test.cases.Generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Jefferson Mangue on 26/05/2016.
 */
public class Rewriter extends Generator {

    private static final String CLASS = "Rewriter";
    private static final String PKG_SPECIALIZATION = "pkg/specialization/";

    @Override
    public void generate(final String dir) throws IOException {
        generate(dir, PKG_SPECIALIZATION + CLASS + ".class", dump());
    }

    public byte[] dump() {

        try {
            byte[] bytes = Files.readAllBytes(Paths.get("asm/test/resources/ContainerJava8.class"));
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            new ClassReader(bytes).accept(new RewriterClassVisitor(cw), 0);

/*
            MethodVisitor mv;

            cw.visit(V1_7, ACC_PUBLIC, PKG_SPECIALIZATION + CLASS, null, "java/lang/Object", null);

            // Main method.
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitMethodInsn(INVOKESTATIC, "pkg/specialization/Rewriter", "foo", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitEnd();
            mv.visitMaxs(0, 0);

            // Foo method printing Hello world.
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "foo", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitLdcInsn("Hello world");
            mv.visitInsn(ARETURN);
            mv.visitEnd();
            mv.visitMaxs(0, 0);

            cw.visitEnd();*/
            return cw.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
