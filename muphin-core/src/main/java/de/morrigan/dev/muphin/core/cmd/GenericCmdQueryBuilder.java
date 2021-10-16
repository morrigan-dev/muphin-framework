package de.morrigan.dev.muphin.core.cmd;

public abstract class GenericCmdQueryBuilder<T extends GenericCmdQueryBuilder<T>> {

  private DefaultCmdQueryBuilder delegate;

  protected GenericCmdQueryBuilder() {
    super();
    this.delegate = new DefaultCmdQueryBuilder();
  }

  public T changeDirectory(String directoryPath) {
    this.delegate.changeDirectory(directoryPath);
    return getBuilder();
  }

  public T gitPull() {
    this.delegate.gitPull();
    return getBuilder();
  }

  public T addCustomCommand(String command) {
    this.delegate.addCustomCommand(command);
    return getBuilder();
  }

  public String getCommand() {
    return this.delegate.getCommand();
  }

  protected void prepareForNextCommand() {
    this.delegate.prepareForNextCommand();
  }

  protected abstract T getBuilder();
}
