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
 * @author Marc-Antoine Perennou <Marc-Antoine@Perennou.com>
 */
package com.clevercloud.bianca.lib.file;

import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.StringValue;

import java.io.IOException;

/**
 * A helper class that handles line endings when reading from a BinaryInput.
 */
public class LineReader {

   private Boolean _isMacLineEnding;

   public LineReader(Env env) {
      this(FileModule.INI_AUTO_DETECT_LINE_ENDINGS.getAsBoolean(env));
   }

   private LineReader(boolean isAutoDetectLineEndings) {
      if (!isAutoDetectLineEndings) {
         _isMacLineEnding = Boolean.FALSE;
      }
   }

   /**
    * Reads a line from the BinaryInput, returning null on EOF.
    */
   public StringValue readLine(Env env, BinaryInput input, long length)
      throws IOException {
      StringBuilder str = new StringBuilder();

      int ch;

      for (; length > 0 && (ch = input.read()) >= 0; length--) {
         // php/161[pq] newlines
         if (ch == '\n') {
            str.append((char) ch);

            if (_isMacLineEnding == null) {
               _isMacLineEnding = false;
            }

            if (!_isMacLineEnding) {
               break;
            }
         } else if (ch == '\r') {
            str.append('\r');

            int ch2 = input.read();

            if (ch2 == '\n') {
               if (_isMacLineEnding == null) {
                  _isMacLineEnding = false;
               }

               if (_isMacLineEnding) {
                  input.unread();
                  break;
               } else {
                  str.append('\n');
                  break;
               }
            } else {
               input.unread();

               if (_isMacLineEnding == null) {
                  _isMacLineEnding = true;
               }

               if (_isMacLineEnding) {
                  return new StringValue(str.toString());
               }
            }

         } else {
            str.append((char) ch);
         }
      }

      if (str.length() == 0) {
         return null;
      } else {
         return new StringValue(str.toString());
      }

   }
}
