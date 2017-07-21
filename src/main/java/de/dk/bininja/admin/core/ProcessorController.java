package de.dk.bininja.admin.core;

public interface ProcessorController {
   public void showError(String msg);
   public void showMessage(String msg);
   public void exit();
}