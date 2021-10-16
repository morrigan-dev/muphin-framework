package de.morrigan.dev.test.muphin.core.cmd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.Test;

import de.morrigan.dev.muphin.core.cmd.CmdResponse;

public class CmdResponseTest {

  @Test
  public void testConstruction() {
    int exitValue = 4711;
    String message = "Test";
    Exception exception = new IOException();
    CmdResponse sut = new CmdResponse(exitValue, message, exception);
    assertThat(sut.getExitValue(), is(equalTo(exitValue)));
    assertThat(sut.getMessage(), is(equalTo(message)));
    assertThat(sut.getException(), is(equalTo(exception)));
  }
}
