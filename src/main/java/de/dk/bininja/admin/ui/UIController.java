package de.dk.bininja.admin.ui;

import java.io.IOException;

import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;

public interface UIController {
   public int countConnectedClients(ConnectionType connectionType) throws IOException, InterruptedException;
   public BooleanAnswerPacket setBufferSize(int bufferSize) throws IOException, InterruptedException;
   public int readBufferSize() throws IOException, InterruptedException;
   public void shutdownServer() throws IOException, InterruptedException;
   public void exit();
}