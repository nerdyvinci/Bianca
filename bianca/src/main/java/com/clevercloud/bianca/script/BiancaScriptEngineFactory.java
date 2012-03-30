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
package com.clevercloud.bianca.script;

import com.clevercloud.bianca.BiancaContext;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.SimpleBindings;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Script engine factory
 */
public class BiancaScriptEngineFactory implements ScriptEngineFactory {

   private Bindings _globalBindings = new SimpleBindings();

   /**
    * Returns the full name of the ScriptEngine.
    */
   @Override
   public String getEngineName() {
      return "CleverCloud Bianca Script Engine";
   }

   /**
    * Returns the version of the ScriptEngine.
    */
   @Override
   public String getEngineVersion() {
      try {
         //return com.clevercloud.Version.VERSION;

         Class cl = Class.forName("com.clevercloud.Version");
         Field version = cl.getField("VERSION");

         return (String) version.get(null);
      } catch (Exception e) {
      }

      return "Bianca/3.1.0";
   }

   /**
    * Returns an array of filename extensions normally used by this
    * language.
    */
   @Override
   public List<String> getExtensions() {
      ArrayList<String> ext = new ArrayList<String>();
      ext.add("php");
      return ext;
   }

   /**
    * Returns the mime-types for scripts for the engine.
    */
   @Override
   public List<String> getMimeTypes() {
      return new ArrayList<String>();
   }

   /**
    * Returns the short names for the scripts for the engine,
    * e.g. {"javascript", "rhino"}
    */
   @Override
   public List<String> getNames() {
      ArrayList<String> names = new ArrayList<String>();
      names.add("bianca");
      names.add("php");
      return names;
   }

   /**
    * Returns the name of the supported language.
    */
   @Override
   public String getLanguageName() {
      return "php";
   }

   /**
    * Returns the version of the scripting language.
    */
   @Override
   public String getLanguageVersion() {
      return "5.3.2";
   }

   /**
    * Returns engine-specific properties.
    * <p/>
    * Predefined keys include:
    * <ul>
    * <li>THREADING
    * </ul>
    */
   @Override
   public Object getParameter(String key) {
      if ("THREADING".equals(key)) {
         return "THREAD-ISOLATED";
      } else if (ScriptEngine.ENGINE.equals(key)) {
         return getEngineName();
      } else if (ScriptEngine.ENGINE_VERSION.equals(key)) {
         return getEngineVersion();
      } else if (ScriptEngine.NAME.equals(key)) {
         return getEngineName();
      } else if (ScriptEngine.LANGUAGE.equals(key)) {
         return getLanguageName();
      } else if (ScriptEngine.LANGUAGE_VERSION.equals(key)) {
         return getLanguageVersion();
      } else {
         return null;
      }
   }

   /**
    * Returns a string which could invoke a method of a Java object.
    */
   @Override
   public String getMethodCallSyntax(String obj, String m, String[] args) {
      StringBuilder sb = new StringBuilder();

      sb.append("$");
      sb.append(obj);
      sb.append("->");
      sb.append(m);
      sb.append("(");
      for (int i = 0; i < args.length; i++) {
         if (i != 0) {
            sb.append(", ");
         }

         sb.append("$");
         sb.append(args[i]);
      }
      sb.append(");");

      return sb.toString();
   }

   /**
    * Returns a string which generates an output statement.
    */
   @Override
   public String getOutputStatement(String toDisplay) {
      return "echo(\'" + toDisplay.replace("\'", "\\\'") + "\');";
   }

   /**
    * Returns a string which generates a valid program.
    */
   @Override
   public String getProgram(String[] statements) {
      StringBuilder sb = new StringBuilder();

      sb.append("<?php\n");

      for (int i = 0; i < statements.length; i++) {
         sb.append(statements[i]);
         sb.append(";\n");
      }

      sb.append("?>\n");

      return sb.toString();
   }

   /**
    * Returns a ScriptEngine instance.
    */
   @Override
   public ScriptEngine getScriptEngine() {
      return new BiancaScriptEngine(this, createBianca());
   }

   /**
    * Creates a new Bianca, which can be overridden for security issues.
    */
   protected BiancaContext createBianca() {
      BiancaContext bianca = new BiancaContext();

      bianca.init();

      return bianca;
   }

   @Override
   public String toString() {
      return "BiancaScriptEngineFactory[]";
   }
}
