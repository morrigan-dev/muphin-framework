package de.morrigan.dev.test.muphin.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;

import de.morrigan.dev.muphin.core.cmd.CmdResponse;
import de.morrigan.dev.muphin.core.exception.MuphinFailureException;
import de.morrigan.dev.muphin.core.tasks.CmdTask;
import de.morrigan.dev.muphin.core.tasks.Task.Verification;

public class CmdTaskTest {

  private class CmdTaskMock extends CmdTask {

    @SafeVarargs
    public CmdTaskMock(String executor, String command, Verification<CmdResponse>... verifications) {
      super(executor, command, verifications);
    }

    @SafeVarargs
    public CmdTaskMock(String executor, boolean asynchron, String command, Verification<CmdResponse>... verifications) {
      super(executor, asynchron, command, verifications);
    }

    @Override
    protected DefaultExecutor getExecutor() {
      return new DefaultExecutor() {
        @Override
        public void execute(CommandLine command, ExecuteResultHandler handler) throws ExecuteException, IOException {
          CmdTaskTest.this.executed = true;
          handler.onProcessComplete(0);
        }
      };
    }
  }

  private boolean executed;

  @Before
  public void setup() {
    this.executed = false;
  }

  @Test
  public void testSuccessExitVerificationWithExitValueEqualToZero() {
    Verification<CmdResponse> sut = CmdTask.SUCCESS_EXIT;
    boolean result = sut.verify(new CmdResponse(0, null, null));
    assertTrue(result);
  }

  @Test
  public void testSuccessExitVerificationWithExitValueEqualToOne() {
    Verification<CmdResponse> sut = CmdTask.SUCCESS_EXIT;
    boolean result = sut.verify(new CmdResponse(1, null, null));
    assertFalse(result);
  }

  @Test
  public void testSuccessAllVerificationWithExitValueEqualToZero() {
    Verification<CmdResponse> sut = CmdTask.SUCCESS_ALL;
    boolean result = sut.verify(new CmdResponse(0, null, null));
    assertTrue(result);
  }

  @Test
  public void testSuccessAllVerificationWithExitValueEqualToOne() {
    Verification<CmdResponse> sut = CmdTask.SUCCESS_ALL;
    boolean result = sut.verify(new CmdResponse(1, null, null));
    assertFalse(result);
  }

  @Test
  public void testSuccessAllVerificationWithMessageValueSet() {
    Verification<CmdResponse> sut = CmdTask.SUCCESS_ALL;
    boolean result = sut.verify(new CmdResponse(0, "Error", null));
    assertFalse(result);
  }

  @Test
  public void testSuccessAllVerificationWithMessageValueEqualToBlank() {
    Verification<CmdResponse> sut = CmdTask.SUCCESS_ALL;
    boolean result = sut.verify(new CmdResponse(0, "", null));
    assertTrue(result);
  }

  @Test
  public void testSuccessAllVerificationWithexceptionValueSet() {
    Verification<CmdResponse> sut = CmdTask.SUCCESS_ALL;
    boolean result = sut.verify(new CmdResponse(0, null, new NullPointerException()));
    assertFalse(result);
  }

  @Test
  public void testVerifyMessageContainsAnyReturnsTrue() {
    boolean result = CmdTask.verifyMessageContainsAny(new CmdResponse(0, "Hello World!", null), "Hello", "Tom");
    assertTrue(result);
  }

  @Test
  public void testVerifyMessageContainsAnyReturnsFalse() {
    boolean result = CmdTask.verifyMessageContainsAny(new CmdResponse(0, "Hello World!", null), "Tom");
    assertFalse(result);
  }

  @Test
  public void testVerifyMessageContainsAllReturnsTrue() {
    boolean result = CmdTask.verifyMessageContainsAll(new CmdResponse(0, "Hello World!", null), "Hello", "World");
    assertTrue(result);
  }

  @Test
  public void testVerifyMessageContainsAllReturnsFalse() {
    boolean result = CmdTask.verifyMessageContainsAll(new CmdResponse(0, "Hello World!", null), "Hello", "Tom");
    assertFalse(result);
  }

  @Test
  public void testVerifyMessageNotContainsReturnsTrue() {
    boolean result = CmdTask.verifyMessageNotContains(new CmdResponse(0, "Hello World!", null), "Tom");
    assertTrue(result);
  }

  @Test
  public void testVerifyMessageNotContainsReturnsFalse() {
    boolean result = CmdTask.verifyMessageNotContains(new CmdResponse(0, "Hello World!", null), "Hello");
    assertFalse(result);
  }

  @Test
  public void testConstructionWithoutVerification() {
    CmdTask task = new CmdTask("cmd /c", "dir");
    String command = getCommand(task);
    assertThat(command, is(equalTo("cmd /c dir")));
    assertThat(getVerifications(task).length, is(equalTo(0)));
    assertThat(getAsynchron(task), is(equalTo(false)));
  }

  @Test
  public void testConstructionWithVerification() {
    CmdTask task = new CmdTask("cmd /c", "dir",
        responseData -> false,
        responseData -> true);
    assertThat(getCommand(task), is(equalTo("cmd /c dir")));
    assertThat(getVerifications(task).length, is(equalTo(2)));
    assertThat(getAsynchron(task), is(equalTo(false)));
  }

  @Test
  public void testConstructionWithAsynchron() {
    CmdTask task = new CmdTask("cmd /c", true, "dir",
        responseData -> false,
        responseData -> true);
    assertThat(getCommand(task), is(equalTo("cmd /c dir")));
    assertThat(getVerifications(task).length, is(equalTo(2)));
    assertThat(getAsynchron(task), is(equalTo(true)));
  }

  @Test
  public void testExecuteWithoutValidations() throws MuphinFailureException {
    CmdTask task = new CmdTaskMock("cmd /c", "dir");
    task.execute();
    assertTrue(this.executed);
  }

  @Test
  public void testExecuteWithSuccessValidation() throws MuphinFailureException {
    CmdTask task = new CmdTaskMock("cmd /c", "dir", responseData -> true);
    task.execute();
    assertTrue(this.executed);
  }

  @Test
  public void testExecuteWithFailingValidation() throws MuphinFailureException {
    CmdTask task = new CmdTaskMock("cmd /c", "dir", responseData -> false);
    MuphinFailureException exception = assertThrows(MuphinFailureException.class, () -> task.execute());
    assertThat(exception.getMessage(), containsString("cmd /c dir"));
    assertThat(exception.getMessage(), containsString("was not executed successfully"));
    assertThat(exception.getMessage(), containsString("exit value 0"));
  }

  private String getCommand(CmdTask sut) {
    try {
      return (String) ReflectionUtil.getFieldValue(CmdTask.class.getDeclaredField("command"), sut);
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private Verification<CmdResponse>[] getVerifications(CmdTask sut) {
    try {
      return (Verification<CmdResponse>[]) ReflectionUtil.getFieldValue(
          CmdTask.class.getDeclaredField("verifications"), sut);
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }

  private boolean getAsynchron(CmdTask sut) {
    try {
      return (boolean) ReflectionUtil.getFieldValue(CmdTask.class.getDeclaredField("asynchron"), sut);
    } catch (NoSuchFieldException | SecurityException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }

}
