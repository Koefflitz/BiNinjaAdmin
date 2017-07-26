package de.dk.bininja.admin.ui.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.UnaryOperator;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.net.ConnectionType;

public class CountConnectionsCommand extends CliCommand {
   private static final String NAME = "count-connections";
   private static final String REGEX = "^" + NAME + "( -(" + connectionTypesWithPipes() + "))?$";

   protected CountConnectionsCommand() {
      super(NAME, REGEX);
   }

   public static String connectionTypesWithPipes(UnaryOperator<String> extender) {
      return Arrays.stream(ConnectionType.values())
                   .map(ConnectionType::getString)
                   .map(extender)
                   .reduce((a, b) -> a + "|" + b)
                   .get();
   }

   public static String connectionTypesWithPipes() {
      return connectionTypesWithPipes(UnaryOperator.identity());
   }

   @Override
   protected CliCommandResult checkedExecute(String input, UIController controller) throws IOException, InterruptedException {
      ConnectionType type;
      if (input.equals(NAME)) {
         type = ConnectionType.ALL;
      } else {
         String param = input.substring(NAME.length() + 2);
         if (param.charAt(param.length() - 1) == ')')
            param = param.substring(0, param.length() - 1);

         type = ConnectionType.parse(param);

         if (type == null)
            return new CliCommandResult(false, "Invalid connectionType: " + param);
      }

      int result = controller.countConnectedClients(type);

      return new CliCommandResult(true, type.getDescription() + " connection count: " + result);
   }

   @Override
   public void printUsage() {
      String synopsis = "count-connections [" + connectionTypesWithPipes(t -> "-" + t) + "]";
      System.out.println(synopsis);
      System.out.println("Get the number of the currently connected clients of a type (or all).");
      System.out.println("With no option given, all connections will be counted.");
   }
}