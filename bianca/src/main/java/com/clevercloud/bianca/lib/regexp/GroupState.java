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
 * @author Nam Nguyen
 * @author Marc-Antoine Perennou <Marc-Antoine@Perennou.com>
 */
package com.clevercloud.bianca.lib.regexp;

import com.clevercloud.util.IntArray;

/*
 * Represents the state of groups in the regexp.
 */
class GroupState {
   // number of items to store per long

   static final int BIT_WIDTH = 32;
   // maximum number of groups
   static final int MAX_SIZE = 99;
   private long[] _set;
   private IntArray _group;
   private GroupState _freeList;

   public GroupState() {
      int arraySize = MAX_SIZE / BIT_WIDTH;

      if (MAX_SIZE % BIT_WIDTH != 0) {
         arraySize++;
      }

      _set = new long[arraySize];

      _group = new IntArray();
   }

   private GroupState(GroupState src) {
      _set = new long[src._set.length];
      _group = new IntArray();
      _group.add(src._group);
      System.arraycopy(src._set, 0, _set, 0, src._set.length);
   }

   public boolean isMatched(int group) {
      int i = group / BIT_WIDTH;

      if (group > MAX_SIZE) {
         throw new RuntimeException("out of range: " + group + " >= " + MAX_SIZE);
      }

      int shift = group - i * BIT_WIDTH;
      int bit = 1 << shift;

      return (_set[i] & bit) != 0;
   }

   public void setMatched(int group) {
      int i = group / BIT_WIDTH;

      if (group > MAX_SIZE) {
         throw new RuntimeException("out of range: " + group + " >= " + MAX_SIZE);
      }

      int shift = group - i * BIT_WIDTH;
      int bit = 1 << shift;

      _set[i] |= bit;
   }

   public GroupState copy() {
      GroupState state;

      if (_freeList != null) {
         state = _freeList;
         _freeList = _freeList._freeList;
         state._freeList = null;

         state._group.clear();
         state._group.add(_group);
         System.arraycopy(_set, 0, state._set, 0, _set.length);
      } else {
         state = new GroupState(this);
      }

      return state;
   }

   public void free(GroupState state) {
      if (state != null && state != this) {
         state._freeList = _freeList;
         _freeList = state;
      }
   }

   public void clear() {
      _group.clear();

      for (int i = 0; i < _set.length; i++) {
         _set[i] = 0;
      }
   }

   public int size() {
      return _group.size();
   }

   public int get(int i) {
      return _group.get(i);
   }

   public void set(int i, int val) {
      _group.set(i, val);
   }

   public void setLength(int len) {
      _group.setLength(len);
   }
}
