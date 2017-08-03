package de.dk.bininja.admin.ui.cli;

import java.io.IOException;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.ui.cli.CliCommand;
import de.dk.bininja.ui.cli.CliCommandResult;

public class ShutdownCommand extends CliCommand<UIController> {
   private static final String NAME = "shutdown";

   public ShutdownCommand() {
      super(NAME, NAME);
   }

   @Override
   protected CliCommandResult checkedExecute(String input, UIController controller) throws IOException, InterruptedException {
      controller.shutdownServer();
      return new CliCommandResult(true, "Server shutdown initiated...");
   }

   @Override
   public void printUsage() {
      System.out.println(NAME);
      System.out.println("Shuts down the server application.");
   }

}