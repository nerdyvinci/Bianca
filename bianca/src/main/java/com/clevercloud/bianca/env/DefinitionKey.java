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
package com.clevercloud.bianca.env;

import com.clevercloud.bianca.page.BiancaPage;

import java.lang.ref.WeakReference;

/**
 * Key for caching function definitions
 */
public final class DefinitionKey {
   // crc of the current definition

   private final long _crc;
   // the including page
   private final WeakReference<BiancaPage> _includePageRef;

   DefinitionKey(long crc, BiancaPage includePage) {
      _crc = crc;
      _includePageRef = new WeakReference<BiancaPage>(includePage);
   }

   @Override
   public int hashCode() {
      return (int) _crc;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof DefinitionKey)) {
         return false;
      }

      DefinitionKey key = (DefinitionKey) o;

      BiancaPage page = _includePageRef.get();
      BiancaPage keyPage = key._includePageRef.get();

      if (page == null || keyPage == null) {
         return false;
      }

      return (_crc == key._crc && page.equals(keyPage));
   }

   @Override
   public String toString() {
      BiancaPage page = _includePageRef.get();

      return "DefinitionKey[" + _crc + ", " + page + "]";
   }
}
