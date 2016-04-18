# Overview of specialized classfile format
_By Maurizio Cimadamore, October 2014, version 0.2_

1. Actual numbers don't matter, just that it's a number
  1. Ordered sub-list

**Context** : Shows the enhancements to the classfile format that are required in order to support
type-specialization. 
<br>As described in State of the specialization : classfile format in its current state, does
not preserve enough type information to allow specialization of generic classes at runtime.
<br>**Goal** : Define a relatively mechanical on-demand class specialization process.
<br>**How** : The valhalla `javac` compiler might _decorate a specializable_ class with additional 
information like bytecode attributes presented below.

## The `TypeVariablesMap` attribute
First thing a specializer runtime might need to know :

1. Which type-variables have been marked with the special modifier `any` in the corresponding source code
  1. Source code is subject to type-erasure, information involving type-parameters is lost `any`type-variable is turned into an ordinary type-variable whose bound is `Object`.
  1. Resolution : Definition of a bytecode attribute, namely `TypeVariablesMap`, which stores all source-related flags asociated with any given type-variable.
