package de.morrigan.dev.test.muphin.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.TestClass;

import de.morrigan.dev.muphin.core.WorkflowRunner;
import de.morrigan.dev.test.muphin.helper.MuphinTestSuite;
import de.morrigan.dev.test.muphin.helper.TestClassA1;

public class WorkflowRunnerTest {

  private WorkflowRunner sut;

  @Before
  public void setup() {
    this.sut = new WorkflowRunner(MuphinTestSuite.class);
  }

  @Test
  public void testConstructorWithMuphinTestsClass() {
    TestClass testClass = this.sut.getTestClass();
    assertThat(testClass, is(notNullValue()));
    assertThat(testClass.getJavaClass(), is(equalTo(MuphinTestSuite.class)));

    Description description = this.sut.getDescription();
    assertThat(description, is(notNullValue()));
    assertThat(description.getDisplayName(), containsString(MuphinTestSuite.class.getName()));
  }

  @Test
  public void testConstructorWithTestClassThatHasWorkflowTestAnnotation() {
    WorkflowRunner sut = new WorkflowRunner(TestClassA1.class);
    TestClass testClass = sut.getTestClass();
    assertThat(testClass, is(notNullValue()));
    assertThat(testClass.getJavaClass(), is(equalTo(TestClassA1.class)));

    Description description = sut.getDescription();
    assertThat(description, is(notNullValue()));
    assertThat(description.getDisplayName(), containsString(TestClassA1.class.getName()));

    List<String> executedTestMethod = new ArrayList<>();
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListener() {
      @Override
      public void testStarted(Description description) throws Exception {
        executedTestMethod.add(description.getDisplayName());
      }
    });
    sut.run(notifier);
    assertThat(executedTestMethod, hasSize(8));

    assertThat(executedTestMethod.get(0),
        is(equalTo("testTC1WorkflowABeforeSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)")));

    assertThat(executedTestMethod.get(1),
        is(equalTo("testTC1WorkflowAAfterSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)")));

    assertThat(executedTestMethod.get(2),
        is(equalTo("testTC1WorkflowABeforeTestPhaseA(de.morrigan.dev.test.muphin.helper.TestClassA1)")));

    assertThat(executedTestMethod.get(3),
        is(equalTo("testTC1WorkflowAAfterTestPhaseA(de.morrigan.dev.test.muphin.helper.TestClassA1)")));

    assertThat(executedTestMethod.get(4),
        is(equalTo("testTC1WorkflowABeforeTestPhaseB(de.morrigan.dev.test.muphin.helper.TestClassA1)")));

    assertThat(executedTestMethod.get(5),
        is(equalTo("testTC1WorkflowAAfterTestPhaseB(de.morrigan.dev.test.muphin.helper.TestClassA1)")));

    assertThat(executedTestMethod.get(6),
        is(equalTo("testTC1WorkflowABeforeTearDownPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)")));

