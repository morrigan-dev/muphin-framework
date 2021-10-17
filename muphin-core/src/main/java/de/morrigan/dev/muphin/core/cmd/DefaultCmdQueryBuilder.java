package de.morrigan.dev.muphin.core.cmd;

/**
 * A command line query builder that simplifies the creation of multiple commands on a single line.
 * <p>
 * The {@code DefaultCmdQueryBuilder} contains some general helper methods for common commands. It is recommended that
 * you create your own query builder and extend it with your specific command line tasks. You have several ways to do
 * this.<br>
 * <ol>
 * <li>Create a class that inherits from this builder and add your specific methods using the
 * {@link #addCustomCommand(String)} method to add your commands. In this case, a fluent call to all methods is not
 * possible.</li>
 * <li>Just use this builder and add all your specific commands with the {@link #addCustomCommand(String)} method. You
 * can use a fluent call, but with a lot of different commands that are used on several places it is not recommended to
 * do it this way. It is only practical for a few isolated command line calls.
 * <li>Create a class that inherits from {@link GenericCmdQueryBuilder}. This generic query builder uses this builder as
 * a delegator, so that all common methods are available. But you can add you own specific methods and you can use a
 * fluent calling mechanism. This is the recommended way for larger command line support.
 * </ol>
 * <b>Examples</b><br>
 *
 *
 * Create your own class.
 *
 * <pre>
 * public class MyCmdQueryBuilder extends DefaultCmdQueryBuilder {
 *
 *   public DefaultCmdQueryBuilder startCalculator() {
 *     addCustomCommand("call calc.exe");
 *     return this;
 *   }
 * }
 * </pre>
 * <p>
 * Just use {@code DefaultCmdQueryBuilder}.
 *
 * <pre>
 * new DefaultCmdQueryBuilder()
 *     .changeDirectory("/tmp")
 *     .addCustomCommand("mkdir test")
 *     .changeDirectory("test")
 *     .addCustomCommand("touch test.txt")
 *     .getCommand();
 * </pre>
 *
 * @author morrigan
 * @since 0.0.1
 */
public class DefaultCmdQueryBuilder {

  private StringBuilder cmdQuery;
  private String separator;

  /**
   * Creates a new instance of this builder.
   *
   * @since 0.0.1
   */
  public DefaultCmdQueryBuilder() {
    super();
    this.cmdQuery = new StringBuilder();
    this.separator = " && ";
  }

  /**
   * Sets a separator that separates multiple commands. The default separator is '&&' to separate multiple windows
   * commands in a single command row.
   *
   * @param separator a separator
   * @return this query builder
   * @since 0.0.1
   */
  public DefaultCmdQueryBuilder withSeparator(String separator) {
    this.separator = separator;
    return this;
  }

  /**
   * Adds a change directory command with the given directory path to the query builder.
   *
   * @param directoryPath a path to a directory
   * @return this query builder
   * @since 0.0.1
   */
  public DefaultCmdQueryBuilder changeDirectory(String directoryPath) {
    prepareForNextCommand();
    this.cmdQuery.append("cd ").append(directoryPath);
    return this;
  }

  /**
   * Adds a git pull command to the query builder.
   * <p>
   * <b>Further information</b><br>
   * Depending on the type of cloned repository and the operating system, authorization must be ensured in advance. On
   * unix systems, access via ssh is recommended, since no further credentials are required.<br>
   * For Windows systems, the Windows Credential Manager can be used for access via https.<br>
   * {@code git config --global credential.helper manager}
   * <p>
   * It is important that git is able to authenticate against the remote server in some way so that no manual user
   * interaction is required!
   *
   * @return this query builder
   * @see https://snede.net/git-does-not-remember-username-password/
   * @since 0.0.1
   */
  public DefaultCmdQueryBuilder gitPull() {
    prepareForNextCommand();
    this.cmdQuery.append("git pull");
    return this;
  }

  /**
   * Adds a custom command to query builder.
   *
   * @param command a custom command
   * @return this query builder
   * @since 0.0.1
   */
  public DefaultCmdQueryBuilder addCustomCommand(String command) {
    prepareForNextCommand();
    this.cmdQuery.append(command);
    return this;
  }

  /**
   * Builds all command in this query builder to a single row command line command.
   *
   * @return a command line command
   * @since 0.0.1
   */
  public String getCommand() {
    return this.cmdQuery.toString();
  }

  protected void prepareForNextCommand() {
    if (this.cmdQuery.length() > 0) {
      this.cmdQuery.append(this.separator);
    }
  }
}