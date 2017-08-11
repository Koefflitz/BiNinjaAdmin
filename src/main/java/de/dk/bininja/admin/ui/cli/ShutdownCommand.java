package de.dk.bininja.admin.ui.cli;

import java.io.IOException;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.ui.cli.CliCommand;
import de.dk.bininja.ui.cli.CliCommandResult;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ShutdownCommand extends CliCommand<UIController> {
   private static final String NAME = "shutdown";

   public ShutdownCommand() {
      super(NAME);
   }

   @Override
   protected CliCommandResult execute(String input, UIController controller) throws InterruptedException {
      try {
         controller.shutdownServer();
      } catch (IOException e) {
         return new CliCommandResult(true, "Could not shutdown the server.\n" + e.getMessage());
      }
      return new CliCommandResult(true, "Server shutdown initiated...");
   }

   @Override
   public void printUsage() {
      System.out.println(NAME);
      System.out.println("Shuts down the server application.");
   }

}
