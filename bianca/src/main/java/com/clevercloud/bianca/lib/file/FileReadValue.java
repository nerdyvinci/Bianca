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
package com.clevercloud.bianca.lib.file;

import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.StringValue;
import com.clevercloud.vfs.Path;
import com.clevercloud.vfs.ReadStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Bianca open file
 */
public class FileReadValue extends FileValue {

   private static final Logger log = Logger.getLogger(FileReadValue.class.getName());
   private ReadStream _is;
   private long _offset;

   public FileReadValue(Path path)
      throws IOException {
      super(path);

      _is = path.openRead();
   }

   /**
    * Returns the number of bytes available to be read, 0 if no known.
    */
   public long getLength() {
      return getPath().getLength();
   }

   /**
    * Reads a character from a file, returning -1 on EOF.
    */
   @Override
   public int read()
      throws IOException {
      if (_is != null) {
         int v = _is.read();

         if (v >= 0) {
            _offset++;
         } else {
            close();
         }

         return v;
      } else {
         return -1;
      }
   }

   /**
    * Reads a buffer from a file, returning -1 on EOF.
    */
   @Override
   public int read(byte[] buffer, int offset, int length)
      throws IOException {
      if (_is != null) {
         int len = _is.read(buffer, offset, length);

         if (len >= 0) {
            _offset += len;
         } else {
            close();
         }

         return len;
      } else {
         return -1;
      }
   }

   /**
    * Reads the optional linefeed character from a \r\n
    */
   @Override
   public boolean readOptionalLinefeed()
      throws IOException {
      if (_is != null) {
         int ch = _is.read();

         if (ch == '\n') {
            _offset++;
            return true;
         } else {
            _is.unread();
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void writeToStream(OutputStream os, int length)
      throws IOException {
      if (_is != null) {
         _is.writeToStream(os, length);
      }
   }

   /**
    * Reads a line from a file, returning null on EOF.
    */
   @Override
   public StringValue readLine(Env env)
      throws IOException {
      // TODO: offset messed up
      if (_is != null) {
         return env.createString(_is.readLineNoChop());
      } else {
         return null;
      }
   }

   /**
    * Returns true on the EOF.
    */
   @Override
   public boolean isEOF() {
      if (_is == null) {
         return true;
      } else {
         try {
            // TODO: not quite right for sockets
            return _is.available() <= 0;
         } catch (IOException e) {
            log.log(Level.FINE, e.toString(), e);

            return true;
         }
      }
   }

   /**
    * Returns the current location in the file.
    */
   @Override
   public long getPosition() {
      if (_is == null) {
         return -1;
      } else {
         return _is.getPosition();
      }
   }

   /**
    * Closes the file.
    */
   @Override
   public void close() {
      ReadStream is = _is;
      _is = null;

      if (is != null) {
         is.close();
      }
   }

   /**
    * Converts to a string.
    *
    * @param env
    */
   @Override
   public String toString() {
      return "File[" + getPath() + "]";
   }
}
