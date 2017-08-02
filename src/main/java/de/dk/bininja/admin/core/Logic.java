package de.dk.bininja.admin.core;

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.net.Base64Connection;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.packet.admin.AdminPacket;
import de.dk.bininja.net.packet.admin.AdminPacket.AdminPacketType;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;
import de.dk.bininja.net.packet.admin.CountConnectionsPacket;
import de.dk.bininja.net.packet.admin.CountConnectionsResultPacket;
import de.dk.bininja.net.packet.admin.ReadBufferSizePacket;
import de.dk.bininja.net.packet.admin.SetBufferSizePacket;
import de.dk.util.channel.ChannelClosedException;
import de.dk.util.net.ConnectionListener;
import de.dk.util.net.ReadingException;
import de.dk.util.net.Receiver;

public class Logic implements Receiver, ConnectionListener {
   private static final Logger LOGGER = LoggerFactory.getLogger(Logic.class);

   private final LogicController controller;
   private Base64Connection connection;

   private AdminPacket result;

   public Logic(LogicController controller) {
      this.controller = controller;
   }

   public void start(String host, int port) throws UnknownHostException, IOException {
      this.connection = new Base64Connection(host, port, this);
      connection.sendRaw(ConnectionType.ADMIN.getString());
      connection.addListener(this);
      connection.start();
   }

   public int countConnectedClients(ConnectionType connectionType) throws IOException,
                                                                          InterruptedException {
      CountConnectionsResultPacket result = request(new CountConnectionsPacket(connectionType), CountConnectionsResultPacket.class);
      return result.getCount();
   }

   public int readBufferSize() throws IOException, InterruptedException {
      ReadBufferSizePacket result = request(new ReadBufferSizePacket(), ReadBufferSizePacket.class);
      return result.getBufferSize();
   }

   public BooleanAnswerPacket setBufferSize(int bufferSize) throws IOException, InterruptedException {
      BooleanAnswerPacket result = request(new SetBufferSizePacket(bufferSize), BooleanAnswerPacket.class);
      return result;
   }

   public BooleanAnswerPacket shutDownServer() throws IOException, InterruptedException {
      BooleanAnswerPacket result = request(new AdminPacket(AdminPacketType.SHUTDOWN), BooleanAnswerPacket.class);
      return result;
   }

   @SuppressWarnings("unchecked")
   private synchronized <P> P request(AdminPacket request, Class<P> resultType) throws IOException, InterruptedException {
      LOGGER.debug("Sending " + request + " to the server and wait...");
      try {
         connection.send(request);
      } catch (ChannelClosedException | IllegalArgumentException e) {
         throw new IOException(e);
      }

      wait();

      if (result == null || result.getType() != request.getType() || !resultType.isAssignableFrom(result.getClass()))
         throw new IOException("The received answer " + result + " did not match the request " + request);

      return (P) result;
   }

   @Override
   public synchronized void receive(Object msg) throws IllegalArgumentException {
      LOGGER.debug("Message received: " + msg);
      try {
         if (msg instanceof AdminPacket)
            this.result = (AdminPacket) msg;
         else
            throw new IllegalArgumentException("Incoming message was no AdminPacket");
      } finally {
         notify();
      }
   }

   @Override
   public void closed() {
      controller.showMessage("Verbindung zum Server verloren.");
      controller.exit();
   }

   @Override
   public void readingError(ReadingException e) {
      String msg = "An error occured while reading from the Server.";
      LOGGER.error(msg, e);
      controller.showError(msg);
   }

   public void close() throws InterruptedException {
      if (connection == null)
         return;

      connection.removeListener(this);
      if (connection.isRunning()) {
         LOGGER.debug("Closing the connection to the server");
         try {
            connection.close(0);
         } catch (IOException e) {
            LOGGER.warn("Error closing the connection " + connection, e);
         }
      }
   }
}