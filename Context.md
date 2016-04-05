# Goal : Generic specialization, from JAVA 10 to JAVA 8

* What we don't care about : The syntax.
* What is interesting for us : How are we going to implement it.

Actually nearly everything is planned at ORACLE for JAVA 10. <br>
But they plan to modificate the JVM's specification.

### Main reasons why we think this is bad (in fact only 1) :
We usually want to specialize only a restricted part of our program, not the whole structure.

### Motivations : Why do we want to specialize with primitive types ?
Currently, everything is reference. This means that when we want to walk across a structure (collection), we have 2 extra costs :

1. A memory cost, it implies memory allocations which finally has impacts on performances resulting.
2. An access cost, it implies indirections to be done, because we loose the data locality in case we have lot of data. Our operations don't benefit from cache systems.

### Compile time or Runtime, where do we stand ?
At runtime. 
#### Because Compile time has several issues :
* Handling every specialization implies in a cartesian product for every type parameter involved.
* Bigger JAR.
* Slows the execution because of the byte code verification (30% of the global startup time).
* We will certainly have to pay this cost anyway, but runtime computations will allow us to perform factorizations.

#### Runtime advantages :
* We only pay what is necessary.
* We plan to perform only 1 verification (at least the least possible)
* We want to avoid naive generations, otherwise we will end up with the same verification times as in compile time situations.
* We have less types to handle. Only 3 types : 32 bits primitive values, 64 bits primitive values and references.
 * We will only consider storing cases.
   * Note : We don't know pointers' sizes. They depend on the architecture's characteristics (RAM).
* At runtime, only integer on the stack (char & booleans have been promoted) which is also less restrictive.

### Notes on verification phase :

We could do this only once if we know that a subset of a specific set of method is used. This specific set of methods on Objects, has to represent methods which can be used with primitive types. If a method which does not belong to this very set is used, we know that we will not be able to use primitive types.

### Notes on primitive types sizes under the JVM :

* Java specifications : Every integer on 32 bits, but we don't know if they are always on 32 bits and not 64 bits.
* We have to know how to switch between 64 bits float and 64 bits integers without any transformations.
 * We will have to use something like `Double#doubleToRawLongBits`
 * Double\#Nan has many different representations. One of the methods `Double#doubleToRawLongBits` unified them, do not use this method.

### Solutions to the problematic

#### Oracle's solution

* Bytecode - We already have holes in Bytecode thanks to the dictionary. We can use this mechanism to special the generic code.

* We could patch the dictionary. This would allow us to specialize :
 * Method calls
 * Fields access

* But, not local variables on the stack, which are not accessible at all with this technic.
* Neither arrays of primitive types (which are difference from array of Objects. Their only common parent is Object).
* This is the way ORACLE is implementing generic specialization.
* This solution implies modifications of the JAVA Specification.

#### Why do we think that this is a problem ?
* Some languages like Kotlin, or Android and Scala to a lesser extent are using Java 6 Bytecode, or similar.
* We want them to be able to use the generics specialization, without breaking their code.

#### Our solutions : 
Many possibilities, we have to answer to these questions first : 
#### Question 1 :
Do we use the same HashMap, the same mold ? Or two versions, one for Objects and another for primitive types ?
We could have one Map derived into map of primitive types thanks to annotations.

#### Question 2 :
How to represent operations on local variables ?
First solution is not pretty and does not work everywhere, but works in practice :
```java
if (class == int.class) { iload }
else { aload }
```
Second solution is to duplicate parameters and local variables (preferred one for the moment) :
```java
f(int a, E e) =transformed> f(int a, int e1, long e2, Object e)
```
It implies an increasing amount of code (3 parameters for each type parameter), which is not totally a problem, since it might be well handled by the JIT. And some invoke dynamic manipulations.

#### Question 2.1 :
How does it work with arrays, fields, and method calls ?
```java
class A <any E> {
  E e;
}
```
.class modificated/compatible with Java 10\. We want compatibility with Java 8 or even 7 + some attributs.

#### Question 3 :
How are specificic operations represented ?
`f1 == f2 ≠ d1 == d2 ≠ {i1 == i2 || r1 == r2}` equality is specfic to types.

#### Question 4 :
How do we handle parameter types bound by an interface ? Do we allow it ?
```java
class A <any T implements Comparable<T> {
  A a;
  ...
  a.compare(b) < 0
  a < b ? // Is it the comparison on integer ?
}
```

#### Question 5 :
How do we represent "?" in the case we accept primitive types ?
With a symbol called *star* *.

#### Question 6 :
How do we represent method parameters ? `[static] <any T> List asList(T... args)` 
note : [static] is more easy to handle.

#### Question 6.1 :
Do we have to create one class per method ? Because there are not in the constant pool (aka dictionary). To study.

## 3 Main productions :
1. A specification of our solution.
2. A program capable to transform JAVA 10 classes into JAVA 8 ones.
3. A program to specialize the resulting code at runtime (by using some BSM and InvokeDynamic to specialize).
