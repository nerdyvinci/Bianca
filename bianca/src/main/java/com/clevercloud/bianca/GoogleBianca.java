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
package com.clevercloud.bianca;

import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.GoogleEnv;
import com.clevercloud.bianca.module.ModuleContext;
import com.clevercloud.bianca.page.BiancaPage;
import com.clevercloud.util.L10N;
import com.clevercloud.vfs.Vfs;
import com.clevercloud.vfs.WriteStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Facade for the PHP language.
 */
public class GoogleBianca extends BiancaContext {

   private static L10N L = new L10N(GoogleBianca.class);
   private static final Logger log = Logger.getLogger(GoogleBianca.class.getName());
   private ModuleContext _localModuleContext;
   private long _dependencyCheckInterval = 2000L;

   /**
    * Constructor.
    */
   public GoogleBianca() {
      super();

      setPwd(Vfs.lookup());
      // setWorkDir(WorkDir.getLocalWorkDir());
   }

   /*
   @Override
   public ModuleContext getLocalContext(ClassLoader loader)
   {
   Thread thread = Thread.currentThread();
   ClassLoader currentLoader = thread.getContextClassLoader();

   synchronized (this) {
   if (_localModuleContext == null) {
   _localModuleContext = createModuleContext(null, currentLoader);

   _localModuleContext.init();
   }
   }

   return _localModuleContext;
   }
    */
   @Override
   protected ModuleContext createModuleContext(ModuleContext parent,
                                               ClassLoader loader) {
      return new ModuleContext(parent, loader);
   }

   @Override
   public Env createEnv(BiancaPage page,
                        WriteStream out,
                        HttpServletRequest request,
                        HttpServletResponse response) {
      return new GoogleEnv(this, page, out, request, response);
   }

   /*
   @Override
   public String getVersion()
   {
   return com.clevercloud.Version.VERSION;
   }

   @Override
   public String getVersionDate()
   {
   return com.clevercloud.Version.VERSION_DATE;
   }
    */
}
