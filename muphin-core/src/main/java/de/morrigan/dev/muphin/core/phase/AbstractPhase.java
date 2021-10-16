package de.morrigan.dev.muphin.core.phase;

/**
 * Defines any phase or task within a process or workflow.
 * <p>
 * A phase represents a single action that is executed and for which a testable state exists before or after. For
 * example, one phase can be copying a file. A subsequent phase can then be the call of an application that processes
 * this file. A third phase could correspond to the creation of an output file. The important thing about the phases is
 * that they can be controlled and executed individually. It must be ensured that the applications, which are needed to
 * a phase, can be switched off or deactivated in some way, so that a further processing of the data can be controlled
 * by the muphin-framework!
 *
 * @author morrigan
 * @since 0.0.1
 */
public abstract class AbstractPhase {

  public static final String INTERNAL_KIND = "InternalPhase";

  private final String kind;
  private final String name;

  protected AbstractPhase(String kind, String name) {
    super();
    this.kind = kind;
    this.name = name;
  }

  public String getKind() {
    return this.kind;
  }

  public String getName() {
    return this.name;
  }

  /**
   * Executes the action that belongs to this phase. Has to be implemented by all subclasses.
   *
   * @return {@code true} if the action was executed successful, otherwise {@code false}.
   */
  public abstract boolean execute();
}
