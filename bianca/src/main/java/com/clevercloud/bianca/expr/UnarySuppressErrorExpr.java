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
package com.clevercloud.bianca.expr;

import com.clevercloud.bianca.Location;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.parser.BiancaParser;

import java.io.IOException;

/**
 * Represents a PHP error suppression
 */
public class UnarySuppressErrorExpr extends AbstractUnaryExpr {

   public UnarySuppressErrorExpr(Location location, Expr expr) {
      super(location, expr);
   }

   public UnarySuppressErrorExpr(Expr expr) {
      super(expr);
   }

   /**
    * Creates the assignment.
    */
   @Override
   public Expr createAssign(BiancaParser parser, Expr value)
      throws IOException {
      // php/03j2

      return new UnarySuppressErrorExpr(parser.getLocation(),
         getExpr().createAssign(parser, value));
   }

   /**
    * Creates the assignment.
    */
   @Override
   public Expr createAssignRef(BiancaParser parser,
                               Expr value)
      throws IOException {
      // php/03j2

      return new UnarySuppressErrorExpr(parser.getLocation(),
         getExpr().createAssignRef(parser, value));
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      int oldErrorMask = env.setErrorMask(0);

      try {
         return _expr.eval(env);
      } finally {
         env.setErrorMask(oldErrorMask);
      }
   }

   /**
    * Evaluates the expression as a boolean.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public boolean evalBoolean(Env env) {
      int oldErrorMask = env.setErrorMask(0);

      try {
         return _expr.evalBoolean(env);
      } finally {
         env.setErrorMask(oldErrorMask);
      }
   }

   /**
    * Evaluates the expression as a string.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public String evalString(Env env) {
      int oldErrorMask = env.setErrorMask(0);

      try {
         return _expr.evalString(env);
      } finally {
         env.setErrorMask(oldErrorMask);
      }
   }

   /**
    * Evaluates the expression, copying the results as necessary
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalCopy(Env env) {
      int oldErrorMask = env.setErrorMask(0);

      try {
         return _expr.evalCopy(env);
      } finally {
         env.setErrorMask(oldErrorMask);
      }
   }

   @Override
   public String toString() {
      return "@" + _expr;
   }
}
