package de.dk.bininja.admin.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.admin.core.Processor;
import de.dk.bininja.admin.core.ProcessorController;
import de.dk.bininja.admin.entrypoint.ParsedArgs;
import de.dk.bininja.admin.ui.UI;
import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.net.Base64Connection;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;

public class MasterControlProgram implements ProcessorController, UIController {
   private static final Logger LOGGER = LoggerFactory.getLogger(MasterControlProgram.class);

   private Processor processor;
   private UI ui;

   public MasterControlProgram() {

   }

   public void start(Processor processor, UI ui, ParsedArgs args) {
      LOGGER.debug("BiNinjaAdmin tool initialized and ready for action.");
      this.processor = processor;
      this.ui = ui;

      String host = args.getHost();
      int port = args.getPort()
                     .orElse(Base64Connection.PORT);
      try {
         processor.start(host, port);
      } catch (IOException e) {
         System.out.println("Could not connect to " + host + ":" + port + " - " + e.getMessage());
         System.exit(1);
         return;
      }
      ui.start();
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