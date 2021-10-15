package de.morrigan.dev.test.muphin.core.cmd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.commons.exec.CommandLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.morrigan.dev.muphin.core.cmd.CmdResponse;
import de.morrigan.dev.muphin.core.cmd.CmdSession;

public class CmdSessionTest {

   private CmdSession sut;

   @Before
   public void setup() {
      this.sut = new CmdSession();
   }

   @After
   public void tearDown() {
      this.sut.close();
   }

   @Test
   public void testCreate() {
      CmdResponse cmdResponse = this.sut.create();
      assertThat(cmdResponse, is(notNullValue()));
      assertThat(cmdResponse.getExitValue(), is(equalTo(0)));
      assertThat(cmdResponse.getException(), is(equalTo(null)));
   }

   @Test
   public void testExecute() {
      this.sut.create();
      CmdResponse cmdResponse = this.sut.execute(new CommandLine("dir"));
      assertThat(cmdResponse, is(notNullValue()));
      assertThat(cmdResponse.getExitValue(), is(equalTo(0)));
      assertThat(cmdResponse.getException(), is(equalTo(null)));
   }

}
