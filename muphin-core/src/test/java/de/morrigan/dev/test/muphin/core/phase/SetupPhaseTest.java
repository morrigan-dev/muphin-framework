package de.morrigan.dev.test.muphin.core.phase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import de.morrigan.dev.muphin.core.phase.SetupPhase;

public class SetupPhaseTest {

  @Test
  public void testConstruction() {
    SetupPhase sut = new SetupPhase();
    assertThat(sut.getName(), is(equalTo(SetupPhase.NAME)));
    assertThat(sut.getData(), is(equalTo("")));
  }
}
