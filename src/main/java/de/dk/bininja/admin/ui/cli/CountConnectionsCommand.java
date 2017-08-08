package de.dk.bininja.admin.ui.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.UnaryOperator;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.ui.cli.CliCommand;
import de.dk.bininja.ui.cli.CliCommandResult;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class CountConnectionsCommand extends CliCommand<UIController> {
   private static final String NAME = "count-connections";
   private static final String REGEX = "^" + NAME + "( (" + connectionTypesWithPipes() + "))?$";

   protected CountConnectionsCommand() {
      super(NAME);
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

   public static ConnectionType extractType(String input, String commandName) throws IllegalArgumentException {
      ConnectionType type;
      if (input.equals(commandName)) {
         type = ConnectionType.ALL;
      } else {
         String param = input.substring(commandName.length() + 1);
         if (param.charAt(param.length() - 1) == ')')
            param = param.substring(0, param.length() - 1);

         type = ConnectionType.parse(param);

         if (type == null)
            throw new IllegalArgumentException("Invalid connectionType: " + param);
      }
      return type;
   }

   @Override
   protected CliCommandResult execute(String input, UIController controller) throws IOException,
                                                                                    InterruptedException {
      if (!input.matches(REGEX))
         return new CliCommandResult(false, "Wrong Syntax of command " + name);

      ConnectionType type;
      try {
         type = extractType(input, name);
      } catch (IllegalArgumentException e) {
         return new CliCommandResult(false, e.getMessage());
      }

      int result = controller.countConnectedClients(type);

      return new CliCommandResult(true, type.getDescription() + " connection count: " + result);
   }

   @Override
   public void printUsage() {
      String synopsis = "count-connections [" + connectionTypesWithPipes() + "]";
      System.out.println(synopsis);
      System.out.println("Get the number of the currently connected clients of a type (or all).");
      System.out.println("With no option given, all connections will be counted.");
   }
}
