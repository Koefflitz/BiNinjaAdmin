package de.dk.bininja.admin.ui;

import java.io.IOException;
import java.util.Collection;

import de.dk.bininja.net.ConnectionDetails;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;
import de.dk.bininja.ui.cli.CliController;

public interface UIController extends CliController {
   public int countConnectedClients(ConnectionType connectionType) throws IOException, InterruptedException;
   public Collection<ConnectionDetails> readConnectionDetailsOf(ConnectionType type) throws IOException, InterruptedException;
   public BooleanAnswerPacket setBufferSize(int bufferSize) throws IOException, InterruptedException;
   public int readBufferSize() throws IOException, InterruptedException;
   public void shutdownServer() throws IOException, InterruptedException;
}