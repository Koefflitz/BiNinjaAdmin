package de.dk.bininja.admin.ui.cli;

import java.io.IOException;

import de.dk.bininja.admin.ui.UIController;
import de.dk.bininja.net.packet.admin.BooleanAnswerPacket;
import de.dk.bininja.ui.cli.CliCommand;
import de.dk.bininja.ui.cli.CliCommandResult;

public class SetBufferSizeCommand extends CliCommand<UIController> {
   private static final String NAME = "set-buffer-size";
   private static final String REGEX = "^set-buffer-size [0-9]+$";

   protected SetBufferSizeCommand() {
      super(NAME, REGEX);
   }

   @Override
   protected CliCommandResult checkedExecute(String input, UIController controller) throws IOException, InterruptedException {
      String value = input.substring(NAME.length() + 1);
      int bufferSize;
      try {
         bufferSize = Integer.parseInt(value);
         if (bufferSize == 0)
            throw new NumberFormatException("Buffersize cannot be 0.");
      } catch (NumberFormatException e) {
         return new CliCommandResult(false, "Invalid buffersize: " + value + " - " + e.getMessage());
      }

      BooleanAnswerPacket result = controller.setBufferSize(bufferSize);
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