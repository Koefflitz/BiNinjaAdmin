package de.dk.bininja.admin.core;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public interface LogicController {
   public void showError(String msg, Object... args);
   public void show(String msg, Object... args);
   public void exit();
}
