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
package com.clevercloud.bianca.statement;

import com.clevercloud.bianca.Location;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.program.InterpretedClassDef;
import com.clevercloud.util.L10N;

/**
 * Represents a class definition
 */
public class ClassDefStatement extends Statement {

   private final static L10N L = new L10N(ClassDefStatement.class);
   protected final InterpretedClassDef _cl;

   public ClassDefStatement(Location location, InterpretedClassDef cl) {
      super(location);

      _cl = cl;
   }

   @Override
   public Value execute(Env env) {
      env.addClass(_cl.getName(), _cl);

      return null;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + "[" + _cl + "]";
   }
}
