/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 * Copyright (c) 2011-2012 Clever Cloud SAS -- all rights reserved
 *
 * This file is part of Bianca(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Bianca Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Bianca Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bianca Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */
package com.clevercloud.bianca.function;

import com.clevercloud.bianca.Location;
import com.clevercloud.bianca.env.BiancaClass;
import com.clevercloud.bianca.env.Callback;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.expr.Expr;
import com.clevercloud.bianca.program.Arg;
import com.clevercloud.bianca.program.ClassDef;
import com.clevercloud.bianca.program.Visibility;
import com.clevercloud.util.L10N;

/**
 * Represents a function
 */
@SuppressWarnings("serial")
abstract public class AbstractFunction extends Callback {

   private static final L10N L = new L10N(AbstractFunction.class);
   private static final Arg[] NULL_ARGS = new Arg[0];
   private static final Value[] NULL_ARG_VALUES = new Value[0];
   private final Location _location;
   private boolean _isGlobal = true;
   protected boolean _isStatic = false;
   protected boolean _isFinal = false;
   protected boolean _isConstructor = false;
   protected boolean _isClosure = false;
   protected Visibility _visibility = Visibility.PUBLIC;
   protected String _declaringClassName;
   protected BiancaClass _bindingClass;
   protected int _parseIndex;

   public AbstractFunction() {
      // TODO:
      _location = Location.UNKNOWN;
   }

   public AbstractFunction(Location location) {
      _location = location;
   }

   public String getName() {
      return "unknown";
   }

   //
   // Callback values
   //
   @Override
   public String getCallbackName() {
      return getName();
   }

   @Override
   public boolean isInternal(Env env) {
      return false;
   }

   @Override
   public boolean isValid(Env env) {
      return true;
   }

   public final String getCompilationName() {
      String compName = getName() + "_" + _parseIndex;

      compName = compName.replace("__", "___");
      compName = compName.replace("\\", "__");

      return compName;
   }

   /*
    * Returns the name of class lexically declaring the method
    */
   public String getDeclaringClassName() {
      return _declaringClassName;
   }

   public void setDeclaringClassName(String name) {
      _declaringClassName = name;
   }

   /*
    * Returns the name of class lexically binding the method
    */
   public String getBindingClassName() {
      if (_bindingClass != null) {
         return _bindingClass.getName();
      } else {
         return "<none>";
      }
   }

   public void setBindingClass(BiancaClass qcl) {
      _bindingClass = qcl;
   }

   public BiancaClass getBindingClass() {
      return _bindingClass;
   }

   /*
    * Returns the implementing class.
    */
   public ClassDef getDeclaringClass() {
      return null;
   }

   /**
    * Returns true for a global function.
    */
   public final boolean isGlobal() {
      return _isGlobal;
   }

   /**
    * Returns true for an abstract function.
    */
   public boolean isAbstract() {
      return false;
   }

   /**
    * Sets true if function is static.
    */
   public void setStatic(boolean isStatic) {
      _isStatic = isStatic;
   }

   /**
    * Returns true for a static function.
    */
   public boolean isStatic() {
      return _isStatic;
   }

   /*
    * Returns true for a final function.
    */
   public boolean isFinal() {
      return _isFinal;
   }

   public final void setFinal(boolean isFinal) {
      _isFinal = isFinal;
   }

   /**
    * Sets true if function is a closure.
    */
   public void setClosure(boolean isClosure) {
      _isClosure = isClosure;
   }

   /**
    * Returns true for a closure.
    */
   public boolean isClosure() {
      return _isClosure;
   }

   public boolean isConstructor() {
      return _isConstructor;
   }

   public final void setConstructor(boolean isConstructor) {
      _isConstructor = isConstructor;
   }

   /*
    * Returns true for a protected function.
    */
   public boolean isPublic() {
      return _visibility == Visibility.PUBLIC;
   }

   /*
    * Returns true for a protected function.
    */
   public boolean isProtected() {
      return _visibility == Visibility.PROTECTED;
   }

   /*
    * Returns true for a private function.
    */
   public boolean isPrivate() {
      return _visibility == Visibility.PRIVATE;
   }

   public final void setVisibility(Visibility v) {
      _visibility = v;
   }

   public final void setParseIndex(int index) {
      _parseIndex = index;
   }

   public final Location getLocation() {
      return _location;
   }

   /**
    * Returns true for a global function.
    */
   public final void setGlobal(boolean isGlobal) {
      _isGlobal = isGlobal;
   }

   /**
    * Returns true for a boolean function.
    */
   @Override
   public boolean isBoolean() {
      return false;
   }

   /**
    * Returns true for a string function.
    */
   @Override
   public boolean isString() {
      return false;
   }

   /**
    * Returns true for a long function.
    */
   @Override
   public boolean isLong() {
      return false;
   }

   /**
    * Returns true for a double function.
    */
   @Override
   public boolean isDouble() {
      return false;
   }

   /**
    * Returns true if the function uses variable args.
    */
   public boolean isCallUsesVariableArgs() {
      return false;
   }

   /**
    * Returns true if the function uses/modifies the local symbol table
    */
   public boolean isCallUsesSymbolTable() {
      return false;
   }

   /**
    * True for a returns reference.
    */
   public boolean isReturnsReference() {
      return true;
   }

   /**
    * Returns the args.
    */
   public Arg[] getArgs() {
      return NULL_ARGS;
   }

   /**
    * For lazy functions, returns the actual function
    */
   public AbstractFunction toFun() {
      return this;
   }

   /**
    * Returns the actual function
    */
   public AbstractFunction getActualFunction(Expr[] args) {
      return this;
   }

   /**
    * Returns the documentation for this function.
    */
   public String getComment() {
      return null;
   }

   /**
    * Binds the user's arguments to the actual arguments.
    *
    * @param args the user's arguments
    * @return the user arguments augmented by any defaults
    */
   public Value[] evalArguments(Env env, Expr fun, Expr[] args) {
      Value[] values = new Value[args.length];

      for (int i = 0; i < args.length; i++) {
         values[i] = args[i].evalArg(env, true);
      }

      return values;
   }

   //
   // Value methods
   //
   //
   // Value predicates
   //

   /**
    * Returns true for an object
    */
   @Override
   public boolean isObject() {
      return true;
   }

   @Override
   public String getType() {
      return "object";
   }

   /**
    * Evaluates the function.
    */
   @Override
   abstract public Value call(Env env, Value[] args);

   /**
    * Evaluates the function, returning a reference.
    */
   @Override
   public Value callRef(Env env, Value[] args) {
      return call(env, args);
   }

   /**
    * Evaluates the function, returning a copy
    */
   @Override
   public Value callCopy(Env env, Value[] args) {
      return call(env, args).copyReturn();
   }

   /**
    * Evaluates the function.
    */
   @Override
   public Value call(Env env) {
      return call(env, NULL_ARG_VALUES);
   }

   /**
    * Evaluates the function with an argument .
    */
   @Override
   public Value call(Env env, Value a1) {
      return call(env, new Value[]{a1});
   }

   /**
    * Evaluates the function with arguments
    */
   @Override
   public Value call(Env env, Value a1, Value a2) {
      return call(env, new Value[]{a1, a2});
   }

   /**
    * Evaluates the function with arguments
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3) {
      return call(env, new Value[]{a1, a2, a3});
   }

   /**
    * Evaluates the function with arguments
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3, Value a4) {
      return call(env, new Value[]{a1, a2, a3, a4});
   }

   /**
    * Evaluates the function with arguments
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3, Value a4, Value a5) {
      return call(env, new Value[]{a1, a2, a3, a4, a5});
   }

   /**
    * Evaluates the function.
    */
   @Override
   public Value callRef(Env env) {
      return callRef(env, NULL_ARG_VALUES);
   }

   /**
    * Evaluates the function with an argument .
    */
   @Override
   public Value callRef(Env env, Value a1) {
      return callRef(env, new Value[]{a1});
   }

   /**
    * Evaluates the function with arguments
    */
   @Override
   public Value callRef(Env env, Value a1, Value a2) {
      return callRef(env, new Value[]{a1, a2});
   }

   /**
    * Evaluates the function with arguments
    */
   @Override
   public Value callRef(Env env, Value a1, Value a2, Value a3) {
      return callRef(env, new Value[]{a1, a2, a3});
   }

   /**
    * Evaluates the function with arguments
    */
   @Override
   public Value callRef(Env env, Value a1, Value a2, Value a3, Value a4) {
      return callRef(env, new Value[]{a1, a2, a3, a4});
   }

   /**
    * Evaluates the function with arguments
    */
   @Override
   public Value callRef(Env env,
                        Value a1, Value a2, Value a3, Value a4, Value a5) {
      return callRef(env, new Value[]{a1, a2, a3, a4, a5});
   }

   //
   // method calls
   //

   /**
    * Evaluates the method call.
    */
   public Value callMethod(Env env,
                           BiancaClass qClass,
                           Value qThis,
                           Value[] args) {
      throw new IllegalStateException(getClass().getName());

      /*
      Value oldThis = env.setThis(qThis);
      BiancaClass oldClass = env.setCallingClass(qClass);

      try {
      return call(env, args);
      } finally {
      env.setThis(oldThis);
      env.setCallingClass(oldClass);
      }
       */
   }

   /**
    * Evaluates the method call, returning a reference.
    */
   public Value callMethodRef(Env env,
                              BiancaClass qClass,
                              Value qThis,
                              Value[] args) {
      throw new IllegalStateException(getClass().getName());

      /*
      Value oldThis = env.setThis(qThis);
      BiancaClass oldClass = env.setCallingClass(qClass);

      try {
      return callRef(env, args);
      } finally {
      env.setThis(oldThis);
      env.setCallingClass(oldClass);
      }
       */
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethod(Env env,
                           BiancaClass qClass,
                           Value qThis) {
      return callMethod(env, qClass, qThis, NULL_ARG_VALUES);
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethodRef(Env env,
                              BiancaClass qClass,
                              Value qThis) {
      return callMethodRef(env, qClass, qThis, NULL_ARG_VALUES);
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethod(Env env,
                           BiancaClass qClass,
                           Value qThis,
                           Value a1) {
      return callMethod(env, qClass, qThis,
         new Value[]{a1});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethodRef(Env env,
                              BiancaClass qClass,
                              Value qThis,
                              Value a1) {
      return callMethodRef(env, qClass, qThis,
         new Value[]{a1});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethod(Env env,
                           BiancaClass qClass,
                           Value qThis,
                           Value a1, Value a2) {
      return callMethod(env, qClass, qThis,
         new Value[]{a1, a2});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethodRef(Env env,
                              BiancaClass qClass,
                              Value qThis,
                              Value a1, Value a2) {
      return callMethodRef(env, qClass, qThis,
         new Value[]{a1, a2});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethod(Env env,
                           BiancaClass qClass,
                           Value qThis,
                           Value a1, Value a2, Value a3) {
      return callMethod(env, qClass, qThis,
         new Value[]{a1, a2, a3});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethodRef(Env env,
                              BiancaClass qClass,
                              Value qThis,
                              Value a1, Value a2, Value a3) {
      return callMethodRef(env, qClass, qThis,
         new Value[]{a1, a2, a3});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethod(Env env,
                           BiancaClass qClass,
                           Value qThis,
                           Value a1, Value a2, Value a3, Value a4) {
      return callMethod(env, qClass, qThis,
         new Value[]{a1, a2, a3, a4});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethodRef(Env env,
                              BiancaClass qClass,
                              Value qThis,
                              Value a1, Value a2, Value a3, Value a4) {
      return callMethodRef(env, qClass, qThis,
         new Value[]{a1, a2, a3, a4});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethod(Env env,
                           BiancaClass qClass,
                           Value qThis,
                           Value a1, Value a2, Value a3, Value a4, Value a5) {
      return callMethod(env, qClass, qThis,
         new Value[]{a1, a2, a3, a4, a5});
   }

   /**
    * Evaluates the function as a method call.
    */
   public Value callMethodRef(Env env,
                              BiancaClass qClass,
                              Value qThis,
                              Value a1, Value a2, Value a3, Value a4, Value a5) {
      return callMethodRef(env, qClass, qThis,
         new Value[]{a1, a2, a3, a4, a5});
   }

   /**
    * Evaluates the function.
    */
   public Value callMethod(Env env,
                           BiancaClass qClass,
                           Value qThis,
                           Expr[] exprs) {
      Value[] argValues = new Value[exprs.length];
      Arg[] args = getArgs();

      for (int i = 0; i < exprs.length; i++) {
         if (i < args.length && args[i].isReference()) {
            argValues[i] = exprs[i].evalArg(env, true);
         } else {
            argValues[i] = exprs[i].eval(env);
         }
      }

      return callMethod(env, qClass, qThis, argValues);
   }

   /**
    * Evaluates the function.
    */
   public Value callMethodRef(Env env,
                              BiancaClass qClass,
                              Value qThis,
                              Expr[] exprs) {
      Value[] argValues = new Value[exprs.length];
      Arg[] args = getArgs();

      for (int i = 0; i < exprs.length; i++) {
         if (i < args.length && args[i].isReference()) {
            argValues[i] = exprs[i].evalArg(env, true);
         } else {
            argValues[i] = exprs[i].eval(env);
         }
      }

      return callMethodRef(env, qClass, qThis, argValues);
   }

   protected Value errorProtectedAccess(Env env, Value oldThis) {
      return env.error(L.l(
         "Cannot call protected method {0}::{1}() from '{2}' context",
         getDeclaringClassName(),
         getName(),
         oldThis != null ? oldThis.getClassName() : null));
   }

   protected Value errorPrivateAccess(Env env, Value oldThis) {
      return env.error(L.l(
         "Cannot call private method {0}::{1}() from '{2}' context",
         getDeclaringClassName(),
         getName(),
         oldThis != null ? oldThis.getClassName() : null));
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + "[" + getName() + "]";
   }
}
