package de.morrigan.dev.test.muphin.core.phase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import de.morrigan.dev.muphin.core.phase.NoPhase;

public class NoPhaseTest {

  @Test
  public void testConstruction() {
    NoPhase sut = new NoPhase();
    assertThat(sut.getName(), is(equalTo(NoPhase.class.getSimpleName())));
  }

  @Test
  public void testExecute() {
    NoPhase sut = new NoPhase();
    assertThat(sut.execute(), is(equalTo(true)));
  }
}
