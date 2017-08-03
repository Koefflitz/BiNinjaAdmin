package de.dk.bininja.admin.core;

public interface LogicController {
   public void showError(String msg, Object... args);
   public void show(String msg, Object... args);
   public void exit();
}