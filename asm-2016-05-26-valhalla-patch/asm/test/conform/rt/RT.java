/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package rt;

import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * Bootstrap class for virtual access adaptations
 */
public class RT {

    // TODO move this in one file and share it with org.objectweb.asm.test.cases.specialization.
    private static final String ANY_PACKAGE = "java/any/";
    private static final String BACK_FACTORY_NAME = "_BackFactory";
    private static final MethodType GET_BACK_FIELD_TYPE = MethodType.methodType(Object.class, MethodHandles.Lookup.class, Class.class, Object.class, String.class);
    private static final MethodType PUT_BACK_FIELD_TYPE = MethodType.methodType(void.class, MethodHandles.Lookup.class, Class.class, Object.class, Object.class, String.class);
    private static final MethodType INVOKE_CALL_TYPE = MethodType.methodType(Object.class, MethodHandles.Lookup.class, MethodType.class, String.class, Object.class, Object[].class); private static final ClassValue<byte[]> BACK_FACTORY = new ClassValue<byte[]>() {
        @Override
        protected byte[] computeValue(Class<?> type) {
            String backName = ANY_PACKAGE + type.getName() + BACK_FACTORY_NAME + ".class";
            try {
                return Files.readAllBytes(Paths.get(backName));
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
    };

    public static CallSite bsm_new(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        System.out.println("BSM_NEW lookup = [" + lookup + "], name = [" + name + "], type = [" + type + "]");
        Class<?> frontClass = type.returnType();
        System.out.println("Return type wanted : " + frontClass);
        // Allocate specialized class - with object for the moment - and return one of its constructor.
        MethodHandle mh = createBackSpecies(lookup, type, frontClass);
        System.out.println("Constructor found : " + mh);
        return new ConstantCallSite(mh);
    }

    public static CallSite bsm_newBackSpecies(MethodHandles.Lookup lookup, String name, MethodType type, String owner) throws Throwable {
        System.out.println("BSM_BACK_SPECIES lookup = [" + lookup + "], name = [" + name + "], type = [" + type + "]");
        Class<?> frontClass = ClassLoader.getSystemClassLoader().loadClass(owner);
        System.out.println("Return type wanted : " + frontClass);
        // Allocate specialized class - with object for the moment - and return one of its constructor.
        MethodHandle mh = createBackSpecies(lookup, type, frontClass);
        System.out.println("Constructor found : " + mh);
        return new ConstantCallSite(mh);
    }

    public static CallSite bsm_delegateCall(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        MethodHandle mh = lookup.findStatic(RT.class, "invokeCall", INVOKE_CALL_TYPE);
        // Dropping name, receiver to have the delegate method type.
        mh = mh.bindTo(lookup).bindTo(type.dropParameterTypes(0, 2));
        // Collecting trailing arguments inside an Object[] array. (- 2 for name, receiver).
        mh = mh.asCollector(Object[].class, type.parameterCount() - 2).asType(type);
        return new ConstantCallSite(mh);
    }

    public static Object invokeCall(MethodHandles.Lookup lookup, MethodType type, String name, Object receiver, Object... args) throws Throwable {
        MethodHandle mh = lookup.findStatic(receiver.getClass(), name, type).asType(type).asSpreader(Object[].class, args.length);
        return type.returnType().cast(mh.invoke(args));
    }

    public static CallSite bsm_getBackField(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        MethodHandle mh = lookup.findStatic(RT.class, name, GET_BACK_FIELD_TYPE);
        mh = mh.bindTo(lookup).bindTo(type.returnType()).asType(type);
        return new ConstantCallSite(mh);
    }

    public static CallSite bsm_putBackField(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        MethodHandle mh = lookup.findStatic(RT.class, name, PUT_BACK_FIELD_TYPE);
        mh = mh.bindTo(lookup).bindTo(type.parameterType(1)); // The field type is the parameter n:1.
        return new ConstantCallSite(mh);
    }

    private static MethodHandle createBackSpecies(MethodHandles.Lookup lookup, MethodType type, Class<?> frontClass)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        // TODO put unsafe into a constant.
        Class<?> unsafeClass = Unsafe.class;
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        byte[] backCode = BACK_FACTORY.get(frontClass);

        // Reading the specialization attributes.
        /*ClassReader reader = new ClassReader(backCode);
        ClassWriter writer = new ClassWriter(reader, 0);
        int index = writer.newConst("Hello");
        System.out.println("createBackSpecies - Hello index : " + index);*/
        SubstitutionTable substitutionTable = SubstitutionTableReader.read(backCode);
        System.out.println("After the substitutionTable : " + substitutionTable);


        // TODO get pool size by reading the class file and specialize.
        Object[] pool = new Object[0];
        // Has to launch with -noverify for the moment...
        System.out.println("Before anonymous");
        Class<?> backClass = unsafe.defineAnonymousClass(frontClass, backCode, pool);
        System.out.println("After anonymous");
        return lookup.findConstructor(backClass, type.changeReturnType(void.class)).asType(type);
    }

    public static Object getBackField(MethodHandles.Lookup lookup, Class<?> fieldType, Object owner, String name)
            throws Throwable {
        Object backField = getBack__(lookup, owner);
        return lookup.findGetter(backField.getClass(), name, fieldType).invoke(backField);
    }

    public static void putBackField(MethodHandles.Lookup lookup, Class<?> fieldType, Object owner, Object value, String name)
            throws Throwable {
        Object backField = getBack__(lookup, owner);
        lookup.findSetter(backField.getClass(), name, fieldType).invoke(backField, value);
    }

    private static Object getBack__(MethodHandles.Lookup lookup, Object owner) throws Throwable {
        return lookup.findGetter(owner.getClass(), "_back__", Object.class).invoke(owner);
    }

    /**
     * This bootstrap method returns an adapted callSite to match the virtual method signature defined
     * in the XYZ$any interface.
     */
    public static CallSite metafactory(MethodHandles.Lookup caller,
                                       String invokedName,
                                       MethodType invokedType,
                                       Object... args) throws ReflectiveOperationException {
        List<Class<?>> params = invokedType.parameterList();
        if (params.isEmpty()) {
            throw new AssertionError("Missing dynamic parameters!");
        }
        MethodHandle res = caller.findStatic(RT.class, invokedName, invokedType);
        return new ConstantCallSite(res);
    }

    /**
     * equals
     **/

    public static boolean equals(byte b1, byte b2) {
        return b1 == b2;
    }

    public static boolean equals(short s1, short s2) {
        return s1 == s2;
    }

    public static boolean equals(char c1, char c2) {
        return c1 == c2;
    }

    public static boolean equals(int i1, int i2) {
        return i1 == i2;
    }

    public static boolean equals(long l1, long l2) {
        return l1 == l2;
    }

    public static boolean equals(float f1, float f2) {
        return f1 == f2;
    }

    public static boolean equals(double d1, double d2) {
        return d1 == d2;
    }

    public static boolean equals(boolean b1, boolean b2) {
        return b1 == b2;
    }

    public static boolean equals(Object o1, Object o2) {
        return o1.equals(o2);
    }

    /**
     * toString
     **/

    public static String toString(byte b) {
        return String.valueOf(b);
    }

    public static String toString(short s) {
        return String.valueOf(s);
    }

    public static String toString(char c) {
        return String.valueOf(c);
    }

    public static String toString(int i) {
        return String.valueOf(i);
    }

    public static String toString(long l) {
        return String.valueOf(l);
    }

    public static String toString(float f) {
        return String.valueOf(f);
    }

    public static String toString(double d) {
        return String.valueOf(d);
    }

    public static String toString(boolean b) {
        return String.valueOf(b);
    }

    public static String toString(Object o) {
        return o.toString();
    }

    /**
     * hashCode
     */

    public static int hashCode(byte b) {
        return Objects.hashCode(b);
    }

    public static int hashCode(short s) {
        return Objects.hashCode(s);
    }

    public static int hashCode(char c) {
        return Objects.hashCode(c);
    }

    public static int hashCode(int i) {
        return Objects.hashCode(i);
    }

    public static int hashCode(long l) {
        return Objects.hashCode(l);
    }

    public static int hashCode(float f) {
        return Objects.hashCode(f);
    }

    public static int hashCode(double d) {
        return Objects.hashCode(d);
    }

    public static int hashCode(boolean b) {
        return Objects.hashCode(b);
    }

    public static int hashCode(Object o) {
        return o.hashCode();
    }
}
