package de.dk.bininja.admin.ui.cli;

import java.io.IOException;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.ui.cli.CliCommand;
import de.dk.bininja.ui.cli.CliCommandResult;

public class ExitCommand extends CliCommand<UIController> {
   private static final String NAME = "exit";

   protected ExitCommand() {
      super(NAME, NAME);
   }

   @Override
   protected CliCommandResult checkedExecute(String input, UIController controller) throws IOException, InterruptedException {
      controller.exit();
      return new CliCommandResult(true, "Exiting the BiNinja admintool.");
   }

   @Override
   public void printUsage() {
      System.out.println("exit");
      System.out.println("Exit the program.");
   }

}