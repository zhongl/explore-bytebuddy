package zhongl;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.utility.JavaModule;
import org.springframework.http.client.AbstractClientHttpRequest;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class Agent {

    public static void premain(String args, Instrumentation inst) {
        new AgentBuilder.Default().disableClassFormatChanges()
                                  .type(isSubTypeOf(AbstractClientHttpRequest.class))
                                  .transform(new Transformer() {
                                      @Override
                                      public Builder<?> transform(Builder<?> b, TypeDescription td, ClassLoader cl, JavaModule m) {
                                          return b.visit(Advice.to(Probe.class).on(named("executeInternal")));
                                      }
                                  }).installOn(inst);
    }

    static class Probe {
        @Advice.OnMethodEnter
        static void enter(@Advice.Origin String name) {
            System.out.println("enter " + name);
        }
    }
}
