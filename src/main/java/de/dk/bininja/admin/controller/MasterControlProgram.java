package de.dk.bininja.admin.controller;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Collection;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.admin.core.Logic;
import de.dk.bininja.admin.core.LogicController;
import de.dk.bininja.admin.opt.ParsedArgs;
import de.dk.bininja.admin.ui.UI;
import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.net.Base64Connection;
import de.dk.bininja.net.ConnectionDetails;
import de.dk.bininja.net.ConnectionRefusedException;
import de.dk.bininja.net.ConnectionRequest;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.SessionKeyBuilder;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;
import de.dk.util.net.security.SessionKeyArrangement;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class MasterControlProgram implements LogicController, UIController, SessionKeyBuilder {
   private static final Logger LOGGER = LoggerFactory.getLogger(MasterControlProgram.class);

   private static final long CONNECT_TIMEOUT = 0;

   private Logic processor;
   private UI ui;

   private KeyPair keys;

   public MasterControlProgram() {

   }

   public void start(Logic processor, UI ui, ParsedArgs args) {
      LOGGER.debug("BiNinjaAdmin tool initialized and ready for action.");
      this.processor = processor;
      this.ui = ui;

      String host = args.getHost();
      int port = args.getPort()
                     .orElse(Base64Connection.PORT);

      if (args.isSecure()) {
         this.keys = args.getSecurityArgs()
                         .getKeys();
      }

      if (host != null) {
         try {
            connect(host, port);
         } catch (IOException | ConnectionRefusedException e) {
            System.out.println("Could not connect to " + host + ":" + port + " - " + e.getMessage());
            System.exit(1);
            return;
         }
      }
      ui.start();
   }

   @Override
   public void connect(String host, int port) throws IOException, ConnectionRefusedException {
      ConnectionRequest request = new ConnectionRequest(host, port);
      if (isSecure())
         request.setCrypterBuilder(this);

      Base64Connection connection;
      try {
         connection = request.request(ConnectionType.ADMIN, CONNECT_TIMEOUT);
      } catch (InterruptedException e) {
         throw new IOException("Interrupted while establishing connection", e);
      }
      processor.connected(connection);
      ui.setConnected(true);
   }

   @Override
   public SecretKey buildSessionKey(SessionKeyArrangement builder) throws IOException {
      return builder.setGenerateSessionKey(true)
                    .setPublicKey(keys.getPublic())
                    .arrange();
   }

   private boolean isSecure() {
      return keys != null;
   }

   @Override
   public int countConnectedClients(ConnectionType connectionType) throws IOException, InterruptedException {
      return processor.countConnectedClients(connectionType);
   }

   @Override
   public Collection<ConnectionDetails> readConnectionDetailsOf(ConnectionType type) throws IOException, InterruptedException {
      return processor.readConnectionDetailsOf(type);
   }

   @Override
   public int readBufferSize() throws IOException, InterruptedException {
      return processor.readBufferSize();
   }

   @Override
   public BooleanAnswerPacket setBufferSize(int bufferSize) throws IOException, InterruptedException {
      return processor.setBufferSize(bufferSize);
   }

   @Override
   public void shutdownServer() throws IOException, InterruptedException {
      processor.shutDownServer();
   }

   @Override
   public void exit() {
      LOGGER.debug("Exiting the BiNinja admintool.");
      try {
         processor.close();
      } catch (InterruptedException e) {

      }
      ui.close();
   }

   @Override
   public void show(String msg, Object... args) {
      ui.show(msg);
   }

   @Override
   public void showError(String msg, Object... args) {
      ui.showError(msg);
   }
}
