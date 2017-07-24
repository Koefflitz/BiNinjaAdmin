package de.dk.bininja.admin.entrypoint;

import java.util.OptionalInt;

public class ParsedArgs {
   private String host;
   private int port = -1;

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
}