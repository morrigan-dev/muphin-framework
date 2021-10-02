package de.morrigan.dev.test.muphit.core.runner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphit.core.runner.WorkflowRunner;
import de.morrigan.dev.test.muphit.helper.MuphitTests;

public class WorkflowRunnerTest {

  private static final Logger LOG = LoggerFactory.getLogger(WorkflowRunnerTest.class);

  private WorkflowRunner sut;

  @Before
  public void setup() {
    this.sut = new WorkflowRunner(MuphitTests.class);
  }

  @Test
  public void testConstructor() {
    TestClass testClass = this.sut.getTestClass();
    assertThat(testClass, is(notNullValue()));
    assertThat(testClass.getJavaClass(), is(equalTo(MuphitTests.class)));

    Description description = this.sut.getDescription();
    assertThat(description, is(notNullValue()));
    assertThat(description.getDisplayName(), containsString(MuphitTests.class.getName()));
  }

  @Test
  public void testGetDescription() {
    Description description = this.sut.getDescription();

    assertThat(description, is(notNullValue()));
    assertThat(description.getDisplayName(), containsString(MuphitTests.class.getName()));

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
    assertThat(workflowBSetupPhase, hasSize(3));
    assertThat(workflowBSetupPhase.get(0).getMethodName(), is(equalTo("testWorkflowBBeforeSetupPhase")));
    afterSetupPhase = Arrays.asList(
        workflowBSetupPhase.get(1).getMethodName(),
        workflowBSetupPhase.get(2).getMethodName());
    assertThat(afterSetupPhase,
        containsInAnyOrder("testWorkflowBAfterSetupPhase1", "testWorkflowBAfterSetupPhase2"));
  }

}