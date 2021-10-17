package de.morrigan.dev.test.muphin.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;

import de.morrigan.dev.muphin.core.exception.MuphinFailureException;
import de.morrigan.dev.muphin.core.tasks.Task;
import de.morrigan.dev.muphin.core.tasks.TaskBuilder;

public class TaskBuilderTest {

  private class TestTaskBuilder extends TaskBuilder<TestTaskBuilder> {

    @Override
    protected TestTaskBuilder getBuilder() {
      return this;
    }

    @Override
    protected List<Task> getTasks() {
      return super.getTasks();
    }
  }

  private class TestTask extends Task {

    private String name;
    private boolean executed;
    private long executionTime;

    public TestTask(String name) {
      super();
      this.name = name;
      this.executed = false;
    }

    @Override
    public void execute() throws MuphinFailureException {
      this.executed = true;
      this.executionTime = System.nanoTime();
    }
  }

  private TestTaskBuilder sut;

  @Before
  public void setup() {
    this.sut = new TestTaskBuilder();
  }

  @Test
  public void testConstruction() {
    List<Task> tasks = getTasks(this.sut);
    assertThat(tasks, is(notNullValue()));
    assertThat(tasks, hasSize(0));
  }

  @Test
  public void testAddCustomTaskWithIndex() {
    String nameA = "TestTaskA";
    String nameB = "TestTaskB";
    this.sut.addCustomTask(0, new TestTask(nameA));
    List<Task> tasks = getTasks(this.sut);
    assertThat(tasks, hasSize(1));
    TestTaskBuilder builder = this.sut.addCustomTask(0, new TestTask(nameB));
    assertThat(builder, is(equalTo(this.sut)));
    assertThat(tasks, hasSize(2));
    String name = ((TestTask) tasks.get(0)).name;
    assertThat(name, is(equalTo(nameB)));
  }

  @Test
  public void testAddCustomTask() {
    String expectedName = "TestTask";
    this.sut.addCustomTask(new TestTask(expectedName));
    List<Task> tasks = getTasks(this.sut);
    assertThat(tasks, hasSize(1));
    String name = ((TestTask) tasks.get(0)).name;
    assertThat(name, is(equalTo(expectedName)));
  }

  @Test
  public void testExecuteWithoutTasks() throws MuphinFailureException {
    this.sut.execute();
    assertTrue(true);
  }

  @Test
  public void testExecuteWithTasks() throws MuphinFailureException {
    TestTask testTaskA = new TestTask("TestTaskA");
    TestTask testTaskB = new TestTask("TestTaskB");
    this.sut.addCustomTask(testTaskA);
    this.sut.addCustomTask(testTaskB);
    assertThat(testTaskA.executed, is(equalTo(false)));
    assertThat(testTaskB.executed, is(equalTo(false)));

    this.sut.execute();

    assertThat(testTaskA.executed, is(equalTo(true)));
    assertThat(testTaskB.executed, is(equalTo(true)));
    assertThat(testTaskA.executionTime, is(lessThan(testTaskB.executionTime)));
  }

  @Test
  public void testGetTasks() {
    TestTask testTaskA = new TestTask("TestTaskA");
    this.sut.addCustomTask(testTaskA);

    List<Task> tasks = this.sut.getTasks();
    assertThat(tasks, hasSize(1));

    TestTask testTaskB = new TestTask("TestTaskB");
    tasks.add(testTaskB);

    assertThat(this.sut.getTasks(), hasSize(1));
  }

  @SuppressWarnings("unchecked")
  private List<Task> getTasks(TaskBuilder<TestTaskBuilder> sut) {
    try {
      return (List<Task>) ReflectionUtil.getFieldValue(TaskBuilder.class.getDeclaredField("tasks"), sut);
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }
}
