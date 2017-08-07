package de.dk.bininja.admin.ui.cli;

import java.util.Arrays;
import java.util.Collection;

import de.dk.bininja.admin.ui.UI;
import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.ui.cli.Cli;
import de.dk.bininja.ui.cli.CliCommand;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class AdminCli extends Cli<UIController> implements UI {
   private static final String PROMPT_NOT_CONNECTED = "BiNinja (n.c.)>";
   private static final String PROMPT_CONNECTED = "BiNinjaServer>";

   public AdminCli(UIController controller) {
      super(controller, getCommands(), PROMPT_CONNECTED, PROMPT_NOT_CONNECTED);
   }

   private static Collection<CliCommand<? super UIController>> getCommands() {
      return Arrays.asList(new SetBufferSizeCommand(),
                           new ReadBuffersizeCommand(),
                           new CountConnectionsCommand(),
                           new ReadConnectionsCommand(),
                           new ShutdownCommand(),
                           new ExitCommand());
   }
}
