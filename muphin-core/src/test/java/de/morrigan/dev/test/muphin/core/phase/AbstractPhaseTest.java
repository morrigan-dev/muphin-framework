package de.morrigan.dev.test.muphin.core.phase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;

public class AbstractPhaseTest {

   private static final String KIND = "Test";
   private static final String NAME = "Test Phase";
   private static final String DATA = "data";

   private interface ExecutionListener {
      void executed(String data);
   }

   private class TestPhase extends AbstractPhase {

      private ExecutionListener listener;
      private String data;

      protected TestPhase() {
         super(KIND, NAME);
         this.data = DATA;
      }

      protected TestPhase(ExecutionListener listener) {
         this();
         this.listener = listener;
      }

      public String getData() {
         return this.data;
      }

      @Override
      public boolean execute() {
         if (this.listener != null) {
            this.listener.executed(DATA);
         }
         return true;
      }
   }

   @Test
   public void testConstruction() {
      TestPhase sut = new TestPhase();
      assertThat(sut.getKind(), is(equalTo(KIND)));
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
