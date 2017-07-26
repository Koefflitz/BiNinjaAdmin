package de.dk.bininja.admin.entrypoint;

import static de.dk.bininja.admin.entrypoint.Argument.HOST;
import static de.dk.bininja.admin.entrypoint.Option.PORT;

import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.admin.controller.MasterControlProgram;
import de.dk.bininja.admin.core.Logic;
import de.dk.bininja.admin.ui.cli.Cli;
import de.dk.util.StringUtils;
import de.dk.util.opt.ArgumentModel;
import de.dk.util.opt.ArgumentParser;
import de.dk.util.opt.ArgumentParserBuilder;
import de.dk.util.opt.ex.ArgumentParseException;

public class Entrypoint {
   private static final Logger LOGGER = LoggerFactory.getLogger(Entrypoint.class);
   private static final String EXIT = "exit";
   private static Scanner scanner;

   public Entrypoint() {

   }

   private static Scanner getScanner() {
      if (scanner == null)
         scanner = new Scanner(System.in);

      return scanner;
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
      mcp.start(new Logic(mcp), new Cli(mcp), parsedArgs);
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
      Optional<String> host = result.getOptionalArgumentValue(HOST.getName());
      if (host.isPresent())
         arguments.setHost(host.get());
      else
         arguments.setHost(prompt("Please specifiy a host to connect to: "));

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

   private static String prompt(String msg) {
      System.out.println(msg);
      Scanner scanner = getScanner();
      String input = scanner.nextLine();
      if (StringUtils.isBlank(input) || input.equals(EXIT))
         System.exit(1);

      return input;
   }

}