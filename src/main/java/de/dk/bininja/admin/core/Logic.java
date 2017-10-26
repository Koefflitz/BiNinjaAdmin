package de.dk.bininja.admin.core;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.net.Base64Connection;
import de.dk.bininja.net.ConnectionMetadata;
import de.dk.bininja.net.ConnectionRefusedException;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.packet.admin.AdminPacket;
import de.dk.bininja.net.packet.admin.AdminPacket.AdminPacketType;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;
import de.dk.bininja.net.packet.admin.ConnectionMetaPacket;
import de.dk.bininja.net.packet.admin.CountConnectionsPacket;
import de.dk.bininja.net.packet.admin.CountConnectionsResultPacket;
import de.dk.bininja.net.packet.admin.ReadBufferSizePacket;
import de.dk.bininja.net.packet.admin.SetBufferSizePacket;
import de.dk.util.net.Connection;
import de.dk.util.net.ConnectionListener;
import de.dk.util.net.Receiver;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class Logic implements Receiver, ConnectionListener {
   private static final Logger LOGGER = LoggerFactory.getLogger(Logic.class);

   private final LogicController controller;
   private Base64Connection connection;

   private AdminPacket result;

   private boolean exiting;

   public Logic(LogicController controller) {
      this.controller = controller;
   }

   public void connected(Base64Connection connection) throws IOException, ConnectionRefusedException {
      this.connection = connection;
      connection.addListener(this);
      connection.addReceiver(this);
   }

   public int countConnectedClients(ConnectionType connectionType) throws IOException,
                                                                          InterruptedException {
      CountConnectionsResultPacket result = request(new CountConnectionsPacket(connectionType),
                                                    CountConnectionsResultPacket.class);
      return result.getCount();
   }

   public Collection<ConnectionMetadata> readConnectionDetailsOf(ConnectionType type) throws IOException,
                                                                                            InterruptedException {
      ConnectionMetaPacket result = request(new ConnectionMetaPacket(type),
                                               ConnectionMetaPacket.class);
      return result.getConnectionDetails();
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
      connection.send(request);

      wait();

      LOGGER.debug("Answer to " + request + " received: " + result);

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
   public void closed(Connection connection) {
      if (exiting)
         return;

      controller.show("Verbindung zum Server verloren.");
      controller.exit();
   }

   public void close() throws InterruptedException {
      if (connection == null)
         return;

      exiting = true;

      connection.removeListener(this);
      if (!connection.isClosed()) {
         LOGGER.debug("Closing the connection to the server");
         try {
            connection.close(0);
         } catch (IOException e) {
            LOGGER.warn("Error closing the connection " + connection, e);
         }
      }
      exiting = false;
   }
}
