package de.dk.bininja.admin.entrypoint;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.admin.controller.MasterControlProgram;
import de.dk.bininja.admin.core.Logic;
import de.dk.bininja.admin.opt.ParsedArgs;
import de.dk.bininja.admin.ui.cli.AdminCli;
import de.dk.util.opt.ex.ArgumentParseException;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class Entrypoint {
   private static final Logger LOGGER = LoggerFactory.getLogger(Entrypoint.class);

   public Entrypoint() {

   }

   public static void main(String... args) {
      ParsedArgs parsedArgs;
      try {
         parsedArgs = ParsedArgs.parse(args);
      } catch (ArgumentParseException e) {
         String argsString = Stream.<String>of(args)
                                   .reduce((a, b) -> a + " " + b)
                                   .orElse("");
         LOGGER.info("Error parsing arguments: \"" + argsString + "\"", e);
         System.out.println(e.getMessage());
         System.exit(1);
         return;
      }

      MasterControlProgram mcp = new MasterControlProgram();
      mcp.start(new Logic(mcp), new AdminCli(mcp), parsedArgs);
   }

}
