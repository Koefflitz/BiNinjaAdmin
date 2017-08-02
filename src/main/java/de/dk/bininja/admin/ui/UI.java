package de.dk.bininja.admin.ui;

public interface UI {
   public void showError(String msg);
   public void showMessage(String msg);
   public void start();
   public void setConnected(boolean connected);
   public void close();
}