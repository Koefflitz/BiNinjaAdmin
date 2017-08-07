package de.dk.bininja.admin.entrypoint;

import de.dk.util.opt.ArgumentParserBuilder;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public enum Argument {
   HOST("host", "The host to connect to.", false);

   private final String name;
   private final String description;
   private final boolean mandatory;

   private Argument(String name, String description, boolean mandatory) {
      this.name = name;
      this.description = description;
      this.mandatory = mandatory;
   }

   private Argument(String name, String description) {
      this(name, description, true);
   }

   public ArgumentParserBuilder build(ArgumentParserBuilder builder) {
      return builder.buildArgument(name)
                    .setDescription(description)
                    .setMandatory(mandatory)
                    .build();
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public boolean isMandatory() {
      return mandatory;
   }
}
