package de.dk.bininja.admin.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.admin.core.Processor;
import de.dk.bininja.admin.core.ProcessorController;
import de.dk.bininja.admin.ui.UI;
import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.admin.ui.cli.Cli;
import de.dk.bininja.net.Base64Connection;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;

public class MasterControlProgram implements ProcessorController, UIController {
   private static final Logger LOGGER = LoggerFactory.getLogger(MasterControlProgram.class);

   private Processor processor;
   private UI ui;

   public MasterControlProgram() {

   }

   public static void main(String... args) {
      new MasterControlProgram().start(args);
   }

   public int start(String... args) {
      this.ui = new Cli(this);

      String host = "localhost";
      int port = Base64Connection.PORT;

      if (args != null && args.length > 0) {
         LOGGER.debug("Parsing arguments");
         host = args[0];
         if (args.length > 1) {
            try {
               port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
               ui.showError("Invalid port: \"" + args[1] + "\" - " + e.getMessage());
               return 1;
            }
         }
      }

      try {
         this.processor = new Processor(this, host, port);
      } catch (IOException e) {
         ui.showError("Could not connect to " + host + ":" + port + " - " + e.getMessage());
         return 1;
      }

      LOGGER.debug("BiNinjaAdmin tool initialized and ready for action.");
      ui.start();
      return 0;
   }

   @Override
   public int countConnectedClients(ConnectionType connectionType) throws IOException, InterruptedException {
      return processor.countConnectedClients(connectionType);
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
   public void showMessage(String msg) {
      ui.showMessage(msg);
   }

   @Override
   public void showError(String msg) {
      ui.showError(msg);
   }
}