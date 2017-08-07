package de.dk.bininja.admin.entrypoint;

import static de.dk.bininja.admin.entrypoint.Argument.HOST;
import static de.dk.bininja.admin.entrypoint.Option.PORT;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.admin.controller.MasterControlProgram;
import de.dk.bininja.admin.core.Logic;
import de.dk.bininja.admin.ui.cli.AdminCli;
import de.dk.util.opt.ArgumentModel;
import de.dk.util.opt.ArgumentParser;
import de.dk.util.opt.ArgumentParserBuilder;
import de.dk.util.opt.ex.ArgumentParseException;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class Entrypoint {
   private static final Logger LOGGER = LoggerFactory.getLogger(Entrypoint.class);

   public Entrypoint() {

   }

   public static void main(String[] args) {
      ParsedArgs parsedArgs;
      try {
         parsedArgs = parseArguments(args);
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

   private static ParsedArgs parseArguments(String... args) throws ArgumentParseException {
      LOGGER.debug("Parsing arguments");
      ArgumentParserBuilder builder = new ArgumentParserBuilder();
      for (Argument arg : Argument.values())
         arg.build(builder);

      for (Option opt : Option.values())
         opt.build(builder);

      ArgumentParser parser = builder.buildAndGet();

      if (parser.isHelp(args)) {
         parser.printUsage(System.out);
         System.exit(0);
      }
      ArgumentModel result = parser.parseArguments(args);
      ParsedArgs arguments = new ParsedArgs();

      // host
      result.getOptionalArgumentValue(HOST.getName())
            .ifPresent(arguments::setHost);

      // port
      if (result.isOptionPresent(PORT.getKey())) {
         String portString = result.getOptionValue(PORT.getKey());
         int port;
         try {
            port = Integer.parseInt(portString);
            if (port < 0 || port > 0xffff)
               throw new InvalidArgumentException("Invalid port: " + portString);
         } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Invalid port: " + portString, e);
         }
         arguments.setPort(port);
      }

      return arguments;
   }

}
