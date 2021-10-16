package de.morrigan.dev.test.muphin.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

import de.morrigan.dev.muphin.core.exception.MuphinFailureException;

public class MuphinFailureExceptionTest {

  @Test
  public void testConstructionWithMessage() {
    String message = "TestException";
    MuphinFailureException sut = new MuphinFailureException(message);
    assertThat(sut.getMessage(), is(equalTo(message)));
    assertThat(sut.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructionWithParameterizedMessage() {
    String message = "TestException {}";
    MuphinFailureException sut = new MuphinFailureException(message, 42);
    assertThat(sut.getMessage(), is(equalTo("TestException 42")));
    assertThat(sut.getCause(), is(nullValue()));
  }

  @Test
  public void testConstructionWithCauseAndMessage() {
    String message = "TestException";
    NullPointerException npe = new NullPointerException();
    MuphinFailureException sut = new MuphinFailureException(npe, message);
    assertThat(sut.getMessage(), is(equalTo("TestException")));
    assertThat(sut.getCause(), is(equalTo(npe)));
  }

  @Test
  public void testConstructionWithCauseAndParameterizedMessage() {
    String message = "TestException {}{}{}";
    NullPointerException npe = new NullPointerException();
    MuphinFailureException sut = new MuphinFailureException(npe, message, 1, 33, 7);
    assertThat(sut.getMessage(), is(equalTo("TestException 1337")));
    assertThat(sut.getCause(), is(equalTo(npe)));
  }
}
