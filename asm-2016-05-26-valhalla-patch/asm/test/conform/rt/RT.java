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

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.test.cases.specialization.BackClassVisitor;
import org.objectweb.asm.test.cases.specialization.FrontClassVisitor;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Bootstrap class for virtual access adaptations
 */
public class RT {

    private static final Unsafe UNSAFE = initUnsafe();
    private static final String ANY_PACKAGE = BackClassVisitor.ANY_PACKAGE;
    private static final String BACK_FACTORY_NAME = BackClassVisitor.BACK_FACTORY_NAME;
    private static final MethodType BSMS_TYPE = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
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
        // System.out.println("BSM_DELEGATE_CALL : lookup = [" + lookup + "], name = [" + name + "], type = [" + type + "]");
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

    public static CallSite bsm_getBackField(MethodHandles.Lookup frontLookup, String name, MethodType type) throws Throwable {
        MethodHandle mh = frontLookup.findStatic(RT.class, name, GET_BACK_FIELD_TYPE);
        mh = mh.bindTo(frontLookup).bindTo(type.returnType()).asType(type);
        return new ConstantCallSite(mh);
    }

    public static CallSite bsm_putBackField(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        MethodHandle mh = lookup.findStatic(RT.class, name, PUT_BACK_FIELD_TYPE);
        mh = mh.bindTo(lookup).bindTo(type.parameterType(1)); // The field type is the parameter n:1.
        return new ConstantCallSite(mh);
    }

    private static MethodHandle createBackSpecies(MethodHandles.Lookup frontClassLookup, MethodType type, Class<?> frontClass)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        System.out.println("RT#createBackSpecies : frontClassLookup = [" + frontClassLookup + "], type = [" + type + "], frontClass = [" + frontClass + "]");
        System.out.println("Param number : " + type.parameterArray().length);
        // Reading the specialization attributes.
        // TODO store the Substitution table in a couple values for the key class in classValue.
        byte[] backCode = BACK_FACTORY.get(frontClass);
        SubstitutionTable substitutionTable = SubstitutionTableReader.read(backCode);
        System.out.println("After the substitutionTable : " + substitutionTable);


        // Creating substitution pool.
        Object[] pool = new Object[substitutionTable.getMax() + 1];
        for (Map.Entry<Integer, Map.Entry<String, String>> descs : substitutionTable.getDescriptors().entrySet()) {
            Integer index = descs.getKey();
            Map.Entry<String, String> ownerAndDescriptor = descs.getValue();
            String owner = ownerAndDescriptor.getKey();
            String descriptor = ownerAndDescriptor.getValue();
            if (!owner.equals(BackClassVisitor.RT_METHOD_HANDLE_TYPE)) {
                pool[index] = Type.specializeDescriptor(descriptor, type.parameterArray());
                continue;
            }

            if (descriptor.equals(BackClassVisitor.HANDLE_RT_BSM_NEW)) {
                //pool[index] = frontClassLookup.findStatic(RT.class, "bsm_new", BSMS_TYPE); // Idx : 1
            } else if (descriptor.equals(BackClassVisitor.HANDLE_RT_BSM_GET_FIELD)) {
                // TODO get the index into
                // Preparing the method handle for the invoke call with Object varargs.
                MethodHandle bsm_getBackField = frontClassLookup.findStatic(RT.class, "bsm_getBackField", BSMS_TYPE);
                pool[64] = bsm_getBackField.asSpreader(Object[].class, 3).asType(MethodType.methodType(Object.class, Object[].class));
            } else if (descriptor.equals(BackClassVisitor.HANDLE_RT_BSM_PUT_FIELD)) {
                // Preparing the method handle for the invoke call with Object varargs.
                MethodHandle bsm_putBackField = frontClassLookup.findStatic(RT.class, "bsm_putBackField", BSMS_TYPE);
                pool[84] = bsm_putBackField.asSpreader(Object[].class, 3).asType(MethodType.methodType(Object.class, Object[].class));
            }
        }

        // Passing Object.class
        Class<?> backClass = UNSAFE.defineAnonymousClass(Object.class, backCode, pool);
        MethodHandle constructor = frontClassLookup.findConstructor(backClass, type.changeReturnType(void.class));
        // We set the front class lookup parameter, and we want to cast the result to an Object instead of the plain anonymous BackClass.
        MethodHandle methodHandle = constructor.asType(type.changeReturnType(Object.class));
        return methodHandle;
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
        return lookup.findGetter(owner.getClass(), FrontClassVisitor.BACK_FIELD, Object.class).invoke(owner);
    }

    private static Unsafe initUnsafe() {
        try {
            Class<?> unsafeClass = Unsafe.class;
            Field theUnsafe = null;
            theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
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
