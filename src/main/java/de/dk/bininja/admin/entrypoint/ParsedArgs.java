package de.dk.bininja.admin.entrypoint;

import java.util.OptionalInt;

import de.dk.bininja.opt.ParsedSecurityArguments;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ParsedArgs {
   private String host;
   private int port = -1;
   private ParsedSecurityArguments secArgs;

   public ParsedArgs() {

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
