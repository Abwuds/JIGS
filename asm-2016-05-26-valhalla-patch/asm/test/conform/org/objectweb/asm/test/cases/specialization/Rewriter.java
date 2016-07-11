package org.objectweb.asm.test.cases.specialization;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.SubstitutionTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * Created by Jefferson Mangue on 26/05/2016.
 */
public class Rewriter {

    private static final String FOLDER_SPECIALIZATION = "compiled/";
    /** The directory visited */
    private final String directory;

    /**
     * @param directory the working directory to walk.
     */
    private Rewriter(String directory) {
        this.directory = directory;
    }

    /**
     * Compiles all the classes present inside the working directory.
     * @throws IOException
     * TODO walk recursively.
     */
    public void compileDirectory() throws IOException {
        File[] files = new File(directory).listFiles((dir, name) -> {
            return name.endsWith(".class");
        });
        for (File f : files) {
            compileClazz(f.toPath());
        }
    }

    /**
     * Compile the class at the given path according to the rewriting rules.
     * @param path the path of the file to compileDirectory.
     * @throws IOException
     */
    private void compileClazz(final Path path) throws IOException {
        System.out.println("Writing class : " + path);
        writeClazz(FOLDER_SPECIALIZATION + path.getFileName(), dump(path));
    }

    /**
     * Writes the given byte array to the specified path.
     * @param path the path to write the clazz.
     * @param clazz the byte array representing the clazz.
     * @throws IOException
     */
    private void writeClazz(final String path, final byte[] clazz) throws IOException {
        File f = new File(new File(directory), path);
        if (!f.getParentFile().exists() && !f.getParentFile().mkdirs())
            throw new IOException("Cannot create directory " + f.getParentFile());
        FileOutputStream o = new FileOutputStream(f);
        o.write(clazz);
        o.close();
    }

    private byte[] dump(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            FrontClassVisitor frontClassVisitor = new FrontClassVisitor(cw);
            // TODO do not write this attribute on non modified classes.
            new ClassReader(bytes).accept(frontClassVisitor, new Attribute[]{new SubstitutionTable()}, 0);
            // Writing the back factory if one exists.
            writeBackFactoryClazz(frontClassVisitor.getBackFactoryName(), frontClassVisitor);
            // Returning the current class byte array.
            return cw.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeBackFactoryClazz(String name, FrontClassVisitor frontClassVisitor) throws IOException {
        if (frontClassVisitor.hasBackFactory()) {
            writeClazz(FOLDER_SPECIALIZATION + name + ".class", frontClassVisitor.getBackFactoryBytes());
        }
    }

    public static void main(final String[] args) throws IOException {
        if (args.length < 0 || args[0].isEmpty()) {
            throw new IllegalArgumentException("Please provide the directory to visit.");
        }
        new Rewriter(args[0]).compileDirectory();
    }
}