    assertThat(executedTestMethod.get(7),
        is(equalTo("testTC1WorkflowAAfterTearDownPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)")));
  }

  @Test
  public void testGetDescription() {
    Description description = this.sut.getDescription();

    assertThat(description, is(notNullValue()));
    assertThat(description.getDisplayName(), containsString(MuphinTestSuite.class.getName()));

    ArrayList<Description> workflows = description.getChildren();
    assertThat(workflows, hasSize(2));
    assertThat(workflows.get(0).getDisplayName(), containsString("WorkflowA"));
    assertThat(workflows.get(1).getDisplayName(), containsString("WorkflowB"));

    ArrayList<Description> workflowAPhases = workflows.get(0).getChildren();
    assertThat(workflowAPhases, hasSize(4));
    assertThat(workflowAPhases.get(0).getDisplayName(), containsString("SetupPhase"));
    assertThat(workflowAPhases.get(1).getDisplayName(), containsString("TestPhaseA"));
    assertThat(workflowAPhases.get(2).getDisplayName(), containsString("TestPhaseB"));
    assertThat(workflowAPhases.get(3).getDisplayName(), containsString("TearDownPhase"));

    ArrayList<Description> workflowASetupPhase = workflowAPhases.get(0).getChildren();
    assertThat(workflowASetupPhase, hasSize(4));
    List<String> beforeSetupPhase = Arrays.asList(
        workflowASetupPhase.get(0).getMethodName(),
        workflowASetupPhase.get(1).getMethodName());
    assertThat(beforeSetupPhase,
        containsInAnyOrder("testTC1WorkflowABeforeSetupPhase", "testTC2WorkflowABeforeSetupPhase"));
    List<String> afterSetupPhase = Arrays.asList(
        workflowASetupPhase.get(2).getMethodName(),
        workflowASetupPhase.get(3).getMethodName());
    assertThat(afterSetupPhase,
        containsInAnyOrder("testTC1WorkflowAAfterSetupPhase", "testTC2WorkflowAAfterSetupPhase"));

    ArrayList<Description> workflowBPhases = workflows.get(1).getChildren();
    assertThat(workflowBPhases, hasSize(4));
    assertThat(workflowBPhases.get(0).getDisplayName(), containsString("SetupPhase"));
    assertThat(workflowBPhases.get(1).getDisplayName(), containsString("TestPhaseA"));
    assertThat(workflowBPhases.get(2).getDisplayName(), containsString("TestPhaseC"));
    assertThat(workflowBPhases.get(3).getDisplayName(), containsString("TearDownPhase"));

    ArrayList<Description> workflowBSetupPhase = workflowBPhases.get(0).getChildren();
    assertThat(workflowBSetupPhase, hasSize(4));
    beforeSetupPhase = Arrays.asList(
        workflowBSetupPhase.get(0).getMethodName(),
        workflowBSetupPhase.get(1).getMethodName());
    assertThat(beforeSetupPhase,
        containsInAnyOrder("testWorkflowBBeforeSetupPhase", "testWorkflowBBeforeSetupPhaseIgnored"));
    afterSetupPhase = Arrays.asList(
        workflowBSetupPhase.get(2).getMethodName(),
        workflowBSetupPhase.get(3).getMethodName());
    assertThat(afterSetupPhase,
        containsInAnyOrder("testWorkflowBAfterSetupPhase1", "testWorkflowBAfterSetupPhase2"));
  }

  @Test
  public void testRun() {
    List<String> executedTestMethod = new ArrayList<>();
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListener() {
      @Override
      public void testStarted(Description description) throws Exception {
        executedTestMethod.add(description.getDisplayName());
      }
    });
    this.sut.run(notifier);
    assertThat(executedTestMethod, hasSize(25));

    List<String> worklfowABeforeSetupPhase = Arrays.asList(executedTestMethod.get(0), executedTestMethod.get(1));
    assertThat(worklfowABeforeSetupPhase,
        containsInAnyOrder(
            "testTC1WorkflowABeforeSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)",
            "testTC2WorkflowABeforeSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA2)"));

    List<String> worklfowAAfterSetupPhase = Arrays.asList(executedTestMethod.get(2), executedTestMethod.get(3));
    assertThat(worklfowAAfterSetupPhase,
        containsInAnyOrder(
            "testTC1WorkflowAAfterSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)",
            "testTC2WorkflowAAfterSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA2)"));

    List<String> worklfowABeforeTestPhaseA = Arrays.asList(executedTestMethod.get(4), executedTestMethod.get(5));
    assertThat(worklfowABeforeTestPhaseA,
        containsInAnyOrder(
            "testTC1WorkflowABeforeTestPhaseA(de.morrigan.dev.test.muphin.helper.TestClassA1)",
            "testTC2WorkflowABeforeTestPhaseA(de.morrigan.dev.test.muphin.helper.TestClassA2)"));

    List<String> worklfowAAfterTestPhaseA = Arrays.asList(executedTestMethod.get(6), executedTestMethod.get(7));
    assertThat(worklfowAAfterTestPhaseA,
        containsInAnyOrder(
            "testTC1WorkflowAAfterTestPhaseA(de.morrigan.dev.test.muphin.helper.TestClassA1)",
            "testTC2WorkflowAAfterTestPhaseA(de.morrigan.dev.test.muphin.helper.TestClassA2)"));

    List<String> worklfowABeforeTestPhaseB = Arrays.asList(executedTestMethod.get(8), executedTestMethod.get(9));
    assertThat(worklfowABeforeTestPhaseB,
        containsInAnyOrder(
            "testTC1WorkflowABeforeTestPhaseB(de.morrigan.dev.test.muphin.helper.TestClassA1)",
            "testTC2WorkflowABeforeTestPhaseB(de.morrigan.dev.test.muphin.helper.TestClassA2)"));

    List<String> worklfowAAfterTestPhaseB = Arrays.asList(executedTestMethod.get(10), executedTestMethod.get(11));
    assertThat(worklfowAAfterTestPhaseB,
        containsInAnyOrder(
            "testTC1WorkflowAAfterTestPhaseB(de.morrigan.dev.test.muphin.helper.TestClassA1)",
            "testTC2WorkflowAAfterTestPhaseB(de.morrigan.dev.test.muphin.helper.TestClassA2)"));

    List<String> worklfowABeforeTearDownPhase = Arrays.asList(executedTestMethod.get(12), executedTestMethod.get(13));
    assertThat(worklfowABeforeTearDownPhase,
        containsInAnyOrder(
            "testTC1WorkflowABeforeTearDownPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)",
            "testTC2WorkflowABeforeTearDownPhase(de.morrigan.dev.test.muphin.helper.TestClassA2)"));

    List<String> worklfowAAfterTearDownPhase = Arrays.asList(executedTestMethod.get(14), executedTestMethod.get(15));
    assertThat(worklfowAAfterTearDownPhase,
        containsInAnyOrder(
            "testTC1WorkflowAAfterTearDownPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)",
            "testTC2WorkflowAAfterTearDownPhase(de.morrigan.dev.test.muphin.helper.TestClassA2)"));

    List<String> worklfowBBeforeSetupPhase = Arrays.asList(executedTestMethod.get(16));
    assertThat(worklfowBBeforeSetupPhase,
        containsInAnyOrder("testWorkflowBBeforeSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassB)"));

    List<String> worklfowBAfterSetupPhase = Arrays.asList(executedTestMethod.get(17), executedTestMethod.get(18));
    assertThat(worklfowBAfterSetupPhase,
        containsInAnyOrder(
            "testWorkflowBAfterSetupPhase1(de.morrigan.dev.test.muphin.helper.TestClassB)",
            "testWorkflowBAfterSetupPhase2(de.morrigan.dev.test.muphin.helper.TestClassB)"));

    List<String> worklfowBBeforeTestPhaseA = Arrays.asList(executedTestMethod.get(19));
    assertThat(worklfowBBeforeTestPhaseA,
        containsInAnyOrder(
            "testWorkflowBBeforeTestPhaseA(de.morrigan.dev.test.muphin.helper.TestClassB)"));

    List<String> worklfowBAfterTestPhaseA = Arrays.asList(executedTestMethod.get(20));
    assertThat(worklfowBAfterTestPhaseA,
        containsInAnyOrder(
            "testWorkflowBAfterTestPhaseA(de.morrigan.dev.test.muphin.helper.TestClassB)"));

    List<String> worklfowBBeforeTestPhaseC = Arrays.asList(executedTestMethod.get(21));
    assertThat(worklfowBBeforeTestPhaseC,
        containsInAnyOrder(
            "testWorkflowBBeforeTestPhaseC(de.morrigan.dev.test.muphin.helper.TestClassB)"));

    List<String> worklfowBAfterTestPhaseC = Arrays.asList(executedTestMethod.get(22));
    assertThat(worklfowBAfterTestPhaseC,
        containsInAnyOrder(
            "testWorkflowBAfterTestPhaseC(de.morrigan.dev.test.muphin.helper.TestClassB)"));

    List<String> worklfowBBeforeTearDownPhase = Arrays.asList(executedTestMethod.get(23));
    assertThat(worklfowBBeforeTearDownPhase,
        containsInAnyOrder(
            "testWorkflowBBeforeTearDownPhase(de.morrigan.dev.test.muphin.helper.TestClassB)"));

    List<String> worklfowBAfterTearDownPhase = Arrays.asList(executedTestMethod.get(24));
    assertThat(worklfowBAfterTearDownPhase,
        containsInAnyOrder(
            "testWorkflowBAfterTearDownPhase(de.morrigan.dev.test.muphin.helper.TestClassB)"));
  }

  @Test
  public void testFilterWithNoTestsRemainException() {
    assertThrows(NoTestsRemainException.class, () -> {
      Filter filter = new Filter() {

        @Override
        public boolean shouldRun(Description description) {
          return false;
        }

        @Override
        public String describe() {
          return "";
        }
      };
      this.sut.filter(filter);
    });
  }

  @Test
  public void testFilterWithNoTestsRemainExceptionDuringApply() throws NoTestsRemainException {
    assertThrows(NoTestsRemainException.class, () -> {
      Filter filter = new Filter() {

        @Override
        public void apply(Object child) throws NoTestsRemainException {
          throw new NoTestsRemainException();
        }

        @Override
        public boolean shouldRun(Description description) {
          return true;
        }

        @Override
        public String describe() {
          return "";
        }
      };
      this.sut.filter(filter);
    });
  }

  @Test
  public void testFilterBeforePhase() throws NoTestsRemainException {
    List<String> executedTestMethod = new ArrayList<>();
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListener() {
      @Override
      public void testStarted(Description description) throws Exception {
        executedTestMethod.add(description.getDisplayName());
      }
    });
    Filter filter = new Filter() {

      @Override
      public boolean shouldRun(Description description) {
        return "testTC1WorkflowABeforeSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)"
            .equals(description.getDisplayName());
      }

      @Override
      public String describe() {
        return "testTC1WorkflowABeforeSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)";
      }
    };
    this.sut.filter(filter);
    this.sut.run(notifier);

    assertThat(executedTestMethod, hasSize(1));
    assertThat(executedTestMethod.get(0),
        is(equalTo("testTC1WorkflowABeforeSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)")));
  }

  @Test
  public void testFilterAfterPhase() throws NoTestsRemainException {
    List<String> executedTestMethod = new ArrayList<>();
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListener() {
      @Override
      public void testStarted(Description description) throws Exception {
        executedTestMethod.add(description.getDisplayName());
      }
    });
    Filter filter = new Filter() {

      @Override
      public boolean shouldRun(Description description) {
        return "testTC1WorkflowAAfterSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)"
            .equals(description.getDisplayName());
      }

      @Override
      public String describe() {
        return "testTC1WorkflowAAfterSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)";
      }
    };
    this.sut.filter(filter);
    this.sut.run(notifier);

    assertThat(executedTestMethod, hasSize(1));
    assertThat(executedTestMethod.get(0),
        is(equalTo("testTC1WorkflowAAfterSetupPhase(de.morrigan.dev.test.muphin.helper.TestClassA1)")));
  }

}