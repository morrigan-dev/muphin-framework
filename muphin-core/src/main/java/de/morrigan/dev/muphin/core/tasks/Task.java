package de.morrigan.dev.muphin.core.tasks;

import de.morrigan.dev.muphin.core.exception.MuphinFailureException;

public abstract class Task {

   @FunctionalInterface
   public interface Verification<T> {
      boolean verify(T resultData);
   }

   public abstract void execute() throws MuphinFailureException;
}
