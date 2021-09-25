package de.morrigan.dev.test.muphit.core.phase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import de.morrigan.dev.muphit.core.phase.TearDownPhase;

public class TearDownPhaseTest {

  @Test
  public void testConstruction() {
    TearDownPhase sut = new TearDownPhase();
    assertThat(sut.getName(), is(equalTo(TearDownPhase.NAME)));
    assertThat(sut.getData(), is(equalTo("")));
  }
}
