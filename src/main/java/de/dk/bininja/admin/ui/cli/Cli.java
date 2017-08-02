package de.dk.bininja.admin.ui.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.admin.ui.UI;
import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.net.Base64Connection;
import de.dk.util.StringUtils;

public class Cli implements UI {
   private static final Logger LOGGER = LoggerFactory.getLogger(Cli.class);

   private static final String PROMPT_NOT_CONNECTED = "BiNinja (n.c.)>";
   private static final String PROMPT_CONNECTED = "BiNinjaServer>";

   private static final long DEFAULT_READ_INTERVAL = 128;

   private static final String HELP = "help";

   private static final String SHORT_HELP = "h";

   private final UIController controller;
   private final BufferedReader in;
   private long readInterval = DEFAULT_READ_INTERVAL;
   private Thread runningThread;

   private boolean connected;

   private boolean running;

   public Cli(UIController controller) {
      this.controller = controller;
      this.in = new BufferedReader(new InputStreamReader(System.in));
   }

   private static boolean isHelp(String input) {
      return input.equals(HELP) || input.equals(SHORT_HELP);
   }

   @Override
   public void start() {
      running = true;
      this.runningThread = Thread.currentThread();

      if (!connected) {
         if (!connect())
            controller.exit();
      }

      while (running) {
         String input;
         try {
            input = prompt();
         } catch (IOException | InterruptedException e) {
            if (running) {
               System.err.println("Input was closed unexpectedly... ");
               e.printStackTrace(System.err);
               running = false;
            }
            break;
         }

         if (isHelp(input)) {
            printHelp();
            continue;
         }
         CliCommand cmd = CliCommand.parse(input);
         if (cmd == null) {
            System.out.println("Command " + input + " not found.");
            System.out.println("Type h or help to get some help.");
            continue;
         }

         handle(cmd, input);
      }
      System.out.println("BiNinja admintool out.");
   }

   private boolean connect() {
      System.out.println("Not connected. Starting connect procedure.");
      System.out.println("Please enter the host to connect to");
      String host;
      try {
         host = caughtPrompt();
      } catch (IOException | InterruptedException e) {
         return false;
      }

      System.out.println("Now enter a port (or just enter for default port " + Base64Connection.PORT + "): ");
      String portString;
      try {
         portString = caughtPrompt();
      } catch (IOException | InterruptedException e) {
         return false;
      }
      int port;
      if (StringUtils.isBlank(portString)) {
         port = Base64Connection.PORT;
      } else {
         try {
            port = Integer.parseInt(portString);
         } catch (NumberFormatException e) {
            System.out.println(portString + " is not a valid port.");
            return false;
         }
      }
      try {
         controller.connect(host, port);
      } catch (IOException e) {
         System.out.println("Could not connect to " + host + ":" + port);
         System.out.println(e.getMessage());
         return false;
      }
      return true;
   }

   private String prompt() throws IOException, InterruptedException {
      System.out.print(connected ? PROMPT_CONNECTED : PROMPT_NOT_CONNECTED);
      while (!in.ready())
         Thread.sleep(readInterval);

      String input = in.readLine();
      if (input == null)
         throw new IOException("End of stream has been reached.");

      return input.trim();
   }

   private String caughtPrompt() throws IOException, InterruptedException {
      try {
         return prompt();
      } catch (IOException | InterruptedException e) {
         if (running) {
            System.err.println("Input was closed unexpectedly... ");
            e.printStackTrace(System.err);
            running = false;
         }
         throw e;
      }
   }

   private void handle(CliCommand cmd, String input) {
      CliCommandResult result;
      try {
         result = cmd.execute(input, controller);
      } catch (IOException e) {
         System.err.println("Error executing command " + input);
         e.printStackTrace(System.err);
         return;
      } catch (InterruptedException e) {
         return;
      }

      System.out.println(result.getMessage());
      if (!result.worked())
         cmd.printUsage();
   }

   private void printHelp() {
      System.out.println("This tool expects one of the following commands:");
      for (CliCommand cmd : CliCommand.values()) {
         System.out.println();
         cmd.printUsage();
      }
   }

   @Override
   public void showMessage(String msg) {
      System.out.println(msg);
   }

   @Override
   public void showError(String msg) {
      System.err.println(msg);
   }

   public long getReadInterval() {
      return readInterval;
   }

   public void setReadInterval(long readInterval) {
      this.readInterval = readInterval;
   }

   @Override
   public void setConnected(boolean connected) {
      this.connected = connected;
   }

   public boolean isRunning() {
      return running;
   }

   @Override
   public void close() {
      LOGGER.debug("Closing the cli.");
      running = false;
      runningThread.interrupt();
   }
}