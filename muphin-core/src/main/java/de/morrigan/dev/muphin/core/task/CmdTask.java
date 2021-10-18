package de.morrigan.dev.muphin.core.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.cmd.CmdResponse;
import de.morrigan.dev.muphin.core.exception.MuphinFailureException;

/**
 * Provides a way to execute command line tasks. A command line command is executed using {@link DefaultExecutor} and
 * its result is checked against the specified verification rules. There are several standard verifications available in
 * this class that can be used when creating a {@code CmdTask}.
 * <p>
 * <b>Examples</b><br>
 *
 * <pre>
 * CmdTask cmdTask1 = new CmdTask("cmd /c", "cd /", CmdTask.SUCCESS_ALL);
 *
 * CmdTask cmdTask2 = new CmdTask("cmd /c", "myHelloWorldScript.sh",
 *     CmdTask.SUCCESS_EXIT,
 *     response -> response.getMessage().contains("Hello World!"));
 * </pre>
 *
 * @author morrigan
 * @since 0.0.1
 */
public class CmdTask implements Task {

  /**
   * Checks if the exit value of a command line execution is zero.<br>
   * If so, the check returns {@code true}. Otherwise {@code false}.
   *
   * @since 0.0.1
   */
  public static final Verification<CmdResponse> SUCCESS_EXIT = responseData -> responseData.getExitValue() == 0;

  /**
   * Checks all information that is returned by a command line execution.<br>
   * The check returns {@code true} only if the following conditions are true:
   * <ul>
   * <li>exit value is equal to zero</li>
   * <li>message is empty or blank (contains only whitespace)</li>
   * <li>no exception occurred</li>
   * </ul>
   * In all other cases {@code false} is returned. This Verification rule is useful when a command line command returns
   * nothing in case of success and returns prints out an error message in case of an failure.
   *
   * @since 0.0.1
   */
  public static final Verification<CmdResponse> SUCCESS_ALL = responseData -> {
    boolean exitSuccessfully = responseData.getExitValue() == 0;
    boolean emptyMessage = StringUtils.isBlank(responseData.getMessage());
    boolean noException = responseData.getException() == null;
    return exitSuccessfully && emptyMessage && noException;
  };

  /**
   * Checks if the exit value of a command line execution is zero and the return message contains any of the given
   * messages. If no messages are given then they will be ignored.
   *
   * @param responseData response data of a command line execution
   * @param messages messages of which at least one must match
   * @return a verification rule
   * @since 0.0.1
   */
  public static final boolean verifySuccess(CmdResponse responseData, String... messages) {
    boolean exitSuccessfully = CmdTask.SUCCESS_EXIT.verify(responseData);
    boolean expectedMsg = messages.length == 0 ? true : StringUtils.containsAny(responseData.getMessage(), messages);
    return exitSuccessfully && expectedMsg;
  }

  /**
   * Checks if any of the given messages contains in the response message of a command line execution.
   *
   * @param responseData response data of a command line execution
   * @param messages messages of which at least one must match
   * @return a verification rule
   * @since 0.0.1
   */
  public static final boolean verifyMessageContainsAny(CmdResponse responseData, String... messages) {
    return StringUtils.containsAny(responseData.getMessage(), messages);
  }

  /**
   * Checks if all of the given messages contains in the response message of a command line execution. If no message is
   * given this rule returns {@code true}.
   *
   * @param responseData response data of a command line execution
   * @param messages messages of which all must match
   * @return a verification rule
   * @since 0.0.1
   */
  public static final boolean verifyMessageContainsAll(CmdResponse responseData, String... messages) {
    boolean result = true;
    for (String msg : messages) {
      result &= StringUtils.contains(responseData.getMessage(), msg);
      if (!result) {
        break;
      }
    }
    return result;
  }

  /**
   * Checks if none of the given messages contains in the response message of a command line execution. If no message is
   * given this rule returns {@code false}.
   *
   * @param responseData response data of a command line execution
   * @param messages messages of which none need not contains in response message
   * @return a verification rule
   * @since 0.0.1
   */
  public static final boolean verifyMessageNotContains(CmdResponse responseData, String... messages) {
    boolean result = false;
    if (messages.length > 0) {
      result = true;
      for (String msg : messages) {
        result &= !StringUtils.contains(responseData.getMessage(), msg);
        if (!result) {
          break;
        }
      }
    }
    return result;
  }

  private static final Logger LOG = LoggerFactory.getLogger(CmdTask.class);

  private static final long DEFAULT_TIMEOUT = 30000;

  private String command;
  private Verification<CmdResponse>[] verifications;
  private boolean asynchron;
  private long timeout;

