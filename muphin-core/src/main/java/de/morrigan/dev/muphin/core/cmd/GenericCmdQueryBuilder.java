package de.morrigan.dev.muphin.core.cmd;

/**
 * A generic command line query builder that simplifies the creation of multiple commands.
 * <p>
 * With this query builder it is possible to create your own builder that inherits from this one and supports a fluent
 * calling mechanism. You only have to implement the {@link #getBuilder()} method and return the instance of your own
 * builder. This is necessary for this generic query builder so that it can return your own query builder type to
 * support the fluent notation.
 * <p>
 * <b>Examples</b><br>
 *
 * <pre>
 * public class MyGenericCmdQueryBuilder extends GenericCmdQueryBuilder<MyGenericCmdQueryBuilder> {
 *
 *   public MyGenericCmdQueryBuilder startMyApp() {
 *     addCustomCommand("start myApp.exe");
 *     return getBuilder();
 *   }
 *
 *   &#64;Override
 *   protected MyGenericCmdQueryBuilder getBuilder() {
 *     return this;
 *   }
 * }
 *
 * String command = new MyGenericCmdQueryBuilder()
 *     .changeDirectory("myapp")
 *     .startMyApp()
 *     .getCommand();
 * </pre>
 *
 * @author morrigan
 * @param <T> the type of your own query builder that inherits from this one
 * @since 0.0.1
 */
public abstract class GenericCmdQueryBuilder<T extends GenericCmdQueryBuilder<T>> {

  private DefaultCmdQueryBuilder delegate;

  /**
   * Create a new instance of this builder.
   *
   * @since 0.0.1
   */
  protected GenericCmdQueryBuilder() {
    super();
    this.delegate = new DefaultCmdQueryBuilder();
  }

  /**
   * Delegate to {@link DefaultCmdQueryBuilder#withSeparator(String)}
   *
   * @since 0.0.1
   */
  public T withSeparator(String separator) {
    this.delegate.withSeparator(separator);
    return getBuilder();
  }

  /**
   * Delegate to {@link DefaultCmdQueryBuilder#changeDirectory(String)}
   *
   * @since 0.0.1
   */
  public T changeDirectory(String directoryPath) {
    this.delegate.changeDirectory(directoryPath);
    return getBuilder();
  }

  /**
   * Delegate to {@link DefaultCmdQueryBuilder#gitPull()}
   *
   * @since 0.0.1
   */
  public T gitPull() {
    this.delegate.gitPull();
    return getBuilder();
  }

  /**
   * Delegate to {@link DefaultCmdQueryBuilder#addCustomCommand(String)}
   *
   * @since 0.0.1
   */
  public T addCustomCommand(String command) {
    this.delegate.addCustomCommand(command);
    return getBuilder();
  }

  /**
   * Delegate to {@link DefaultCmdQueryBuilder#getCommand()}
   *
   * @since 0.0.1
   */
  public String getCommand() {
    return this.delegate.getCommand();
  }

  /**
   * Delegate to {@link DefaultCmdQueryBuilder#prepareForNextCommand()}
   *
   * @since 0.0.1
   */
  protected void prepareForNextCommand() {
    this.delegate.prepareForNextCommand();
  }

  /**
   * @return this builder for fluent notation support
   * @since 0.0.1
   */
  protected abstract T getBuilder();
}
