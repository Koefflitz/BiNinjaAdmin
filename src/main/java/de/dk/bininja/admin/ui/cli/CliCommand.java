package de.dk.bininja.admin.ui.cli;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import de.dk.bininja.admin.ui.UIController;

public abstract class CliCommand {
   private static final Collection<CliCommand> commands = new LinkedList<>();

   public static final SetBufferSizeCommand SET_BUFFER_SIZE = new SetBufferSizeCommand();
   public static final ReadBuffersizeCommand READ_BUFFERSIZE = new ReadBuffersizeCommand();
   public static final CountConnectionsCommand COUNT_CONNECTIONS = new CountConnectionsCommand();
   public static final ShutdownCommand SHUTDOWN = new ShutdownCommand();
   public static final ExitCommand EXIT = new ExitCommand();

   protected final String name;
   private final String regex;

   public CliCommand(String name, String regex) {
      commands.add(this);
      this.name = name;
      this.regex = regex;
   }

   public static CliCommand parse(String cmdString) {
      for (CliCommand cmd : commands) {
         if (cmdString.startsWith(cmd.getName()))
            return cmd;
      }
      return null;
   }

   public static CliCommand[] values() {
      return commands.toArray(new CliCommand[commands.size()]);
   }

   public boolean matches(String input) {
      return input.matches(regex);
   }

   private String getName() {
      return name;
   }

   public CliCommandResult execute(String input, UIController controller) throws IOException, InterruptedException {
      if (!matches(input))
         return new CliCommandResult(false, "Wrong Syntax of command " + name);

      return checkedExecute(input, controller);
   }

   protected abstract CliCommandResult checkedExecute(String input, UIController controller) throws IOException, InterruptedException;
   public abstract void printUsage();
}