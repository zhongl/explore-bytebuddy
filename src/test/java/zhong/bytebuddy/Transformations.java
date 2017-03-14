package zhong.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

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

    class Foo {
        void m() {}
    }

    class Bar {
        void m() {}
    }

}
