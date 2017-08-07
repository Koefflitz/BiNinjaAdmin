package de.dk.bininja.admin.ui.cli;

import java.io.IOException;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.ui.cli.CliCommand;
import de.dk.bininja.ui.cli.CliCommandResult;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ReadBuffersizeCommand extends CliCommand<UIController> {
   private static final String NAME = "read-buffer-size";

   public ReadBuffersizeCommand() {
      super(NAME, NAME);
   }

   @Override
   protected CliCommandResult checkedExecute(String input, UIController controller) throws IOException, InterruptedException {
      int bufferSize = controller.readBufferSize();
      String msg = "buffer-size=" + bufferSize;
      return new CliCommandResult(true, msg);
   }

   @Override
   public void printUsage() {
      System.out.println(NAME);
      System.out.println("Reads the configured size of the pieces of data"
                         + "that the server downloads and sends to the download client in byte.");
   }
}
