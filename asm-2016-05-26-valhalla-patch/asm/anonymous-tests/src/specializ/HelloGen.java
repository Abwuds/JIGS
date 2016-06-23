package specializ;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelloGen {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        byte[] codes = Files.readAllBytes(Paths.get("ContainerJava10.class"));
        ClassReader reader = new ClassReader(codes);

        ClassWriter writer = new ClassWriter(reader, 0);
        int index = writer.newConst("Hello");

        Class<?> unsafeClass = sun.misc.Unsafe.class;
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
