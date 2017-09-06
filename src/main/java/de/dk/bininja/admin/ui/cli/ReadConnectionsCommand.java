package de.dk.bininja.admin.ui.cli;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.net.ConnectionMetadata;
import de.dk.bininja.net.ConnectionType;
import de.dk.bininja.ui.cli.CliCommand;
import de.dk.bininja.ui.cli.CliCommandResult;
import de.dk.util.StringUtils;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ReadConnectionsCommand extends CliCommand<UIController> {
   private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private static final DateFormat TIME_FORMAT_LONG = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

   private static final String NAME = "read-connection-details";
   private static final String REGEX = "^" + NAME + "( (" + CountConnectionsCommand.connectionTypesWithPipes() + "))?$";

   public ReadConnectionsCommand() {
      super(NAME);
   }

   public static boolean isToday(Date date) {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.HOUR_OF_DAY, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
      return c.getTime()
              .before(date);
   }

   @Override
   protected CliCommandResult execute(String input, UIController controller) throws InterruptedException {
      if (!input.matches(REGEX))
         return new CliCommandResult(false, "Wrong Syntax of command " + name);

      ConnectionType type;
      try {
         type = CountConnectionsCommand.extractType(input, name);
      } catch (IllegalArgumentException e) {
         return new CliCommandResult(false, e.getMessage());
      }

      Collection<ConnectionMetadata> result;
      try {
         result = controller.readConnectionDetailsOf(type);
      } catch (IOException e) {
         return new CliCommandResult(true, "Could not read connection details from the server.\n" + e.getMessage());
      }
      String typeString = type == ConnectionType.ALL ? "" : (type.getString() + " ");
      StringBuilder resultBuilder = new StringBuilder();
      resultBuilder.append("Total ")
                   .append(typeString)
                   .append("connections: ")
                   .append(result.size())
                   .append('\n');
      for (ConnectionMetadata metadata : result) {
         resultBuilder.append(format(metadata))
                      .append('\n');
      }

      String output = StringUtils.indent(resultBuilder.toString(), 1);
      return new CliCommandResult(true, output);
   }

   private String format(ConnectionMetadata metadata) {
      StringBuilder builder = new StringBuilder();
      builder.append(metadata.getType())
             .append(" connection to ")
             .append(metadata.getHost())
             .append(':')
             .append(metadata.getPort())
             .append("\n\tencrypted=")
             .append(metadata.isSecure())
             .append("\n\tbytes received: ")
             .append(metadata.getBytesReceived())
             .append("\n\tbytes sent: ")
             .append(metadata.getBytesSent())
             .append("\n\tactive since ");

      Date date = new Date(metadata.getTimeStamp());
      if (isToday(date))
         builder.append(TIME_FORMAT.format(date));
      else
         builder.append(TIME_FORMAT_LONG.format(date));

      return builder.toString();
   }

   @Override
   public void printUsage() {
      System.out.println(NAME + " [" + CountConnectionsCommand.connectionTypesWithPipes() + "]");
      System.out.println("Read the connection details of every connection of a specified type (or all) from the server.");
      System.out.println("If no connection type is specified all connections will be read.");
   }

}
