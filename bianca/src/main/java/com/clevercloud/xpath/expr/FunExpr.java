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
 *   Free SoftwareFoundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.clevercloud.xpath.expr;

import com.clevercloud.util.CharBuffer;
import com.clevercloud.xpath.Expr;
import com.clevercloud.xpath.ExprEnvironment;
import com.clevercloud.xpath.XPathException;
import com.clevercloud.xpath.XPathFun;
import com.clevercloud.xpath.pattern.AbstractPattern;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Expressions based on custom library extensions.
 */
public class FunExpr extends Expr {
   private String _name;
   private AbstractPattern _pattern;
   private ArrayList<Expr> _args;

   public FunExpr(String name, AbstractPattern pattern, ArrayList<Expr> args) {
      _name = name;
      _pattern = pattern;
      _args = args;
   }

   public boolean evalBoolean(Node node, ExprEnvironment env)
      throws XPathException {
      return toBoolean(evalObject(node, env));
   }

   public double evalNumber(Node node, ExprEnvironment env)
      throws XPathException {
      return toDouble(evalObject(node, env));
   }

   public String evalString(Node node, ExprEnvironment env)
      throws XPathException {
      return toString(evalObject(node, env));
   }

   public Object evalObject(Node node, ExprEnvironment env)
      throws XPathException {
      XPathFun fun = env.getFunction(_name);

      // XXX: need to propagate the exception
      if (fun == null)
         throw new RuntimeException("unknown function: " + _name);

      ArrayList<Object> values = new ArrayList<Object>();
      for (int i = 0; i < _args.size(); i++) {
         Expr expr = _args.get(i);
         values.add(expr.evalObject(node, env));
      }

      return fun.eval(node, env, _pattern, values);
   }

   public String toString() {
      CharBuffer cb = new CharBuffer();
      cb.append(_name);
      cb.append("(");
      for (int i = 0; i < _args.size(); i++) {
         if (i != 0)
            cb.append(", ");
         cb.append(_args.get(i));
      }
      cb.append(")");

      return cb.toString();
   }
}
