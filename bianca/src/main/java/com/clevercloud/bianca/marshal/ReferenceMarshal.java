/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 * Copyright (c) 2011-2012 Clever Cloud SAS -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */
package com.clevercloud.bianca.marshal;

import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.expr.Expr;

public class ReferenceMarshal extends Marshal {

   public static final Marshal MARSHAL = new ReferenceMarshal();

   @Override
   public boolean isReadOnly() {
      return false;
   }

   @Override
   public boolean isReference() {
      return true;
   }

   @Override
   public Object marshal(Env env, Expr expr, Class expectedClass) {
      // bianca/0d1k
      return expr.evalRef(env);
   }

   @Override
   public Object marshal(Env env, Value value, Class argClass) {
      return value.toLocalVarDeclAsRef();
   }

   @Override
   public Value unmarshal(Env env, Object value) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected int getMarshalingCostImpl(Value argValue) {
      return Marshal.ZERO;
   }

   @Override
   public Class getExpectedClass() {
      return Value.class;
   }
}