package zhong;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.named;

class DecorateTransforming implements Case {

    @Override
    public AgentBuilder ab() {
        return new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(RETRANSFORMATION).type(is(Foo.class))
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

    @Override
    public String toString() {
        return "DecorateTransforming";
    }

    static class Foo {
        void m() {}
    }
}
