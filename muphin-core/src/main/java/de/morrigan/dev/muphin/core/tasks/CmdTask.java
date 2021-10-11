package de.morrigan.dev.muphin.core.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.MuphinSession;
import de.morrigan.dev.muphin.core.exception.MuphinFailureException;

public class CmdTask extends Task {

   public static final Verification<CmdResponse> SUCCESS_EXIT = responseData -> {
      return responseData.getExitValue() == 0;
   };

   public static final Verification<CmdResponse> SUCCESS_ALL = responseData -> {
      boolean exitSuccessfully = responseData.getExitValue() == 0;
      boolean emptyMessage = StringUtils.isBlank(responseData.getMessage());
      boolean noException = responseData.getException() == null;
      return exitSuccessfully && emptyMessage && noException;
   };

   public static final boolean verifySuccess(CmdResponse responseData, String... messages) {
      boolean exitSuccessfully = CmdTask.SUCCESS_EXIT.verify(responseData);
      boolean expectedMsg = StringUtils.containsAny(responseData.getMessage(), messages);
      return exitSuccessfully && expectedMsg;
   }

   public class CmdResponse {
      private int exitValue;
      private String message;
      private ExecuteException exception;

      public CmdResponse(int exitValue, String message, ExecuteException exception) {
         super();
         this.exitValue = exitValue;
         this.message = message;
         this.exception = exception;
      }

      public String getMessage() {
         return this.message;
      }

      public ExecuteException getException() {
         return this.exception;
      }

      public int getExitValue() {
         return this.exitValue;
      }
   }

   private static final Logger LOG = LoggerFactory.getLogger(CmdTask.class);

   private CommandLine commandLine;
   private DefaultExecutor executor;
   private String command;
   private Verification<CmdResponse> verification;

   public CmdTask(String command, Verification<CmdResponse> verification) {
      super();
      this.command = command;
      this.verification = verification;

      this.commandLine = new CommandLine(getExecutable());
      this.executor = new DefaultExecutor();
   }

   protected String getCommand() {
      return this.command;
   }

   protected CommandLine getExecutable() {
      return new CommandLine("cmd").addArgument("/k");
   }

   protected DefaultExecutor getExecutor() {
      return this.executor;
   }

   protected CommandLine getCommandLine() {
      return this.commandLine;
   }

   @Override
   public void execute() throws MuphinFailureException {
      getCmdSession();
      // TODO:
      // 1) CMD mit /k starten und offen halten
      // 2) execute greift auf Streams zu um Befehle an die cmd zu geben und Antworten abzurufen
      // 3) Auswertung der Response
      // Thomas Gattinger (10.10.2021)

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();
      ByteArrayInputStream input = new ByteArrayInputStream(new byte[1024]);
      PumpStreamHandler cmdOutHandler = new PumpStreamHandler(output, err, input);
      this.executor.setStreamHandler(cmdOutHandler);
      DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
      try {
         this.executor.execute(getCommandLine(), handler);
         handler.waitFor(1000);
      } catch (IOException e) {
         LOG.error(e.getMessage(), e);
      } catch (InterruptedException e) {
         LOG.error(e.getMessage(), e);
         Thread.currentThread().interrupt();
      }
      int exitValue = handler.getExitValue();
      ExecuteException exception = handler.getException();
      String response = output.toString();
      boolean success = this.verification.verify(new CmdResponse(exitValue, response, exception));
      if (!success) {
         throw new MuphinFailureException(exception,
                  "The task {} was not executed successfully and returned the exit value {}. Response of the executed task is {}",
                  this.command, exitValue, response);
      }
   }

   private void getCmdSession() {
      MuphinSession muphinSession = MuphinSession.getInstance();
      Optional<CommandLine> optCommandLine = muphinSession.getData(MuphinSession.CMD_SESSION, CommandLine.class);
      CommandLine commandLine;
      if (optCommandLine.isPresent()) {
         commandLine = optCommandLine.get();
      } else {
         commandLine = new CommandLine("cmd /k");
      }
   }
}
