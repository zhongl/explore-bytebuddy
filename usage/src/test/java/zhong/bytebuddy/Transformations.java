package zhong.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.net.URL;
import java.net.URLClassLoader;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.named;

public interface Transformations {
    class Multiples extends Case {

        @Override
        public AgentBuilder ab() {
            return super.ab()
                        .type(is(Foo.class))
                        .transform(new AgentBuilder.Transformer() {
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> b, TypeDescription td, ClassLoader cl, JavaModule m) {
                                return b.visit(Advice.to(Probes.PrintEnter.class).on(named("m")));
                            }
                        })
                        .type(is(Bar.class))
                        .transform(new AgentBuilder.Transformer() {
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> b, TypeDescription td, ClassLoader cl, JavaModule m) {
                                return b.visit(Advice.to(Probes.PrintEnter.class).on(named("m")));
                            }
                        });
        }

        @Override
        public void run() throws Exception {
            new Foo().m();
            new Bar().m();
        }

    }

    class Decorating extends Case {

        @Override
        public AgentBuilder ab() {
            return super.ab()
                        .type(is(Foo.class))
                        .transform(new AgentBuilder.Transformer() {
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> b, TypeDescription td, ClassLoader cl, JavaModule m) {
                                return b.visit(Advice.to(Probes.PrintDecorate.class).on(named("m")));
                            }
                        })
                        .transform(new AgentBuilder.Transformer() {
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> b, TypeDescription td, ClassLoader cl, JavaModule m) {
                                return b.visit(Advice.to(Probes.PrintEnter.class).on(named("m")));
                            }
                        });
        }

        @Override
        public void run() throws Exception {
            new Foo().m();
        }

    }

    class BindParams extends Case {

        @Override
        public AgentBuilder ab() {
            final URLClassLoader loader = new URLClassLoader(new URL[0]);
            final Class<?> aClass = new ByteBuddy()
                    .subclass(Object.class)
                    .method(ElementMatchers.named("toString"))
                    .intercept(FixedValue.value("Hello World!"))
                    .make().load(loader).getLoaded();
            final Advice.WithCustomMapping mapping = Advice.withCustomMapping().bind(Probes.Custom.class, aClass);
            return super.ab().with(new AgentBuilder.Listener.Adapter() {
                @Override
                public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                    throwable.printStackTrace();
                }
            }).type(is(Foo.class)).transform(new ForAdvice(mapping).advice(named("m"), Probes.BindClassParam.class.getName()));
        }

        @Override
        public void run() throws Exception {
            new Foo().m();
        }
    }

    class Foo {
        void m() {}
    }

    class Bar {
        void m() {}
    }

}
