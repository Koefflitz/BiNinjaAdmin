package de.dk.bininja.admin.ui.cli;

import java.io.IOException;

import de.dk.bininja.admin.ui.UIController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ReadConnectionsCommand extends CliCommand {
   private static final String NAME = "read-connection-details";
   private static final String REGEX = "^" + NAME + "( -(" + CountConnectionsCommand.connectionTypesWithPipes() + "))?$";

   public ReadConnectionsCommand() {
      super(NAME, REGEX);
   }

   @Override
   protected CliCommandResult checkedExecute(String input, UIController controller) throws IOException,
                                                                                           InterruptedException {
      // TODO Methode implementieren
      throw new NotImplementedException();
   }

   @Override
   public void printUsage() {
      // TODO Methode implementieren
      throw new NotImplementedException();
   }

}
