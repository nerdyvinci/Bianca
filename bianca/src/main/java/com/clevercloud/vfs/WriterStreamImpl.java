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

package com.clevercloud.vfs;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * Handles a stream which outputs to a writer.
 */
public class WriterStreamImpl extends StreamImpl {
   private static Logger log
      = Logger.getLogger(WriterStreamImpl.class.getName());

   private Writer _writer;
   private boolean _isClosed;
   private String encoding;

   /**
    * Sets the writer.
    */
   public void setWriter(Writer writer) {
      _writer = writer;
      _isClosed = false;

      encoding = null;
   }

   /**
    * Returns true if this is a writable stream.
    */
   public boolean canWrite() {
      return true;
   }

   /**
    * Sets the write encoding.
    */
   public void setWriteEncoding(String encoding) {
      this.encoding = encoding;
   }

   /**
    * Writes a buffer to the underlying stream.
    *
    * @param buffer the byte array to write.
    * @param offset the offset into the byte array.
    * @param length the number of bytes to write.
    * @param isEnd  true when the write is flushing a close.
    */
   public void write(byte[] buffer, int offset, int length, boolean isEnd)
      throws IOException {
      if (_isClosed)
         return;

      _writer.write(new String(buffer, encoding), offset, length);
   }

   /**
    * Flushes the write output.
    */
   public void flush() throws IOException {
   }

   /**
    * Closes the output.
    */
   public void close() {
      _isClosed = true;
   }
}
