package de.dk.bininja.admin.ui;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;

import de.dk.bininja.net.ConnectionDetails;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;

public interface UIController {
   public void connect(String host, int port) throws UnknownHostException, IOException;
   public int countConnectedClients(ConnectionType connectionType) throws IOException, InterruptedException;
   public Collection<ConnectionDetails> readConnectionDetailsOf(ConnectionType type) throws IOException, InterruptedException;
   public BooleanAnswerPacket setBufferSize(int bufferSize) throws IOException, InterruptedException;
   public int readBufferSize() throws IOException, InterruptedException;
   public void shutdownServer() throws IOException, InterruptedException;
   public void exit();
}