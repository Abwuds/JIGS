package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.test.cases.Generator;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Jefferson Mangue on 26/05/2016.
 */
public class Reader extends Generator {

    private static final String CLASS = "Reader";
    private static final String PKG_SPECIALIZATION = "pkg/specialization/";

    @Override
    public void generate(final String dir) throws IOException {
        read();
        generate(dir, PKG_SPECIALIZATION + CLASS + ".class", dump());
    }

    private void read() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("asm/test/resources/ContainerJava10.class"));
        ClassReader cr = new ClassReader(bytes);
        cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
    }

    public byte[] dump() {
        return new byte[0];
    }
}
