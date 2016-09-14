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

import specialization.BackClassVisitor;
import specialization.FrontClassVisitor;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import java.io.IOException;
import java.lang.invoke.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Bootstrap class for virtual access adaptations
 */
public class RT {

    public static final MethodType SPECIALIZED_CONSTRUCTOR_TYPE = MethodType.methodType(void.class, Void.class, Object.class);
    private static final MethodHandle LOOKUP_CONSTRUCTOR_MH = initLookupConstructor();
    private static final Unsafe UNSAFE = initUnsafe();
    private static final String ANY_PACKAGE = BackClassVisitor.ANY_PACKAGE;
    private static final String BACK_FACTORY_NAME = BackClassVisitor.BACK_FACTORY_NAME;

    public static final MethodType TYPE_BSM_GETFIELD = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, MethodHandles.Lookup.class);
    public static final MethodType TYPE_BSM_PUTFIELD = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, MethodHandles.Lookup.class);
    public static final MethodType TYPE_BSM_INVOKE_SPECIAL_FROM_BACK = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, MethodHandles.Lookup.class);
    public static final MethodType TYPE_NO_LOOKUP_BSM_GETFIELD = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
    public static final MethodType TYPE_NO_LOOKUP_BSM_PUTFIELD = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
    public static final MethodType TYPE_PUTFIELD = MethodType.methodType(MethodHandle.class, MethodHandles.Lookup.class, String.class, Class.class, Object.class);
    public static final MethodType TYPE_GETFIELD = MethodType.methodType(MethodHandle.class, MethodHandles.Lookup.class, String.class, Class.class, Object.class);
    public static final MethodType TYPE_METAFACTORY = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, MethodHandles.Lookup.class, String.class);
    public static final MethodType TYPE_BSM_CREATE_ANY = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, MethodHandles.Lookup.class);
    public static final MethodType TYPE_NO_LOOKUP_BSM_CREATE_ANY = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
    public static final MethodType TYPE_INVOKE_SPECIAL_FROM_BACK = MethodType.methodType(MethodHandle.class, MethodHandles.Lookup.class, String.class, MethodType.class, Object.class);
    public static final MethodType TYPE_INVOKE_INLINED_CALL = MethodType.methodType(MethodHandle.class, MethodHandles.Lookup.class, String.class, MethodType.class, Object.class);


    public static final MethodType BSMS_TYPE = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
    public static final MethodType BSM_NEW = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class);
    private static final MethodType INVOKE_FRONT_TYPE = MethodType.methodType(Object.class, MethodHandles.Lookup.class, MethodHandle.class, Class.class, Object[].class);
    private static final MethodType GET_BACK_FIELD_TYPE = MethodType.methodType(MethodHandle.class, MethodHandles.Lookup.class, Class.class, String.class, Object.class);
    private static final MethodType PUT_BACK_FIELD_TYPE = MethodType.methodType(MethodHandle.class, MethodHandles.Lookup.class, Class.class, String.class, Object.class);
    private static final MethodType INVOKE_CALL_TYPE = MethodType.methodType(Object.class, MethodHandles.Lookup.class, MethodType.class, String.class, Object.class, Object[].class);
    private static final ClassValue<byte[]> BACK_FACTORY = new ClassValue<byte[]>() {
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


    // Getting the lookup.
    private static MethodHandle initLookupConstructor() {
        String PROXY_CLASS = "yv66vgAAADMAGgoABQASBwAUCgACABUHABYHABcBAAY8aW5pdD4BAAMoKVYB"
                + "AARDb2RlAQAPTGluZU51bWJlclRhYmxlAQAGbG9va3VwAQAGTG9va3VwAQAM"
                + "SW5uZXJDbGFzc2VzAQA6KExqYXZhL2xhbmcvQ2xhc3M7KUxqYXZhL2xhbmcv"
                + "aW52b2tlL01ldGhvZEhhbmRsZXMkTG9va3VwOwEACVNpZ25hdHVyZQEAPShM"
                + "amF2YS9sYW5nL0NsYXNzPCo+OylMamF2YS9sYW5nL2ludm9rZS9NZXRob2RI"
                + "YW5kbGVzJExvb2t1cDsBAApTb3VyY2VGaWxlAQAQTG9va3VwUHJveHkuamF2"
                + "YQwABgAHBwAYAQAlamF2YS9sYW5nL2ludm9rZS9NZXRob2RIYW5kbGVzJExv"
                + "b2t1cAwABgAZAQAcamF2YS9sYW5nL2ludm9rZS9Mb29rdXBQcm94eQEAEGph"
                + "dmEvbGFuZy9PYmplY3QBAB5qYXZhL2xhbmcvaW52b2tlL01ldGhvZEhhbmRs"
                + "ZXMBABQoTGphdmEvbGFuZy9DbGFzczspVgAhAAQABQAAAAAAAgABAAYABwAB"
                + "AAgAAAAdAAEAAQAAAAUqtwABsQAAAAEACQAAAAYAAQAAAAUACQAKAA0AAgAI"
                + "AAAAIQADAAEAAAAJuwACWSq3AAOwAAAAAQAJAAAABgABAAAABwAOAAAAAgAP"
                + "AAIAEAAAAAIAEQAMAAAACgABAAIAEwALABk=";
        try {
            byte[] array = MyBase64.getDecoder().decode(PROXY_CLASS);
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            Class<?> proxy = unsafe.defineAnonymousClass(MethodHandles.class, array, null);
            return MethodHandles.lookup().findStatic(proxy, "lookup", MethodType.methodType(Lookup.class, Class.class));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static CallSite bsm_newBackSpecies(MethodHandles.Lookup lookup, String name, MethodType type, String owner, String genericName) throws Throwable {
        // System.out.println("BSM_BACK_SPECIES lookup = [" + lookup + "], name = [" + name + "], type = [" + type + "]" + "], genericName = [" + genericName + "]");
        Class<?> frontClass = ClassLoader.getSystemClassLoader().loadClass(owner);
        // Allocate specialized class - with object for the moment - and return one of its constructor.
        MethodHandle mh = createBackSpecies(lookup, type, frontClass, genericName);
        return new ConstantCallSite(mh);
    }

    public static CallSite bsm_inlinedBackCall(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        // System.out.println("BSM_INLINED_BACK_CALL : lookup = [" + lookup + "], name = [" + name + "], type = [" + type + "]");
        MethodHandle getBackMethod = lookup.findStatic(RT.class, "invokeInlinedCall", TYPE_INVOKE_INLINED_CALL);
        // Setting the lookup, the method type.
        getBackMethod = getBackMethod.bindTo(lookup).bindTo(type).bindTo(name);
        return new ConstantCallSite(createInvoker(type.dropParameterTypes(0, 1), getBackMethod).asType(type));
    }

    public static MethodHandle invokeInlinedCall(MethodHandles.Lookup lookup, MethodType type, String methodName, Object front) throws Throwable {
        System.out.println("invokeInlinedCall : lookup = [" + lookup + "], type = [" + type + "], methodName = [" + methodName + "], front = [" + front.getClass() + "]");
        return lookup.findStatic(getBack__(lookup, front).getClass(), methodName, type).bindTo(front);
    }

    public static CallSite bsm_delegateBackCall(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        // System.out.println("BSM_DELEGATE_CALL : lookup = [" + lookup + "], name = [" + name + "], type = [" + type + "]");
        MethodHandle mh = lookup.findStatic(RT.class, "invokeCall", INVOKE_CALL_TYPE);
        // Dropping name, receiver to have the delegate method type (-2 for front receiver, delegate target name).
        mh = mh.bindTo(lookup).bindTo(type.dropParameterTypes(0, 2));
        // Collecting trailing arguments inside an Object[] array. (- 2 for name, receiver).
        mh = mh.asCollector(Object[].class, type.parameterCount() - 2).asType(type);
        return new ConstantCallSite(mh);
    }

    // TODO directly put the method inside the call site and stop boxing more args and return type.
    public static Object invokeCall(MethodHandles.Lookup lookup, MethodType type, String name, Object receiver, Object... args) throws Throwable {
        // System.out.println("invokeCall : lookup = [" + lookup + "], type = [" + type + "], name = [" + name + "], receiver = [" + receiver + "], args = [" + args + "]");
        MethodHandle mh = lookup.findStatic(receiver.getClass(), name, type).asType(type).asSpreader(Object[].class, args.length);
        return mh.invoke(args);
    }


    /**
     * Called from something else than a back class, and invokes bsm_createAny by passing null to the requested front lookup.
     */
    public static CallSite bsm_createAnyNoLookup(MethodHandles.Lookup lookup, String name, MethodType erasedType,
                                                 String genericName) throws Throwable {
        return bsm_createAny(lookup, name, erasedType, genericName, null);
    }

    /**
     * If called from a back class, the frontLookup has already been bound. Otherwise (called from another class), frontLookup will be equal to null.
     */
    public static CallSite bsm_createAny(MethodHandles.Lookup lookup, String name, MethodType erasedType,
                                         String genericName, MethodHandles.Lookup front) throws Throwable {
        // System.out.println("bsm_createAny : lookup = [" + lookup + "], name = [" + name + "], erasedType = [" + erasedType + "], genericName = [" + genericName + "], front = [" + front + "]");
        MethodHandles.Lookup l = front == null ? lookup : front;
        String rawAnyName = Type.rawName(genericName);
        Class<?> frontClass = l.lookupClass().getClassLoader().loadClass(rawAnyName);
        // Allocate specialized class - with object for the moment - and return one of its constructor.
        MethodHandle backMH = createBackSpecies(front == null ? computeLookup(frontClass) : front, erasedType, frontClass, genericName);
        MethodHandle frontMH = l.findStatic(RT.class, "createFrontInstance", INVOKE_FRONT_TYPE);
        frontMH = frontMH.bindTo(l).bindTo(backMH).bindTo(frontClass).asCollector(Object[].class, erasedType.parameterCount()).asType(erasedType);
        return new ConstantCallSite(frontMH);
    }

    /**
     * Called from something else than a back class, and invokes bsm_getField by passing null to the requested front lookup.
     */
    public static CallSite bsm_getFieldNoLookup(MethodHandles.Lookup lookup, String name, MethodType erasedType, String notErasedDesc)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        return bsm_getField(lookup, name, erasedType, notErasedDesc, null);
    }

    /**
     * If called from a back class, the frontLookup has already been bound. Otherwise (called from another class), frontLookup will be equal to zero.
     */
    public static CallSite bsm_getField(MethodHandles.Lookup lookup, String name, MethodType erasedType,
                                        String notErasedDesc, MethodHandles.Lookup front)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        // System.out.println("BSM_GET_FIELD : lookup = [" + lookup + "], name = [" + name + "], erasedType = [" + erasedType + "], notErasedDesc = [" + notErasedDesc + "], front = [" + front + "]");
        MethodHandles.Lookup l = front == null ? lookup : front;

        // Loading the field class.
        Class<?> fieldClass = erasedType.returnType();
        MethodHandle mh = l.findStatic(RT.class, "getField", TYPE_GETFIELD);
        mh = mh.bindTo(lookup).bindTo(name).bindTo(fieldClass);
        return new ConstantCallSite(createInvoker(erasedType.dropParameterTypes(0, 1), mh));
    }

    public static MethodHandle getField(MethodHandles.Lookup lookup, String name, Class<?> fieldClass, Object front) throws Throwable {
        // System.out.println("getField : lookup = [" + lookup + "], name = [" + name + "], fieldClass = [" + fieldClass + "], front = [" + front + "]");
        Object back__ = getBack__(lookup, front);
        Lookup backLookup = computeLookup(back__.getClass());
        // System.out.println("Back : " + back__);
        return backLookup.findGetter(back__.getClass(), name, fieldClass).bindTo(back__);
    }

    /**
     * Called from something else than a back class, and invokes bsm_putField by passing null to the requested front lookup.
     */
    public static CallSite bsm_putFieldNoLookup(MethodHandles.Lookup lookup, String name, MethodType erasedType, String notErasedDesc)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        return bsm_putField(lookup, name, erasedType, notErasedDesc, null);
    }

    /**
     * If called from a back class, the frontLookup has already been bound. Otherwise (called from another class), frontLookup will be equal to zero.
     */
    public static CallSite bsm_putField(MethodHandles.Lookup lookup, String name, MethodType erasedType,
                                        String notErasedDesc, MethodHandles.Lookup front)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        // System.out.println("bsm_putField : lookup = [" + lookup + "], name = [" + name + "], erasedType = [" + erasedType + "], notErasedDesc = [" + notErasedDesc + "], front = [" + front + "]");
        MethodHandles.Lookup l = front == null ? lookup : front;
        Class<?> fieldClass = erasedType.parameterType(1);
        MethodHandle mh = l.findStatic(RT.class, "putField", TYPE_PUTFIELD);
        mh = mh.bindTo(l).bindTo(name).bindTo(fieldClass);
        return new ConstantCallSite(createInvoker(erasedType.dropParameterTypes(0, 1), mh));
    }

    public static MethodHandle putField(MethodHandles.Lookup lookup, String name, Class<?> fieldClass, Object front) throws Throwable {
        Object back__ = getBack__(lookup, front);
        Lookup backLookup = computeLookup(back__.getClass());
        return backLookup.findSetter(back__.getClass(), name, fieldClass).bindTo(back__);
    }

    /**
     * If called from a back class, the frontLookup has already been bound. Otherwise (called from another class), frontLookup will be equal to zero.
     */
    public static CallSite bsm_invokeSpecialFromBack(MethodHandles.Lookup lookup, String name, MethodType erasedType,
                                                 String notErasedDesc, MethodHandles.Lookup front)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        System.out.println("BSM_INVOKE_SPECIAL_FROM_BACK : lookup = [" + lookup + "], name = [" + name + "], erasedType = [" + erasedType + "], notErasedDesc = [" + notErasedDesc + "], front = [" + front + "]");
        if (front == null) { throw new IllegalStateException("Front lookup can not be null during special invocations from a back class."); }

        MethodHandle mh;
        // Determining if the receiver is a typeVar or a parameterized type.
        Type methodType = Type.getMethodType(notErasedDesc);
        Type receiver = methodType.getArgumentTypes()[0];
        if (Type.isParameterizedType(receiver)) {
            mh = front.findStatic(RT.class, "invokeInlinedCall", TYPE_INVOKE_INLINED_CALL);
            // Removing the receiver parameterized type information.
            notErasedDesc = Type.translateMethodDescriptor(notErasedDesc);
        } else {
            mh = front.findStatic(RT.class, "invokeSpecialFromBack", TYPE_INVOKE_SPECIAL_FROM_BACK);
        }

        MethodType mt = MethodType.fromMethodDescriptorString(notErasedDesc, front.lookupClass().getClassLoader());
        mh = MethodHandles.insertArguments(mh, 0, front, name, mt);
        return new ConstantCallSite(createInvoker(erasedType.dropParameterTypes(0, 1), mh));
    }

    public static MethodHandle invokeSpecialFromBack(Lookup lookup, String method, MethodType type, Object receiver)
            throws NoSuchMethodException, IllegalAccessException {
        System.out.println("invokeSpecialFromBack : lookup = [" + lookup + "], method = [" + method + "], type = [" + type + "], receiver = [" + receiver + "]");
        Class<?> aClass = receiver.getClass();
        return lookup.findSpecial(lookup.lookupClass(), method, type, aClass).bindTo(receiver);
    }

    public static MethodHandle invokeSpecialFromBackOnParameterizedType(Lookup lookup, String method, MethodType type, Object receiver)
            throws NoSuchMethodException, IllegalAccessException {
        Class<?> aClass = receiver.getClass();
        System.out.println("Runtime class : " + aClass);
        return lookup.findSpecial(lookup.lookupClass(), method, type, aClass).bindTo(receiver);
    }

    /*
    public static CallSite bsm_getBackField(MethodHandles.Lookup frontLookup, String name, MethodType type) throws Throwable {
        MethodHandle getterMH = frontLookup.findStatic(RT.class, "getBackField", GET_BACK_FIELD_TYPE);
        getterMH = getterMH.bindTo(frontLookup).bindTo(type.returnType()).bindTo(name);
        return new ConstantCallSite(createInvoker(type.dropParameterTypes(0, 1), getterMH));
    }

    public static CallSite bsm_putBackField(MethodHandles.Lookup lookup, String name, MethodType type) throws Throwable {
        MethodHandle setterMH = lookup.findStatic(RT.class, "putBackField", PUT_BACK_FIELD_TYPE);
        setterMH = setterMH.bindTo(lookup).bindTo(type.parameterType(1)).bindTo(name); // The field type is the parameter 1.
        return new ConstantCallSite(createInvoker(type.dropParameterTypes(0, 1), setterMH));
    }
*/

    /**
     * Invoke the _back__ field of the FrontClass and then invoke the FrontClass by passing it the _back__ field created.
     */
    // TODO remove the args from this signature to remove boxing.
    public static Object createFrontInstance(MethodHandles.Lookup lookup, MethodHandle backConstructor, Class<?> frontClass, Object... args) throws Throwable {
        // System.out.println("createFrontInstance lookup = [" + lookup + "], backConstructor = [" + backConstructor + "], frontClass = [" + frontClass + "], args = [" + args + "]");
        Object back = backConstructor.asSpreader(Object[].class, args.length).invoke(args);
        return lookup.findConstructor(frontClass, SPECIALIZED_CONSTRUCTOR_TYPE).bindTo(null).bindTo(back).invoke();
    }

    /**
     * Creates a {@link MethodHandle} applying the given {@link MethodHandle resolver} - which will find the good _back__ method -
     * to the arguments starting at the position 0 to return a method handle invoked with the remaining arguments.
     */
    private static MethodHandle createInvoker(MethodType type, MethodHandle resolver) {
        MethodHandle target = MethodHandles.exactInvoker(type);
        return MethodHandles.collectArguments(target, 0, resolver);
    }

    public static MethodHandle getBackField(MethodHandles.Lookup lookup, Class<?> fieldType, String name, Object owner)
            throws Throwable {
        Object backField = getBack__(lookup, owner);
        return lookup.findGetter(backField.getClass(), name, fieldType).bindTo(backField);
    }

    public static MethodHandle putBackField(MethodHandles.Lookup lookup, Class<?> fieldType, String name, Object owner)
            throws Throwable {
        Object backField = getBack__(lookup, owner);
        return lookup.findSetter(backField.getClass(), name, fieldType).bindTo(backField);
    }

    private static MethodHandle createBackSpecies(MethodHandles.Lookup frontClassLookup, MethodType type, Class<?> frontClass, String genericName)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        // TODO BUG : Use a value which is a HashMap storing the String specialization and the corresponding class.
        byte[] backCode = BACK_FACTORY.get(frontClass);


        // TODO the first lookup is not the "front lookup" when allocating from a back instance. So we have to pass 2 lookup the normal and the front to the calling methods : bsm_new and bsm_newBackSpecies
        // TODO get the TX types and a classLoader using frontClassLookup.getClass.getClassLoader + Type.parse(genericName) !
        // System.out.println("RT#createBackSpecies : frontClassLookup = [" + frontClassLookup + "], type = [" + type + "], frontClass = [" + frontClass + "]" + " Generic name = [" + genericName + "]");
        // Reading the specialization attributes.
        // TODO store the Substitution table in a couple values for the key class in classValue so it is not read every time. Not needed, because bootstrap are not called too much.
        SubstitutionTable substitutionTable = SubstitutionTableReader.read(backCode);

        // System.out.println("After the substitutionTable : " + substitutionTable);


        // Creating substitution pool.
        String[] classes = Type.getParameterizedTypeValues(genericName);
        // System.out.println("Class : " + Arrays.toString(classes));
        Object[] pool = new Object[substitutionTable.getMax() + 1];
        for (Map.Entry<Integer, Map.Entry<String, String>> descs : substitutionTable.getDescriptors().entrySet()) {
            Integer index = descs.getKey();
            Map.Entry<String, String> ownerAndDescriptor = descs.getValue();
            String owner = ownerAndDescriptor.getKey();
            String descriptor = ownerAndDescriptor.getValue();

            if (!owner.equals(BackClassVisitor.RT_METHOD_HANDLE_TYPE)) {
                pool[index] = Type.specializeDescriptor(descriptor, classes);
                // System.out.println("Index : " + index + "  Value : " + owner + " :: " + pool[index]);
                continue;
            }

            if (descriptor.equals(BackClassVisitor.HANDLE_RT_BSM_NEW)) {
                // Preparing the method handle for the invoke call with Object varargs.
                MethodHandle bsm_createAny = frontClassLookup.findStatic(RT.class, "bsm_createAny", TYPE_BSM_CREATE_ANY);
                pool[index] = MethodHandles.insertArguments(bsm_createAny, 4, frontClassLookup).asSpreader(Object[].class, 4).asType(MethodType.methodType(Object.class, Object[].class));
            } else if (descriptor.equals(BackClassVisitor.HANDLE_RT_BSM_GET_FIELD)) {
                // Preparing the method handle for the invoke call with Object varargs.
                MethodHandle bsm_getField = frontClassLookup.findStatic(RT.class, "bsm_getField", TYPE_BSM_GETFIELD);
                pool[index] = MethodHandles.insertArguments(bsm_getField, 4, frontClassLookup).asSpreader(Object[].class, 4).asType(MethodType.methodType(Object.class, Object[].class));
            } else if (descriptor.equals(BackClassVisitor.HANDLE_RT_BSM_PUT_FIELD)) {
                // Preparing the method handle for the invoke call with Object varargs.
                MethodHandle bsm_putField = frontClassLookup.findStatic(RT.class, "bsm_putField", TYPE_BSM_PUTFIELD);
                pool[index] = MethodHandles.insertArguments(bsm_putField, 4, frontClassLookup).asSpreader(Object[].class, 4).asType(MethodType.methodType(Object.class, Object[].class));
             } else if (descriptor.equals(BackClassVisitor.HANDLE_RT_BSM_INVOKE_SPECIAL_FROM_BACK)) {
                // Preparing the method handle for the invoke call with Object varargs.
                MethodHandle bsm_invokeFromBack = frontClassLookup.findStatic(RT.class, "bsm_invokeSpecialFromBack", TYPE_BSM_INVOKE_SPECIAL_FROM_BACK);
                pool[index] = MethodHandles.insertArguments(bsm_invokeFromBack, 4, frontClassLookup).asSpreader(Object[].class, 4).asType(MethodType.methodType(Object.class, Object[].class));
            } else if (descriptor.equals(BackClassVisitor.HANDLE_RT_METAFACTORY)) {
                // Preparing the method handle for the invoke call with Object varargs.
                MethodHandle bsm_metafactory = frontClassLookup.findStatic(RT.class, "metafactory", TYPE_METAFACTORY);
                // Inserting the frontLookup and dropping the "name" dummy argument received.
                pool[index] = MethodHandles.insertArguments(bsm_metafactory, 3, frontClassLookup).asSpreader(Object[].class, 4).asType(MethodType.methodType(Object.class, Object[].class));
            }
        }

        // Passing Object.class
        Class<?> backClass = UNSAFE.defineAnonymousClass(Object.class, backCode, pool);
        MethodHandle constructor = frontClassLookup.findConstructor(backClass, type.changeReturnType(void.class));
        // We set the front class lookup parameter, and we want to cast the result to an Object instead of the plain anonymous BackClass.
        return constructor.asType(type.changeReturnType(Object.class));
    }


    private static Object getBack__(MethodHandles.Lookup lookup, Object owner) throws Throwable {
        return lookup.findGetter(owner.getClass(), FrontClassVisitor.BACK_FIELD, Object.class).invoke(owner);
    }

    private static Lookup computeLookup(Class<?> type) throws Throwable {
        return (Lookup) LOOKUP_CONSTRUCTOR_MH.invokeExact(type);
    }

    private static Unsafe initUnsafe() {
        try {
            Class<?> unsafeClass = Unsafe.class;
            Field theUnsafe = null;
            theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This bootstrap method returns an adapted callSite to match the virtual method signature defined
     * in the XYZ$any interface.
     */
    public static CallSite metafactory(MethodHandles.Lookup caller, String invokedName,
                                       MethodType invokedType, MethodHandles.Lookup front, String dummy) throws ReflectiveOperationException {
        // System.out.println("metafactory : caller = [" + caller + "], invokedName = [" + invokedName + "], invokedType = [" + invokedType + "], front = [" + front + "], dummy = [" + dummy + "]");
        List<Class<?>> params = invokedType.parameterList();
        if (params.isEmpty()) {
            throw new AssertionError("Missing dynamic parameters!");
        }
        MethodHandle res = /*front*/caller.findStatic(RT.class, invokedName, invokedType);
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
