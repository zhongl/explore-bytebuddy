package zhongl;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.asm.Advice;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class Agent {

    public static void premain(String args, Instrumentation inst) {
        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .type(hasSuperType(named("org.springframework.http.client.AbstractClientHttpRequest")))
                .transform(new ForAdvice().advice(named("executeInternal"), "zhongl.Agent$Probe")).installOn(inst);
    }

    static class Probe {
        @Advice.OnMethodExit(onThrowable = Throwable.class)
        static void exit(@Advice.This ClientHttpRequest request, @Advice.Return ClientHttpResponse response) {
            try {
                System.out.printf("%s\t%d\t%s\n", request.getMethod(), response.getRawStatusCode(), request.getURI());
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }
}
