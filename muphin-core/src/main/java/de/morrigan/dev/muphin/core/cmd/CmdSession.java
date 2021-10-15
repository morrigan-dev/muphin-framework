package de.morrigan.dev.muphin.core.cmd;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdSession {

   private static final Logger LOG = LoggerFactory.getLogger(CmdSession.class);

   private static final long DEFAULT_TIMEOUT = 10000;
   private static final int SLEEP_TIME_MS = 50;

   private OutputStream output;
   private OutputStream err;
   private InputStream input;
   private boolean finished;

   public CmdSession() {
      super();
   }

   public CmdResponse create() {
      return createDefaultExecutor();
//      return createRuntimeExec();
   }

   public CmdResponse execute(CommandLine command) {
      LOG.info("command: {}", command);
      Exception exception = null;
      try {
         PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(this.output)), true);
         BufferedReader reader = new BufferedReader(new InputStreamReader(this.input));
         String line = reader.readLine();
         this.finished = false;
         while (line != null) {
            /* Pass the value to command prompt/user input */
            writer.println(command.toString());
            LOG.info(line);
            line = reader.readLine();
         }
         this.finished = true;
      } catch (IOException e) {
         LOG.error(e.getMessage(), e);
         exception = e;
      }
      return new CmdResponse(0, this.output.toString(), exception);
   }

   public void close() {
      LOG.info("close");
      execute(new CommandLine("exit"));
   }

   public void waitFor(final long timeout) throws InterruptedException {

      final long until = System.currentTimeMillis() + timeout;

      while (!this.finished && System.currentTimeMillis() < until) {
         Thread.sleep(SLEEP_TIME_MS);
      }
   }

   private CmdResponse createRuntimeExec() {
      String[] command = new String[2];
      command[0] = "cmd";
      command[1] = "/k";

      CmdResponse response;
      try {
         Process p = Runtime.getRuntime().exec(command);
         this.output = p.getOutputStream();
         this.input = p.getInputStream();

         response = new CmdResponse(p.exitValue(), getMessage(p), null);
      } catch (IOException e) {
         LOG.error(e.getMessage(), e);
         response = new CmdResponse(-1, e.getMessage(), e);
      }
      return response;
   }

   private String getMessage(Process p) throws IOException {
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      StringBuilder msg = new StringBuilder();
      String input;
      while ((input = stdInput.readLine()) != null) {
         msg.append(input).append("\n");
      }
      return msg.toString();
   }

   private CmdResponse createDefaultExecutor() {
      LOG.info("createDefaultExecutor");
      DefaultExecutor executor = new DefaultExecutor();
      this.output = new ByteArrayOutputStream();
      this.err = new ByteArrayOutputStream();
      this.input = new ByteArrayInputStream(new byte[1024]);
      PumpStreamHandler streamHandler = new PumpStreamHandler(this.output, this.err, this.input);
      executor.setStreamHandler(streamHandler);
      DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
      Exception exception = null;
      try {
         executor.execute(new CommandLine("cmd").addArgument("/k"), handler);
         handler.waitFor(DEFAULT_TIMEOUT);
      } catch (IOException e) {
         LOG.error(e.getMessage(), e);
         exception = e;
      } catch (InterruptedException e) {
         LOG.error(e.getMessage(), e);
         exception = e;
         Thread.currentThread().interrupt();
      }
      String responseMsg = this.output.toString();
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
}