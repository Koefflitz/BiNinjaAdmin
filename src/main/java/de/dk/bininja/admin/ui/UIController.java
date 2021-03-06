package de.dk.bininja.admin.ui;

import java.io.IOException;
import java.util.Collection;

import de.dk.bininja.net.ConnectionMetadata;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;
import de.dk.bininja.ui.cli.CliController;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public interface UIController extends CliController {
   public int countConnectedClients(ConnectionType connectionType) throws IOException, InterruptedException;
   public Collection<ConnectionMetadata> readConnectionDetailsOf(ConnectionType type) throws IOException, InterruptedException;
   public BooleanAnswerPacket setBufferSize(int bufferSize) throws IOException, InterruptedException;
   public int readBufferSize() throws IOException, InterruptedException;
   public void shutdownServer() throws IOException, InterruptedException;
}
