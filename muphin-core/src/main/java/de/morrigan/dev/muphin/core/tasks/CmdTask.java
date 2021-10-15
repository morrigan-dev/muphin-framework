package de.morrigan.dev.muphin.core.tasks;

import java.util.Optional;

import org.apache.commons.exec.CommandLine;
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

   private static final Logger LOG = LoggerFactory.getLogger(CmdTask.class);

   private String command;
   private Verification<CmdResponse> verification;

   public CmdTask(String command, Verification<CmdResponse> verification) {
      super();
      this.command = command;
      this.verification = verification;
   }

   @Override
   public void execute() throws MuphinFailureException {
      CmdResponse cmdResponse = getCmdSession().execute(new CommandLine(this.command));
      boolean success = this.verification.verify(cmdResponse);
      if (!success) {
         throw new MuphinFailureException(cmdResponse.getException(),
                  "The task {} was not executed successfully and returned the exit value {}. Response of the executed task is {}",
                  this.command, cmdResponse.getExitValue(), cmdResponse.getMessage());
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
