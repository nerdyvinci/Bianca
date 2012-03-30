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
 * @author Sam
 */
package com.clevercloud.bianca.lib.spl;

import com.clevercloud.bianca.annotation.Name;
import com.clevercloud.bianca.annotation.Optional;
import com.clevercloud.bianca.annotation.This;
import com.clevercloud.bianca.env.*;
import com.clevercloud.bianca.lib.ArrayModule;
import com.clevercloud.vfs.WriteStream;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

public class ArrayIterator
   implements SeekableIterator,
   ArrayAccess,
   Countable {

   public static final int STD_PROP_LIST = 0x00000001;
   public static final int ARRAY_AS_PROPS = 0x00000002;
   private static final StringValue _rewind = MethodIntern.intern("rewind");
   private Env _env;
   private Value _qThis;
   private Value _value = NullValue.NULL;
   private int _flags;
   private java.util.Iterator<Map.Entry<Value, Value>> _iterator;
   private Map.Entry<Value, Value> _current;

   @Name("__construct")
   public Value __construct(Env env,
                            @This Value qThis,
                            @Optional Value value,
                            @Optional int flags) {

      _env = env;
      _qThis = qThis;

      if (value == null) {
         value = NullValue.NULL;
      }

      _value = value;
      _flags = flags;

      return qThis;
   }

   public void append(Value value) {
      _value.put(value);
   }

   public void asort(ArrayValue array, @Optional long sortFlag) {
      sortFlag = 0; // qa/4a46

      if (_value instanceof ArrayValue) {
         ArrayModule.asort(_env, (ArrayValue) _value, sortFlag);
      }
   }

   @Override
   public int count() {
      return _value.getCount(_env);
   }

   @Override
   public Value current(Env env) {
      if (_iterator == null) {
         rewindJava(env);
      }

      return _current == null ? UnsetValue.UNSET : _current.getValue();
   }

   public Value getArrayCopy() {
      return _value.copy();
   }

   public int getFlags() {
      return _flags;
   }

   @Override
   public Value key(Env env) {
      if (_iterator == null) {
         rewindJava(env);
      }

      return _current == null ? UnsetValue.UNSET : _current.getKey();
   }

   public void ksort(@Optional long sortFlag) {
      if (_value instanceof ArrayValue) {
         ArrayModule.ksort(_env, (ArrayValue) _value, sortFlag);
      }
   }

   public void natcasesort() {
      if (_value instanceof ArrayValue) {
         ArrayModule.natcasesort(_env, _value);
      }
   }

   public void natsort() {
      if (_value instanceof ArrayValue) {
         ArrayModule.natsort(_env, _value);
      }
   }

   @Override
   public void next(Env env) {
      if (_iterator == null) {
         rewind();
      }

      if (_iterator.hasNext()) {
         _current = _iterator.next();
      } else {
         _current = null;
      }
   }

   @Override
   public boolean offsetExists(Value offset) {
      return _value.get(offset).isset();
   }

   @Override
   public Value offsetGet(Value offset) {
      return _value.get(offset);
   }

   @Override
   public Value offsetSet(Value offset, Value value) {
      return _value.put(offset, value);
   }

   @Override
   public Value offsetUnset(Value offset) {
      return _value.remove(offset);
   }

   public void rewindJava(Env env) {
      if (_qThis != null) {
         _qThis.callMethod(env, _rewind);
      } else {
         rewind();
      }
   }

   @Override
   public void rewind() {
      // php/4as8
      _iterator = _value.getBaseIterator(_env);

      if (_iterator.hasNext()) {
         _current = _iterator.next();
      } else {
         _current = null;
      }
   }

   public void setFlags(Value flags) {
      _flags = flags.toInt();
   }

   @Override
   public void seek(Env env, int index) {
      rewindJava(env);

      for (int i = 0; i < index; i++) {
         if (!_iterator.hasNext()) {
            _current = null;
            break;
         }

         _current = _iterator.next();
      }
   }

   public void uasort(Callback func, @Optional long sortFlag) {
      if (_value instanceof ArrayValue) {
         ArrayModule.uasort(_env, (ArrayValue) _value, func, sortFlag);
      }
   }

   public void uksort(Callback func, @Optional long sortFlag) {
      if (_value instanceof ArrayValue) {
         ArrayModule.uksort(_env, (ArrayValue) _value, func, sortFlag);
      }
   }

   @Override
   public boolean valid() {
      if (_iterator == null) {
         rewind();
      }

      return _current != null;
   }

   private static void printDepth(WriteStream out, int depth)
      throws java.io.IOException {
      for (int i = depth; i > 0; i--) {
         out.print(' ');
      }
   }

   public void varDumpImpl(Env env,
                           Value obj,
                           WriteStream out,
                           int depth,
                           IdentityHashMap<Value, String> valueSet)
      throws IOException {
      String name = "ArrayIterator";

      if (obj != null) {
         name = obj.getClassName();
      }

      if ((_flags & STD_PROP_LIST) != 0) {
         // TODO:  env.getThis().varDumpObject(env, out, depth, valueSet);
      } else {
         Value arrayValue = _value;

         out.println("object(" + name + ") (" + arrayValue.getCount(env) + ") {");

         depth++;

         java.util.Iterator<Map.Entry<Value, Value>> iterator = arrayValue.getIterator(env);

         while (iterator.hasNext()) {
            Map.Entry<Value, Value> entry = iterator.next();

            Value key = entry.getKey();
            Value value = entry.getValue();

            printDepth(out, 2 * depth);

            out.print("[");

            if (key.isString()) {
               out.print("\"" + key + "\"");
            } else {
               out.print(key);
            }

            out.println("]=>");

            printDepth(out, 2 * depth);

            value.varDump(env, out, depth, valueSet);

            out.println();
         }

         depth--;

         printDepth(out, 2 * depth);

         out.print("}");
      }
   }
}
