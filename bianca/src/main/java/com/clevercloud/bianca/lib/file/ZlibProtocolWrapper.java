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
 * @author Emil Ong
 */
package com.clevercloud.bianca.lib.file;

import com.clevercloud.bianca.env.*;
import com.clevercloud.bianca.lib.UrlModule;
import com.clevercloud.bianca.lib.zlib.ZlibModule;
import com.clevercloud.util.L10N;

import java.util.logging.Logger;

public class ZlibProtocolWrapper extends ProtocolWrapper {

   private static final Logger log = Logger.getLogger(ZlibProtocolWrapper.class.getName());
   private static final L10N L = new L10N(ZlibProtocolWrapper.class);

   public ZlibProtocolWrapper() {
   }

   @Override
   public BinaryStream fopen(Env env, StringValue path, StringValue mode,
                             LongValue options) {
      boolean useIncludePath =
         (options.toLong() & StreamModule.STREAM_USE_PATH) != 0;

      Value pathComponent = UrlModule.parse_url(env, path, UrlModule.PHP_URL_PATH);

      if (!pathComponent.isset()) {
         log.info(L.l("no path component found in '{0}'", path.toString()));
         return null;
      }

      return ZlibModule.gzopen(env, pathComponent.toStringValue(),
         mode.toString(),
         useIncludePath);
   }

   @Override
   public Value opendir(Env env, StringValue path, LongValue flags) {
      env.warning(L.l("opendir not supported by protocol"));

      return BooleanValue.FALSE;
   }

   @Override
   public boolean unlink(Env env, StringValue path) {
      env.warning(L.l("unlink not supported by protocol"));

      return false;
   }

   @Override
   public boolean rename(Env env, StringValue path_from, StringValue path_to) {
      env.warning(L.l("rename not supported by protocol"));

      return false;
   }

   @Override
   public boolean mkdir(Env env,
                        StringValue path, LongValue mode, LongValue options) {
      env.warning(L.l("mkdir not supported by protocol"));

      return false;
   }

   @Override
   public boolean rmdir(Env env, StringValue path, LongValue options) {
      env.warning(L.l("rmdir not supported by protocol"));

      return false;
   }

   @Override
   public Value url_stat(Env env, StringValue path, LongValue flags) {
      env.warning(L.l("stat not supported by protocol"));

      return BooleanValue.FALSE;
   }
}
