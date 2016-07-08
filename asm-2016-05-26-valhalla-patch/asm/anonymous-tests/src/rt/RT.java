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

import org.objectweb.asm.Opcodes;
import specializ.HelloGen;

import java.lang.invoke.*;
import java.util.List;
import java.util.Objects;

/**
 * Bootstrap class for virtual access adaptations
 */
public class RT {


    public static CallSite bsm_new(MethodHandles.Lookup lookup, String name, MethodType type,
                                   String specialization) throws Throwable {
        System.out.println("BSM_NEW : lookup = [" + lookup + "], name = [" + name + "], type = [" + type + "], specialization = [" + specialization + "]");
        // TODO use a cache system.
        // TODO load class String name = lookup.lookupClass.
        Class<?> species = HelloGen.FUNCTION_CLASS_LOADER.loadClass("HelloDynamicGen");// Already loaded.
        return new ConstantCallSite(lookup.findConstructor(species, type.changeReturnType(void.class)));
    }

    public static CallSite bsm_getBackField(MethodHandles.Lookup lookup, String name,
                                            MethodType type) throws Throwable {
        MethodHandle mh = lookup.findStatic(RT.class, name, MethodType.methodType(Object.class, MethodHandles.Lookup.class));
        mh.bindTo(lookup);
        return new ConstantCallSite(mh);
    }

    public static CallSite bsm_setBackField(MethodHandles.Lookup lookup, String name,
                                            MethodType type) throws Throwable {
        MethodHandle mh = lookup.findStatic(RT.class, name, MethodType.methodType(Object.class, MethodHandles.Lookup.class));
        mh.bindTo(lookup);
        return new ConstantCallSite(mh);
    }

    /**
     * Retrieves the _back__ field of a front class specialized.
     * @param owner the owner class containing the field.
     * @return the _back__ field of the owner.
     */
    public static java.lang.invoke.MethodHandle getBackField(MethodHandles.Lookup lookup, Object owner, String name)
            throws NoSuchFieldException, IllegalAccessException {
        // Pour putField : lookup.findSetter() ??
        return lookup.findGetter(owner.getClass(), "_back__", Object.class);
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

    /** equals **/

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

    /** toString **/

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

    /** hashCode */

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
