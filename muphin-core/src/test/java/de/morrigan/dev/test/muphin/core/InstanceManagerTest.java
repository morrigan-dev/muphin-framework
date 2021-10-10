package de.morrigan.dev.test.muphin.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.morrigan.dev.muphin.core.InstanceManager;
import de.morrigan.dev.muphin.core.phase.AbstractPhase;
import de.morrigan.dev.muphin.core.phase.SetupPhase;
import de.morrigan.dev.muphin.core.workflow.AbstractWorkflow;
import de.morrigan.dev.test.muphin.helper.TestPhaseA;
import de.morrigan.dev.test.muphin.helper.TestPhaseX;
import de.morrigan.dev.test.muphin.helper.WorkflowA;
import de.morrigan.dev.test.muphin.helper.WorkflowB;
import de.morrigan.dev.test.muphin.helper.WorkflowX;

public class InstanceManagerTest {

   private static final InstanceManager SUT = InstanceManager.getInstance();

   @Before
   public void setup() {
      SUT.clear();
   }

   @Test
   public void testGetWorkflowsAfterConstruction() {
      List<AbstractWorkflow> workflows = SUT.getWorkflows();
      assertThat(workflows, hasSize(0));
   }

   @Test
   public void testGetWorkflows() {
      AbstractWorkflow workflowA = SUT.getWorkflow(WorkflowA.class);
      AbstractWorkflow workflowB = SUT.getWorkflow(WorkflowB.class);
      assertThat(workflowA, is(notNullValue()));
      assertThat(workflowA.getClass(), is(equalTo(WorkflowA.class)));
      assertThat(workflowB, is(notNullValue()));
      assertThat(workflowB.getClass(), is(equalTo(WorkflowB.class)));

      List<AbstractWorkflow> workflows = SUT.getWorkflows();
      assertThat(workflows, hasSize(2));
      assertThat(workflows, containsInAnyOrder(workflowA, workflowB));
   }

   @Test
   public void testGetWorkflowWithInvalidWorkflowClass() {
      IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> SUT.getWorkflow(WorkflowX.class));
      assertThat(iae.getMessage(),
               containsString("Can't create a new instance from class de.morrigan.dev.test.muphin.helper.WorkflowX"));
   }

   @Test
   public void testGetPhase() {
      AbstractPhase setupPhase = SUT.getPhase(SetupPhase.class);
      AbstractPhase testPhaseA = SUT.getPhase(TestPhaseA.class);
      assertThat(setupPhase, is(notNullValue()));
      assertThat(setupPhase.getClass(), is(equalTo(SetupPhase.class)));
      assertThat(testPhaseA, is(notNullValue()));
      assertThat(testPhaseA.getClass(), is(equalTo(TestPhaseA.class)));
   }

   @Test
   public void testGetPhaseWithInvalidPhaseClass() {
      IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> SUT.getPhase(TestPhaseX.class));
      assertThat(iae.getMessage(),
               containsString("Can't create a new instance from class de.morrigan.dev.test.muphin.helper.TestPhaseX"));
   }
}
