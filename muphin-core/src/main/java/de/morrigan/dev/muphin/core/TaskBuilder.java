package de.morrigan.dev.muphin.core;

import java.util.ArrayList;
import java.util.List;

import de.morrigan.dev.muphin.core.exception.MuphinFailureException;
import de.morrigan.dev.muphin.core.tasks.Task;

public abstract class TaskBuilder<T extends TaskBuilder<T>> {

  private List<Task> tasks;

  protected TaskBuilder() {
    super();
    this.tasks = new ArrayList<>();
  }

  public T addCustomTask(int position, Task customTask) {
    this.tasks.add(position, customTask);
    return getBuilder();
  }

  public T addCustomTask(Task customTask) {
    this.tasks.add(customTask);
    return getBuilder();
  }

  public void execute() throws MuphinFailureException {
    for (Task task : this.tasks) {
      task.execute();
    }
  }

  protected List<Task> getTasks() {
    return new ArrayList<>(this.tasks);
  }

  protected abstract T getBuilder();

}
