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
 * @author Nam Nguyen
 * @author Marc-Antoine Perennou <Marc-Antoine@Perennou.com>
 */
package com.clevercloud.bianca.lib.gettext;

import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.StringValue;
import com.clevercloud.bianca.lib.gettext.expr.PluralExpr;
import com.clevercloud.util.L10N;
import com.clevercloud.vfs.Path;
import com.clevercloud.vfs.ReadStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Parses Gettext PO files.
 */
class POFileParser extends GettextParser {

   private static final Logger log = Logger.getLogger(POFileParser.class.getName());
   private static final L10N L = new L10N(POFileParser.class);
   // Parsing constants and variables
   private static final int MSGID = 256;
   private static final int MSGID_PLURAL = 257;
   private static final int MSGSTR = 258;
   private static final int UNKNOWN = 260;
   private Env _env;
   private ReadStream _in;
   private int _peekChar;
   private StringValue _string;

   POFileParser(Env env, Path path)
      throws IOException {
      _env = env;

      init(path);
   }

   final void init(Path path)
      throws IOException {
      _in = path.openRead();
      _peekChar = -1;

      StringValue metadata = getMetadata();

      _pluralExpr = PluralExpr.getPluralExpr(metadata);
      _charset = getCharset(metadata);

      _in.setEncoding(_charset);
   }

   private StringValue getMetadata()
      throws IOException {
      StringValue metadata = null;

      int token = readToken();

      while (token >= 0 && token != UNKNOWN) {
         if (token == MSGID && _string.length() == 0) {

            if (readToken() == MSGSTR) {
               metadata = _string;
            }

            break;
         }
      }

      _peekChar = -1;
      _in.setPosition(0);

      return metadata;
   }

   /**
    * Returns the gettext translations.
    *
    * @return translations from file, or null on error
    */
   @Override
   HashMap<StringValue, ArrayList<StringValue>> readTranslations()
      throws IOException {
      HashMap<StringValue, ArrayList<StringValue>> translations =
         new HashMap<StringValue, ArrayList<StringValue>>();

      int token = readToken();

      while (token >= 0) {
         if (token != MSGID) {
            return null;
         }

         StringValue msgid = _string;

         token = readToken();
         if (token == MSGID_PLURAL) {
            token = readToken();
         }

         ArrayList<StringValue> msgstrs = new ArrayList<StringValue>();

         for (; token == MSGSTR; token = readToken()) {
            msgstrs.add(_string);
         }

         translations.put(msgid, msgstrs);
      }

      return translations;
   }

   private int readToken()
      throws IOException {
      int ch = skipWhitespace();

      switch (ch) {
         case '#':
            skipLine();
            return readToken();

         case 'm':
            if (read() == 's'
               && read() == 'g') {
               return readMsgToken();
            } else {
               return UNKNOWN;
            }

         case -1:
            return -1;

         default:
            return UNKNOWN;
      }
   }

   private int readMsgToken()
      throws IOException {
      int ch = read();

      switch (ch) {
         case 'i':
            if (read() == 'd') {
               return readMsgidToken();
            } else {
               return UNKNOWN;
            }

         case 's':
            if (read() == 't'
               && read() == 'r') {
               return readMsgstrToken();
            } else {
               return UNKNOWN;
            }

         default:
            return UNKNOWN;
      }
   }

   private int readMsgidToken()
      throws IOException {
      int token;
      int ch = skipWhitespace();

      if (ch == '_') {
         if (read() == 'p'
            && read() == 'l'
            && read() == 'u'
            && read() == 'r'
            && read() == 'a'
            && read() == 'l') {
            token = MSGID_PLURAL;

            ch = skipWhitespace();
         } else {
            return UNKNOWN;
         }
      } else {
         token = MSGID;
      }

      if (ch != '"') {
         return UNKNOWN;
      }

      return readOriginalString(token);
   }

   private int readMsgstrToken()
      throws IOException {
      int ch = skipWhitespace();

      if (ch == '[') {
         ch = read();

         while (ch >= 0 && ch != ']') {
            ch = read();
         }

         ch = skipWhitespace();
      }

      if (ch != '"') {
         return UNKNOWN;
      }

      return readString(MSGSTR);
   }

   /**
    * Reads a string in quotes.
    */
   private int readOriginalString(int token)
      throws IOException {
      return readString(new StringValue(), token);
   }

   /**
    * Reads a string in quotes.
    */
   private int readString(int token)
      throws IOException {
      return readString(new StringValue(), token);
   }

   /**
    * XXX: any other possible character escapes?
    */
   private int readString(StringValue sb, int token)
      throws IOException {
      for (int ch = read(); ch != '"'; ch = read()) {
         switch (ch) {
            case '\\':
               ch = read();
               switch (ch) {
                  case 'n':
                     sb.append('\n');
                     break;
                  case 'r':
                     sb.append('\r');
                     break;
                  case 't':
                     sb.append('\t');
                     break;
                  case '\r':
                     ch = read();
                     if (ch != '\n') {
                        _peekChar = ch;
                     }
                     break;
                  case '\n':
                     break;
                  default:
                     _peekChar = ch;
                     sb.append('\\');
               }
               break;

            case -1:
               return UNKNOWN;

            default:
               sb.append((char) ch);
         }
      }

      // String may be continued on the next line.

      int ch = skipWhitespace();

      if (ch == '"') {
         return readString(sb, token);
      } else {
         _peekChar = ch;
      }

      _string = sb;
      return token;
   }

   private int read()
      throws IOException {
      if (_peekChar >= 0) {
         int swap = _peekChar;
         _peekChar = -1;
         return swap;
      }

      return _in.readChar();
   }

   private void skipLine()
      throws IOException {
      int ch = read();

      while (ch >= 0) {
         switch (ch) {
            case '\r':
               ch = read();

               if (ch != '\n') {
                  _peekChar = ch;
               }

               return;

            case '\n':
               return;
         }

         ch = read();
      }
   }

   private int skipWhitespace()
      throws IOException {
      while (true) {
         int ch = read();

         switch (ch) {
            case ' ':
            case '\r':
            case '\n':
            case '\t':
               continue;
            default:
               return ch;
         }
      }
   }

   @Override
   void close() {
      if (_in != null) {
         _in.close();
      }
   }
}
