package de.morrigan.dev.muphit.core.phase;

/**
 * Defines any phase or task within a process or workflow.
 * <p>
 * A phase represents a single action that is executed and for which a testable state exists before or after. For
 * example, one phase can be copying a file. A subsequent phase can then be the call of an application that processes
 * this file. A third phase could correspond to the creation of an output file. The important thing about the phases is
 * that they can be controlled and executed individually. It must be ensured that the applications, which are needed to
 * a phase, can be switched off or deactivated in some way, so that a further processing of the data can be controlled
 * by the muphit-framework!
 *
 * @author morrigan
 * @param <T>
 * @since 0.0.1
 */
public abstract class AbstractPhase<T> {

  private final String name;
  private final T data;

  protected AbstractPhase(String name, T data) {
    super();

    this.name = name;
    this.data = data;
  }

  public String getName() {
    return this.name;
  }

  public T getData() {
    return this.data;
  }
}
