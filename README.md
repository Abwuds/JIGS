# JIGS
Java Internship on Generic Specialization.

My goal is to implement a solution allowing the backport of generic specialization from JAVA 10 to 8 (or even 7).

## 3 Main productions :
1. A specification of our solution.
2. A program capable to transform JAVA 10 classes into JAVA 8 ones.
3. A program to specialize the resulting code at runtime (by using BSM and InvokeDynamic, to perform the specialization).

## Weekly planning
# Weekly planning

| Week        | Task           |
| ------------- |:-------------:|
| 9 - 13 May      | Finish the first version of the specification  |
| 16 - 20 May      | Modificate the ASM code to handle the last version of Valhalla Model 3|
| 23 - 27 May | Modificate the ASM code to handle the last version of Valhalla Model 3 |
| 30 - 3 June      | Implement the specification |
| 6 - 10 June | Implement the specification      |
| 13 - 17 June | Apply UT      |
| 20 - 24 June | Pass all UT      |
--------------------------------------------------------------

## Internship road map
1. ~~[Read Interoperation between Miniboxing and other Generics Translations](http://infoscience.epfl.ch/record/210236/files/Thesis%20Report%20%28Milos%20Stojanovic%29_1.pdf)~~
2. ~~Write the Road map and Internship's context documents~~
3. ~~[Watch Brian Goetz on Generic Specialization](https://www.youtube.com/watch?v=TkpcuL1t1lY)~~
4. ~~Translate everything on a new github repository~~
5. ~~[Read Compiling Generics Through User-Directed TypeSpecialization](http://infoscience.epfl.ch/record/150134/files/p42-dragos.pdf)~~
 5. ~~[Read State of the specialization](http://cr.openjdk.java.net/~briangoetz/valhalla/specialization.html)~~
 5. Write resume of the *State of the specialization* document
6. ~~Download the source code from Valhalla's workspace (using Mercurial)~~
7. ~~Create an image of the JDK~~
8. Study the ORACLE's syntax
 8. Read the Valhalla source code
 9. Write a complete description
9. Resolve the question : " How can we treat the problem ? "
X . Patch ASM into JAVA 10 to read ORACLE's sources.
