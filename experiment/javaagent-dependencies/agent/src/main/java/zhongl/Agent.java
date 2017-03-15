package zhongl;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RawMatcher;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class Agent {

    public static void premain(String args, Instrumentation inst) {
        final ClassFileLocator sys = ClassFileLocator.ForClassLoader.of(ClassLoader.getSystemClassLoader());

        new AgentBuilder.Default()
                .with(new AgentBuilder.Listener.Adapter() {

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .disableClassFormatChanges()
                .type(new RawMatcher() {
                    @Override
                    public boolean matches(TypeDescription td, ClassLoader cl, JavaModule m, Class<?> cbr, ProtectionDomain pd) {
                        try {
                            final String name = "org.springframework.http.client.AbstractClientHttpRequest";
                            final Class<?> c = cl.loadClass(name);
                            return isSubTypeOf(c).matches(td);
                        } catch (Exception e) {
                            return false;
                        }
                    }
                })
                .transform(new Transformer() {
                    @Override
                    public Builder<?> transform(Builder<?> b, TypeDescription td, ClassLoader cl, JavaModule m) {
                        final ClassFileLocator cur = ClassFileLocator.ForClassLoader.of(cl);
                        final ClassFileLocator.Compound locator = new ClassFileLocator.Compound(cur, sys);
                        final TypeDescription desc = TypePool.Default.of(locator)
                                                                     .describe(Probe.class.getName())
                                                                     .resolve();
                        return b.visit(Advice.to(desc, locator).on(named("executeInternal")));
                    }
                }).installOn(inst);
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
