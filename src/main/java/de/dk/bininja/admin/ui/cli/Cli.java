package de.dk.bininja.admin.ui.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.admin.ui.UI;
import de.dk.bininja.admin.ui.UIController;

public class Cli implements UI {
   private static final Logger LOGGER = LoggerFactory.getLogger(Cli.class);

   private static final String PROMPT = "BiNinjaServer>";

   private static final long DEFAULT_READ_INTERVAL = 128;

   private static final String HELP = "help";

   private static final String SHORT_HELP = "h";

   private final UIController controller;
   private final BufferedReader in;
   private long readInterval = DEFAULT_READ_INTERVAL;
   private Thread runningThread;

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
      while (running) {
         System.out.print(PROMPT);
         String input;
         try {
            while (!in.ready())
               Thread.sleep(readInterval);

            input = in.readLine()
                      .trim();
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