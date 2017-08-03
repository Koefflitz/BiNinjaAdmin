package de.dk.bininja.admin.ui;

public interface UI {
   public void show(String format, Object... args);
   public void showError(String format, Object... args);
   public void start();
   public void setConnected(boolean connected);
   public void close();
}