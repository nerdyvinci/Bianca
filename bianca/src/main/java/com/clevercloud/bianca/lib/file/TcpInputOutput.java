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
 */
package com.clevercloud.bianca.lib.file;

import com.clevercloud.bianca.BiancaException;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.vfs.ReadStream;
import com.clevercloud.vfs.SocketStream;
import com.clevercloud.vfs.WriteStream;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents read/write stream
 */
public class TcpInputOutput
   extends BufferedBinaryInputOutput
   implements SocketInputOutput {

   private static final Logger log = Logger.getLogger(TcpInputOutput.class.getName());
   private Socket _socket;
   private Domain _domain;
   private int _errno;

   public TcpInputOutput(Env env, String host, int port,
                         boolean isSecure,
                         Domain domain)
      throws IOException {
      super(env);
      env.addCleanup(this);

      if (isSecure) {
         try {
            _socket = createSSLSocket(host, port);
         } catch (KeyManagementException e) {
            throw new BiancaException(e);
         } catch (NoSuchAlgorithmException e) {
            throw new BiancaException(e);
         }
      } else {
         _socket = SocketFactory.getDefault().createSocket(host, port);
      }

      _domain = domain;
   }

   public TcpInputOutput(Env env, Socket socket, Domain domain) {
      super(env);
      env.addCleanup(this);

      _socket = socket;
      _domain = domain;
   }

   private Socket createSSLSocket(String host, int port)
      throws IOException, NoSuchAlgorithmException, KeyManagementException {
      Socket s = new Socket(host, port);

      SSLContext context = SSLContext.getInstance("TLS");

      javax.net.ssl.TrustManager tm =
         new javax.net.ssl.X509TrustManager() {

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
               return null;
            }

            @Override
            public void checkClientTrusted(
               java.security.cert.X509Certificate[] cert, String foo) {
            }

            @Override
            public void checkServerTrusted(
               java.security.cert.X509Certificate[] cert, String foo) {
            }
         };


      context.init(null, new javax.net.ssl.TrustManager[]{tm}, null);
      SSLSocketFactory factory = context.getSocketFactory();

      return factory.createSocket(s, host, port, true);
   }

   @Override
   public void bind(SocketAddress address)
      throws IOException {
      _socket.bind(address);
   }

   @Override
   public void connect(SocketAddress address)
      throws IOException {
      _socket.connect(address);

      init();
   }

   @Override
   public void setError(int errno) {
      _errno = errno;
   }

   public int getError() {
      return _errno;
   }

   @Override
   public void init() {
      SocketStream sock = new SocketStream(_socket);
      sock.setThrowReadInterrupts(true);

      WriteStream os = new WriteStream(sock);
      ReadStream is = new ReadStream(sock, os);

      init(is, os);
   }

   @Override
   public void setTimeout(long timeout) {
      try {
         if (_socket != null) {
            _socket.setSoTimeout((int) timeout);
         }
      } catch (Exception e) {
         log.log(Level.FINER, e.toString(), e);
      }
   }

   @Override
   public void write(int ch)
      throws IOException {
      super.write(ch);
      flush();
   }

   @Override
   public void write(byte[] buffer, int offset, int length)
      throws IOException {
      super.write(buffer, offset, length);
      flush();
   }

   /**
    * Read length bytes of data from the InputStream
    * argument and write them to this output stream.
    */
   @Override
   public int write(InputStream is, int length)
      throws IOException {
      int writeLength = super.write(is, length);
      flush();

      return writeLength;
   }

   /**
    * Implements the EnvCleanup interface.
    */
   @Override
   public void cleanup() {
      Socket s = _socket;
      _socket = null;

      try {
         if (s != null) {
            s.close();
         }
      } catch (IOException e) {
         log.log(Level.FINE, e.toString(), e);
      }
   }

   @Override
   public String toString() {
      if (_socket != null) {
         return "TcpInputOutput[" + _socket.getInetAddress()
            + "," + _socket.getPort() + "]";
      } else {
         return "TcpInputOutput[closed]";
      }
   }
}
