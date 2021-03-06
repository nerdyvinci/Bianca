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
 * @author Marc-Antoine Perennou <Marc-Antoine@Perennou.com>
 */
package com.clevercloud.bianca.env;

import com.clevercloud.bianca.program.JavaClassDef;
import com.clevercloud.vfs.WriteStream;

import java.io.IOException;
import java.io.Serializable;
import java.util.IdentityHashMap;

/**
 * Represents a PHP object which extends a Java value.
 */
public class ObjectExtJavaValue extends ObjectExtValue
   implements Serializable {

   private Object _object;
   private final JavaClassDef _javaClassDef;

   public ObjectExtJavaValue(BiancaClass cl, Object object,
                             JavaClassDef javaClassDef) {
      super(cl);

      _object = object;
      _javaClassDef = javaClassDef;
   }

   public ObjectExtJavaValue(BiancaClass cl,
                             JavaClassDef javaClassDef) {
      super(cl);

      _javaClassDef = javaClassDef;
   }

   //
   // field
   //

   /**
    * Returns fields not explicitly specified by this value.
    */
   @Override
   protected Value getFieldExt(Env env, StringValue name) {
      if (_object == null) {
         _object = createJavaObject(env);
      }

      Value parentValue = super.getFieldExt(env, name);
      if (parentValue != NullValue.NULL && parentValue != UnsetValue.UNSET) {
         return parentValue;
      }

      Value value = _javaClassDef.getField(env, this, name);
      Value biancaValue = _biancaClass.getField(env, this, name);

      if (biancaValue != null && biancaValue != UnsetValue.UNSET && biancaValue != NullValue.NULL) {
         return biancaValue;
      }

      if (value != null) {
         return value;
      } else {
         return super.getFieldExt(env, name);
      }
   }

   /**
    * Sets fields not specified by the value.
    */
   @Override
   protected Value putFieldExt(Env env, StringValue name, Value value) {
      if (_object == null) {
         createJavaObject(env);
      }

      return _javaClassDef.putField(env, this, name, value);
   }

   /**
    * Returns the java object.
    */
   @Override
   public Object toJavaObject() {
      if (_object == null) {
         _object = createJavaObject(Env.getInstance());
      }

      return _object;
   }

   /**
    * Binds a Java object to this object.
    */
   @Override
   public void setJavaObject(Value value) {
      if (_object == null) {
         _object = value.toJavaObject();
      }
   }

   /**
    * Creats a backing Java object for this php object.
    */
   private Object createJavaObject(Env env) {
      Value javaWrapper = _javaClassDef.callNew(env, new Value[0]);
      return javaWrapper.toJavaObject();
   }

   @Override
   public void varDumpImpl(Env env,
                           WriteStream out,
                           int depth,
                           IdentityHashMap<Value, String> valueSet)
      throws IOException {
      if (_object == null) {
         _object = createJavaObject(Env.getInstance());
      }

      if (!_javaClassDef.varDumpImpl(env, this, _object, out, depth, valueSet)) {
         super.varDumpImpl(env, out, depth, valueSet);
      }
   }

   @Override
   protected void printRImpl(Env env,
                             WriteStream out,
                             int depth,
                             IdentityHashMap<Value, String> valueSet)
      throws IOException {
      if (_object == null) {
         _object = createJavaObject(Env.getInstance());
      }

      _javaClassDef.printRImpl(env, _object, out, depth, valueSet);
   }
}
