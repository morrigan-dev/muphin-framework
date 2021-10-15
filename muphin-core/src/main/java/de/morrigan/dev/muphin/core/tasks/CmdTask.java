package de.morrigan.dev.muphin.core.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morrigan.dev.muphin.core.MuphinSession;
import de.morrigan.dev.muphin.core.cmd.CmdResponse;
import de.morrigan.dev.muphin.core.cmd.CmdSession;
import de.morrigan.dev.muphin.core.exception.MuphinFailureException;

public class CmdTask extends Task {

   public static final Verification<CmdResponse> SUCCESS_EXIT = responseData -> responseData.getExitValue() == 0;

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

   public static final boolean verifyMessageContainsAny(CmdResponse responseData, String... messages) {
      return StringUtils.containsAny(responseData.getMessage(), messages);
   }

   public static final boolean verifyMessageContainsAll(CmdResponse responseData, String... messages) {
      boolean result = false;
      if (messages != null) {
         result = true;
         for (String msg : messages) {
            result &= StringUtils.contains(responseData.getMessage(), msg);
            if (!result) {
               break;
            }
         }
      }
      return result;
   }

   public static final boolean verifyMessageNotContains(CmdResponse responseData, String... messages) {
      boolean result = false;
      if (messages != null) {
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

   private String command;
   private Verification<CmdResponse>[] verifications;
   private boolean asynchron;

   @SafeVarargs
   public CmdTask(String executor, String command, Verification<CmdResponse>... verifications) {
      super();
      this.command = StringUtils.join(executor, " ", command);
      this.verifications = verifications;
      this.asynchron = false;
   }

   @SafeVarargs
   public CmdTask(String executor, boolean asynchron, String command, Verification<CmdResponse>... verifications) {
      super();
      this.command = StringUtils.join(executor, " ", command);
      this.verifications = verifications;
      this.asynchron = asynchron;
   }

   @Override
   public void execute() throws MuphinFailureException {
      CmdResponse cmdResponse = executeCommand(CommandLine.parse(this.command));
      boolean success = true;
      if (this.verifications != null) {
         for (Verification<CmdResponse> verification : this.verifications) {
            success &= verification.verify(cmdResponse);
         }
      }
      if (!success) {
         throw new MuphinFailureException(cmdResponse.getException(),
                  "The task {} was not executed successfully and returned the exit value {}. Response of the executed task is {}",
                  this.command, cmdResponse.getExitValue(), cmdResponse.getMessage());
      }
   }

   private CmdResponse executeCommand(CommandLine commandLine) {
      LOG.info("command: {}", this.command);
      DefaultExecutor executor = new DefaultExecutor();
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();
      ByteArrayInputStream input = new ByteArrayInputStream(new byte[1024]);
      PumpStreamHandler streamHandler = new PumpStreamHandler(output, err, input);
      executor.setStreamHandler(streamHandler);
      DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
      Exception exception = null;
      try {
         executor.execute(commandLine, handler);
         while (!this.asynchron && !handler.hasResult()) {
            handler.waitFor(1000);
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
      LOG.info("responseMsg: {}", responseMsg);
      if (handler.hasResult()) {
         if (exception == null) {
            exception = handler.getException();
         }
         return new CmdResponse(handler.getExitValue(), responseMsg, exception);
      } else {
         return new CmdResponse(0, responseMsg, exception);
      }
   }

   private CmdSession getCmdSession() {
      MuphinSession muphinSession = MuphinSession.getInstance();
      Optional<CmdSession> optCommandLine = muphinSession.getData(MuphinSession.CMD_SESSION, CmdSession.class);
      CmdSession cmdSession;
      if (optCommandLine.isPresent()) {
         cmdSession = optCommandLine.get();
      } else {
         cmdSession = new CmdSession();
         cmdSession.create();
         muphinSession.putData(MuphinSession.CMD_SESSION, cmdSession);
      }
      return cmdSession;
   }
}
