package de.dk.bininja.admin.opt;

import static de.dk.bininja.admin.opt.Option.PORT;

import java.io.IOException;
import java.util.OptionalInt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dk.bininja.InvalidArgumentException;
import de.dk.bininja.opt.ParsedSecurityArguments;
import de.dk.util.opt.ArgumentModel;
import de.dk.util.opt.ArgumentParser;
import de.dk.util.opt.ArgumentParserBuilder;
import de.dk.util.opt.ex.ArgumentParseException;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ParsedArgs {
   private static final Logger LOGGER = LoggerFactory.getLogger(ParsedArgs.class);

   private String host;
   private int port = -1;
   private ParsedSecurityArguments secArgs;

   public ParsedArgs() {

   }

   public static ParsedArgs parse(String... args) throws ArgumentParseException {
      LOGGER.debug("Parsing arguments");
      ArgumentParserBuilder builder = new ArgumentParserBuilder();

      for (Option opt : Option.values())
         opt.build(builder);

      ParsedSecurityArguments.build(builder);

      ArgumentParser parser = builder.buildAndGet();

      if (parser.isHelp(args)) {
         parser.printUsage(System.out);
         System.exit(0);
      }
      ArgumentModel result = parser.parseArguments(args);
      ParsedArgs arguments = new ParsedArgs();

      // host
      result.getOptionalValue(Option.HOST.getKey())
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

      // security
      ArgumentModel securityResult = result.getCommandValue(ParsedSecurityArguments.NAME);
      if (securityResult != null) {
         ParsedSecurityArguments secArgs;
         try {
            secArgs = ParsedSecurityArguments.parse(securityResult);
         } catch (IOException e) {
            throw new ArgumentParseException("Error parsing the security args", e);
         }
         arguments.setSecurityArgs(secArgs);
      }

      return arguments;
   }

   public String getHost() {
      return host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public OptionalInt getPort() {
      return port == -1 ? OptionalInt.empty() : OptionalInt.of(port);
   }

   public void setPort(int port) {
      this.port = port;
   }

   public ParsedSecurityArguments getSecurityArgs() {
      return secArgs;
   }

   public void setSecurityArgs(ParsedSecurityArguments secArgs) {
      this.secArgs = secArgs;
   }

   public boolean isSecure() {
      return secArgs != null;
   }
}
