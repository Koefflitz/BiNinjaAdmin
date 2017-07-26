package de.dk.bininja.admin.entrypoint;

import de.dk.util.opt.ex.ArgumentParseException;

public class InvalidArgumentException extends ArgumentParseException {

   private static final long serialVersionUID = 126652555748006069L;

   public InvalidArgumentException(String message) {
      super(message);
   }

   public InvalidArgumentException(Throwable cause) {
      super(cause);
   }

   public InvalidArgumentException(String message, Throwable cause) {
      super(message, cause);
   }
}