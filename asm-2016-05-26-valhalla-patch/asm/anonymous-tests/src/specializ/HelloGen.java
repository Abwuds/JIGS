package specializ;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import sun.misc.Unsafe;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class HelloGen {
  public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
    byte[] codes = Files.readAllBytes(Paths.get("specializ/Hello.class"));
    ClassReader reader = new ClassReader(codes);
    
    ClassWriter writer = new ClassWriter(reader, 0);
    //reader.accept(writer, 0);
    int index = writer.newConst("Hello");
    
    Class<?> unsafeClass = sun.misc.Unsafe.class;
    Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
    theUnsafe.setAccessible(true);
    Unsafe unsafe = (Unsafe)theUnsafe.get(null);
    
    Object[] pool = new Object[index + 1];
    pool[index] = "Hello specialized";
    Class<?> mainClass = unsafe.defineAnonymousClass(HelloGen.class, codes, pool);
    Method main = mainClass.getMethod("main", String[].class);
    main.invoke(null, new Object[] { null });
  }
}