  /**
   * Creates a new instance of this {@code CmdTask} and sets the given parameter.
   * <p>
   * <b>Synchrony execution</b><br>
   * After the command line execution this task waits for execution finish up to the default timeout of 30 seconds. If a
   * result is received within this time all given verifications are executed. Otherwise a
   * {@link MuphinFailureException} exception is thrown.
   *
   * @param executor a command line command that starts a script or an application (e.g. cmd /c)
   * @param command a command to be executed via the {@code executor} script or application
   * @param verifications verifications to check the response of a command line execution
   * @since 0.0.1
   */
  @SafeVarargs
  public CmdTask(String executor, String command, Verification<CmdResponse>... verifications) {
    super();
    this.command = StringUtils.join(executor, " ", command);
    this.verifications = verifications;
    this.asynchron = false;
    this.timeout = DEFAULT_TIMEOUT;
  }

  /**
   * Creates a new instance of this {@code CmdTask} and sets the given parameter.
   * <p>
   * <b>Synchrony execution</b><br>
   * After the command line execution this task waits for execution finish up to the specified timeout. If a result is
   * received within this time all given verifications are executed. Otherwise a {@link MuphinFailureException}
   * exception is thrown.
   *
   * @param executor a command line command that starts a script or an application (e.g. cmd /c)
   * @param command a command to be executed via the {@code executor} script or application
   * @param timeout a timeout in milliseconds
   * @param verifications verifications to check the response of a command line execution
   * @since 0.0.1
   */
  @SafeVarargs
  public CmdTask(String executor, String command, long timeout, Verification<CmdResponse>... verifications) {
    super();
    this.command = StringUtils.join(executor, " ", command);
    this.verifications = verifications;
    this.asynchron = false;
    this.timeout = timeout;
  }

  /**
   * Creates a new instance of this {@code CmdTask} and sets the given parameter.
   * <p>
   * <b>Asynchrony execution</b><br>
   * This task does not wait for any response of the started command line task. For this reason, only an internal
   * verification check is performed, which checks for exceptions that occurred during execution of the command line
   * task.
   *
   * @param executor a command line command that starts a script or an application (e.g. cmd /c)
   * @param command a command to be executed via the {@code executor} script or application
   * @since 0.0.1
   */
  @SuppressWarnings("unchecked")
  public CmdTask(String executor, String command) {
    super();
    this.command = StringUtils.join(executor, " ", command);
    this.verifications = new Verification[] {
        CmdTask.SUCCESS_ALL
    };
    this.asynchron = true;
  }

  /**
   * Executes the commands and run all available verification checks against the response of the executed command. If a
   * verification rule fail, a {@link MuphinFailureException} is thrown.
   *
   * @throws MuphinFailureException if a verification rule fails
   * @since 0.0.1
   */
  @Override
  public void execute() throws MuphinFailureException {
    CmdResponse cmdResponse = executeCommand(CommandLine.parse(this.command));
    boolean success = true;
    for (Verification<CmdResponse> verification : this.verifications) {
      success &= verification.verify(cmdResponse);
    }
    if (!success) {
      throw new MuphinFailureException(cmdResponse.getException(),
          "The task {} was not executed successfully and returned the exit value {}. Response of the executed task is {}",
          this.command, cmdResponse.getExitValue(), cmdResponse.getMessage());
    }
  }

  /**
   * @return a executor to run the commands
   * @since 0.0.1
   */
  protected DefaultExecutor getExecutor() {
    return new DefaultExecutor();
  }

  /**
   * @return a result handler that provides the result of a command line execution (e.g. exit value, message, exception)
   * @since 0.0.1
   */
  protected DefaultExecuteResultHandler getExecuteResultHandler() {
    return new DefaultExecuteResultHandler();
  }

  private CmdResponse executeCommand(CommandLine commandLine) throws MuphinFailureException {
    LOG.debug("command: {}", this.command);
    DefaultExecutor executor = getExecutor();
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    ByteArrayInputStream input = new ByteArrayInputStream(new byte[1024]);
    PumpStreamHandler streamHandler = new PumpStreamHandler(output, err, input);
    executor.setStreamHandler(streamHandler);
    DefaultExecuteResultHandler handler = getExecuteResultHandler();
    Exception exception = null;
    try {
      executor.execute(commandLine, handler);
      while (!this.asynchron && !handler.hasResult()) {
        handler.waitFor(this.timeout);
        if (!handler.hasResult()) {
          throw new MuphinFailureException("The task '{}' does not finish execution within {} seconds.", this.command,
              this.timeout / 1000.0);
        }
      }
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      exception = e;
    } catch (InterruptedException e) {
      LOG.error(e.getMessage(), e);
      exception = e;
      Thread.currentThread().interrupt();
    }
    String responseMsg = output.toString();
    int exitValue = 0;
    LOG.debug("responseMsg: {}", responseMsg);
    if (handler.hasResult()) {
      exitValue = handler.getExitValue();
      if (exception == null) {
        exception = handler.getException();
      } else {
        exitValue = -1;
        responseMsg = exception.getMessage();
      }
    }
    return new CmdResponse(exitValue, responseMsg, exception);
  }
}
