package specializ;

public class FunctionClassLoader extends ClassLoader {
  public Class<?> createClass(String name, byte[] instrs) {
    return defineClass(name, instrs, 0, instrs.length);
  }
}