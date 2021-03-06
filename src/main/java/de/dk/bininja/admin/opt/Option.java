package de.dk.bininja.admin.opt;

import de.dk.bininja.net.Base64Connection;
import de.dk.opt.ArgumentParserBuilder;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public enum Option {
   HOST('H', "host", "The host to connect to.", true),
   PORT('p', "port", "The port to connect on. Default is " + Base64Connection.PORT, true);

   private final char key;
   private final String longKey;
   private final String description;
   private final boolean expectsValue;

   private Option(char key, String name, String description, boolean expectsValue) {
      this.key = key;
      this.longKey = name;
      this.description = description;
      this.expectsValue = expectsValue;
   }

   private Option(char key, String name, String description) {
      this(key, name, description, false);
   }

   public ArgumentParserBuilder build(ArgumentParserBuilder builder) {
      return builder.buildOption(key)
                    .setLongKey(longKey)
                    .setDescription(description)
                    .setExpectsValue(expectsValue)
                    .build();
   }

   public char getKey() {
      return key;
   }

   public String getName() {
      return longKey;
   }

   public String getDescription() {
      return description;
   }

   public boolean isExpectsValue() {
      return expectsValue;
   }
}
