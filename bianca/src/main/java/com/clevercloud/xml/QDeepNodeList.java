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

package com.clevercloud.xml;

// import com.clevercloud.xpath.pattern.NodeListIterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QDeepNodeList implements NodeList {
   QNode _top;
   QNodePredicate _predicate;
   QAbstractNode _first;
   QAbstractNode _node;
   int _index = -2;
   int _length = -2;
   int _changeCount;

   QDeepNodeList(QNode top, QAbstractNode first, QNodePredicate predicate) {
      _top = top;
      _first = first;
      _predicate = predicate;
   }

   public Node item(int index) {
      QAbstractNode next = _node;

      int i = _index;

      if (next == null || index < i ||
         _changeCount != _top._owner._changeCount) {
         _changeCount = _top._owner._changeCount;
         next = _first;

         if (_predicate != null && next != null && _predicate.isMatch(next))
            i = 0;
         else
            i = -1;
      }

      QAbstractNode end = getEnd();
      while (i < index && next != end) {
         next = next.getNextPreorder();
         if (next != end && _predicate.isMatch(next)) {
            i++;
         }
      }

      if (next == end) {
         next = null;
         i = -1;
      }
      _index = i;
      _node = next;

      return i == _index ? next : null;
   }

   public int getLength() {
      if (_changeCount != _top._owner._changeCount)
         _length = -1;

      if (_length >= 0)
         return _length;

      QAbstractNode end = getEnd();
      _length = 0;
      for (QAbstractNode ptr = _first; ptr != end; ptr = ptr.getNextPreorder()) {
         if (_predicate.isMatch(ptr))
            _length++;
      }

      return _length;
   }

   /**
    * Returns the final node in the preorder.
    */
   QAbstractNode getEnd() {
      QAbstractNode end = _top;

      if (_first == null)
         return null;

      while (end != null && end._next == null)
         end = end._parent;

      return end == null ? null : end._next;
   }

   /*
   // for bianca
   public Iterator<Node> iterator()
   {
     return new NodeListIterator(null, this);
   }
   */
}
