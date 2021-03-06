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
import com.clevercloud.xpath.pattern.NodeArrayListIterator;
import com.clevercloud.xpath.pattern.NodeIterator;
import com.clevercloud.xpath.pattern.NodeListIterator;
import com.clevercloud.xpath.pattern.SingleNodeIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public abstract class Var {
   /**
    * Returns the value as a boolean.
    */
   boolean getBoolean()
      throws XPathException {
      return Expr.toBoolean(getObject());
   }

   /**
    * Returns the value as a double.
    */
   double getDouble()
      throws XPathException {
      Object o = getObject();
      return Expr.toDouble(getObject());
   }

   /**
    * Returns the value as a string.
    */
   String getString()
      throws XPathException {
      return Expr.toString(getObject());
   }

   /**
    * Returns the value as a string.
    */
   void getString(CharBuffer cb)
      throws XPathException {
      cb.append(getString());
   }

   /**
    * Returns the value as a node set.
    */
   NodeIterator getNodeSet(ExprEnvironment env)
      throws XPathException {
      Object obj = getObject();

      if (obj instanceof Node)
         return new SingleNodeIterator(env, (Node) obj);
      else if (obj instanceof NodeList)
         return new NodeListIterator(env, (NodeList) obj);
      else if (obj instanceof ArrayList)
         return new NodeArrayListIterator(env, (ArrayList) obj);
      else
         return new SingleNodeIterator(env, null);
   }

   /**
    * Returns the value as an object.
    */
   abstract Object getObject();

   /**
    * Frees the var.
    */
   public void free() {
   }
}
