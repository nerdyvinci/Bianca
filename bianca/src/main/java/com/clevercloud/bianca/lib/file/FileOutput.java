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

import com.clevercloud.bianca.BiancaModuleException;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.EnvCleanup;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.vfs.LockableStream;
import com.clevercloud.vfs.Path;
import com.clevercloud.vfs.WriteStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a PHP open file
 */
public class FileOutput extends AbstractBinaryOutput
   implements LockableStream, EnvCleanup {

   private static final Logger log = Logger.getLogger(FileOutput.class.getName());
   private Env _env;
   private Path _path;
   private WriteStream _os;
   private long _offset;

   public FileOutput(Env env, Path path)
      throws IOException {
      this(env, path, false);
   }

   public FileOutput(Env env, Path path, boolean isAppend)
      throws IOException {
      _env = env;

      env.addCleanup(this);

      _path = path;

      if (isAppend) {
         _os = path.openAppend();
      } else {
         _os = path.openWrite();
      }
   }

   /**
    * Returns the write stream.
    */
   @Override
   public OutputStream getOutputStream() {
      return _os;
   }

   /**
    * Returns the file's path.
    */
   public Path getPath() {
      return _path;
   }

   /**
    * Prints a string to a file.
    */
   @Override
   public void print(char v)
      throws IOException {
      if (_os != null) {
         _os.print(v);
      }
   }

   /**
    * Prints a string to a file.
    */
   @Override
   public void print(String v)
      throws IOException {
      if (_os != null) {
         _os.print(v);
      }
   }

   /**
    * Writes a character
    */
   @Override
   public void write(int ch)
      throws IOException {
      if (_os != null) {
         _os.write(ch);
      }
   }

   /**
    * Writes a buffer to a file.
    */
   @Override
   public void write(byte[] buffer, int offset, int length)
      throws IOException {
      if (_os != null) {
         _os.write(buffer, offset, length);
      }
   }

   /**
    * Flushes the output.
    */
   @Override
   public void flush()
      throws IOException {
      if (_os != null) {
         _os.flush();
      }
   }

   /**
    * Closes the file.
    */
   @Override
   public void closeWrite() {
      close();
   }

   /**
    * Closes the file.
    */
   @Override
   public void close() {
      _env.removeCleanup(this);

      cleanup();
   }

   /**
    * Implements the EnvCleanup interface.
    */
   @Override
   public void cleanup() {
      try {
         WriteStream os = _os;
         _os = null;

         if (os != null) {
            os.close();
         }
      } catch (IOException e) {
         log.log(Level.FINE, e.toString(), e);
      }
   }

   /**
    * Lock the shared advisory lock.
    */
   @Override
   public boolean lock(boolean shared, boolean block) {
      return _os.lock(shared, block);
   }

   /**
    * Unlock the advisory lock.
    */
   @Override
   public boolean unlock() {
      return _os.unlock();
   }

   @Override
   public Value stat() {
      return FileModule.statImpl(_env, getPath());
   }

   /**
    * Returns the current location in the file.
    */
   @Override
   public long getPosition() {
      if (_os == null) {
         return -1;
      }

      return _os.getPosition();
   }

   /**
    * Sets the current location in the stream
    */
   @Override
   public boolean setPosition(long offset) {
      if (_os == null) {
         return false;
      }

      try {
         return _os.setPosition(offset);
      } catch (IOException e) {
         throw new BiancaModuleException(e);
      }
   }

   /**
    * Converts to a string.
    */
   @Override
   public String toString() {
      return "FileOutput[" + getPath() + "]";
   }
}
