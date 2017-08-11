package de.dk.bininja.admin.ui.cli;

import java.io.IOException;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;
import de.dk.bininja.ui.cli.CliCommand;
import de.dk.bininja.ui.cli.CliCommandResult;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class SetBufferSizeCommand extends CliCommand<UIController> {
   private static final String NAME = "set-buffer-size";
   private static final String REGEX = "^set-buffer-size [0-9]+$";

   protected SetBufferSizeCommand() {
      super(NAME);
   }

   @Override
   protected CliCommandResult execute(String input, UIController controller) throws InterruptedException {
      if (!input.matches(REGEX))
         return new CliCommandResult(false, "Wrong Syntax of command " + name);

      String value = input.substring(NAME.length() + 1);
      int bufferSize;
      try {
         bufferSize = Integer.parseInt(value);
         if (bufferSize == 0)
            throw new NumberFormatException("Buffersize cannot be 0.");
      } catch (NumberFormatException e) {
         return new CliCommandResult(false, "Invalid buffersize: " + value + " - " + e.getMessage());
      }

      BooleanAnswerPacket result;
      try {
         result = controller.setBufferSize(bufferSize);
      } catch (IOException e) {
         return new CliCommandResult(true, "Could not set the buffer size on the server.\n" + e.getMessage());
      }
      if (result.getResult())
         return new CliCommandResult(true, "Buffer size set to " + bufferSize);
      else
         return new CliCommandResult(false, "Could not set buffer size. " + result.getMsg());
   }

   @Override
   public void printUsage() {
      System.out.println("set-buffer-size <buffersize>");
      System.out.println("Sets the size of the pieces of data"
                         + "that the server downloads and sends to the download client in byte.");
   }
}
