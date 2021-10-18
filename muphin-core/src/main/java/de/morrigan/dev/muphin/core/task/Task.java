package de.morrigan.dev.muphin.core.task;

import de.morrigan.dev.muphin.core.exception.MuphinFailureException;
import de.morrigan.dev.muphin.core.phase.AbstractPhase;

/**
 * This interface should be implemented by all tasks that can be run in a {@link AbstractPhase}.
 *
 * @author morrigan
 * @since 0.0.1
 */
public interface Task {

  /**
   * A verification rule interface to verify a result of an execution of a task.
   *
   * @author morrigan
   * @param <T> the type of the response from the execution which should be checked
   * @since 0.0.1
   */
  @FunctionalInterface
  public interface Verification<T> {
    boolean verify(T resultData);
  }

  /**
   * Executes a task.
   *
   * @throws MuphinFailureException if any error occurred during exetion of the task
   * @since 0.0.1
   */
  void execute() throws MuphinFailureException;
}
