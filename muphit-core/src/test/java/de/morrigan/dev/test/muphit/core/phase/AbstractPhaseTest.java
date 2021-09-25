package de.morrigan.dev.test.muphit.core.phase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;

public class AbstractPhaseTest {

  private static final String NAME = "Test Phase";
  private static final String DATA = "data";

  private class TestPhase extends AbstractPhase<String> {

    protected TestPhase() {
      super(NAME, DATA);
    }
  }

  @Test
  public void testConstruction() {
    TestPhase sut = new TestPhase();
    assertThat(sut.getName(), is(equalTo(NAME)));
    assertThat(sut.getData(), is(equalTo(DATA)));
  }
}
