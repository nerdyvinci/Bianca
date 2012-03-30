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

package com.clevercloud.xpath.pattern;

import com.clevercloud.xml.CleverCloudElement;
import com.clevercloud.xml.CleverCloudNode;
import com.clevercloud.xml.QAttr;
import com.clevercloud.xml.QElement;
import com.clevercloud.xpath.Env;
import com.clevercloud.xpath.ExprEnvironment;
import com.clevercloud.xpath.XPathException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * matches attributes of an element.
 */
public class FromAttributes extends Axis {
   public FromAttributes(AbstractPattern parent) {
      super(parent);
   }

   /**
    * matches if the node is an attribute.
    *
    * @param node the current node
    * @param env  the variable environment
    * @return true if the pattern matches
    */
   public boolean match(Node node, ExprEnvironment env)
      throws XPathException {
      if (!(node instanceof Attr))
         return false;

      if (node instanceof QAttr
         && ((QAttr) node).getNamespaceURI() == XMLNS)
         return false;

      return _parent == null || _parent.match(node.getParentNode(), env);
   }

   /**
    * The position of the child is the count of previous siblings
    * matching the pattern.
    * <p/>
    * Technically, attributes don't have positions, but our DOM allows it.
    */
   public int position(Node node, Env env, AbstractPattern pattern)
      throws XPathException {
      int count = 1;

      for (node = node.getPreviousSibling();
           node != null;
           node = node.getPreviousSibling()) {
         if (pattern.match(node, env))
            count++;
      }

      return count;
   }

   /**
    * counts all siblings matching the pattern.
    */
   public int count(Node node, Env env, AbstractPattern pattern)
      throws XPathException {
      int count = 0;

      CleverCloudElement parent = (CleverCloudElement) node.getParentNode();

      for (node = parent.getFirstAttribute();
           node != null;
           node = node.getNextSibling()) {
         if (pattern.match(node, env))
            count++;
      }

      return count;
   }

   /**
    * Creates a new node iterator.
    *
    * @param node  the starting node
    * @param env   the variable environment
    * @param match the axis match pattern
    * @return the node iterator
    */
   public NodeIterator createNodeIterator(Node node, ExprEnvironment env,
                                          AbstractPattern match)
      throws XPathException {
      if (node instanceof CleverCloudNode) {
         if (_parent == null)
            return new AttributeIterator(null, this, node, env, match);
         else if (_parent instanceof FromRoot) {
            if (node instanceof Document)
               return new AttributeIterator(null, this, node, env, match);
            else
               return new AttributeIterator(null, this, node.getOwnerDocument(),
                  env, match);
         }

         NodeIterator parentIter;
         parentIter = _parent.createNodeIterator(node, env,
            _parent.copyPosition());

         return new AttributeIterator(parentIter, this, null, env, match);
      }

      if (_parent == null)
         return new AttributeListIterator(null, env, match);
      else if (_parent instanceof FromRoot) {
         if (node instanceof Document)
            return new AttributeListIterator(null, env, match);
         else
            return new AttributeListIterator(null, env, match);
      }

      NodeIterator parentIter;
      parentIter = _parent.createNodeIterator(node, env, _parent.copyPosition());

      return new AttributeListIterator(parentIter, env, match);
   }

   /**
    * Returns the first node in the selection order.
    *
    * @param node the current node
    * @return the first node
    */
   public Node firstNode(Node node, ExprEnvironment env) {
      if (node instanceof QElement)
         return ((QElement) node).getFirstAttribute();
      else
         return null;
   }

   /**
    * Returns the next node in the selection order.
    *
    * @param node     the current node
    * @param lastNode the last node
    * @return the next node
    */
   public Node nextNode(Node node, Node lastNode) {
      return node.getNextSibling();
   }

   /**
    * Returns true if the pattern is strictly ascending.
    */
   public boolean isStrictlyAscending() {
      if (_parent == null)
         return true;
      else
         return _parent.isStrictlyAscending();
   }

   /**
    * Returns true if the two patterns are equal.
    */
   public boolean equals(Object b) {
      if (!(b instanceof FromAttributes))
         return false;

      FromAttributes bPattern = (FromAttributes) b;

      return (_parent == bPattern._parent ||
         (_parent != null && _parent.equals(bPattern._parent)));
   }

   public String toString() {
      return getPrefix() + "attribute::";
   }
}
