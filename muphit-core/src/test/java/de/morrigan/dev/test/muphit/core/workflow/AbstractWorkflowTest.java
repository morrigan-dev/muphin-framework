package de.morrigan.dev.test.muphit.core.workflow;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;
import de.morrigan.dev.muphit.core.workflow.AbstractWorkflow;
import de.morrigan.dev.test.muphit.helper.TestPhaseA;
import de.morrigan.dev.test.muphit.helper.TestPhaseB;

public class AbstractWorkflowTest {

  private static final String NAME = "Workflow";
  private static final List<AbstractPhase<?>> PHASES = Arrays.asList(new TestPhaseA(), new TestPhaseB());

  private class TestWorkflow extends AbstractWorkflow {

    protected TestWorkflow() {
      super(NAME, PHASES);
    }
  }

  @Test
  public void testConstruction() {
    TestWorkflow sut = new TestWorkflow();
    assertThat(sut.getName(), is(equalTo(NAME)));
    assertThat(sut.getPhases(), hasItems(PHASES.get(0), PHASES.get(1)));
  }
}
