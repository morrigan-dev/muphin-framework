package de.morrigan.dev.test.muphit.core.phase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.morrigan.dev.muphit.core.phase.AbstractPhase;

public class AbstractPhaseTest {

  private static final String NAME = "Test Phase";
  private static final String DATA = "data";

  private interface ExecutionListener {
    void executed(String data);
  }

  private class TestPhase extends AbstractPhase<String> {

    private ExecutionListener listener;

    protected TestPhase() {
      super(NAME, DATA);
    }

    protected TestPhase(ExecutionListener listener) {
      this();
      this.listener = listener;
    }

    @Override
    public boolean execute(String data) {
      if (this.listener != null) {
        this.listener.executed(data);
      }
      return true;
    }
  }

  @Test
  public void testConstruction() {
    TestPhase sut = new TestPhase();
    assertThat(sut.getName(), is(equalTo(NAME)));
    assertThat(sut.getData(), is(equalTo(DATA)));
  }

  @Test
  public void testExecute() {
    final List<String> executedData = new ArrayList<>();
    TestPhase sut = new TestPhase(data -> executedData.add(data));
    sut.execute();
    assertThat(executedData, contains(DATA));
  }
}
