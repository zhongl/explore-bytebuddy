package zhong.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Identified;
import net.bytebuddy.agent.builder.AgentBuilder.Identified.Narrowable;
import net.bytebuddy.agent.builder.AgentBuilder.LocationStrategy.ForClassLoader;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;
import org.junit.Test;

import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class TransformDefinition {


    @Test
    public void usage() throws Exception {
        final AgentBuilder ab = new AgentBuilder.Default().disableClassFormatChanges();
        final DSL a = wrap(ab);

        a.type(new AgentBuilder.RawMatcher() {
            @Override
            public boolean matches(TypeDescription td, ClassLoader cl, JavaModule m, Class<?> cbr, ProtectionDomain pd) {
                try {
                    return cl.loadClass("javax.servlet.HttpServlet").isAssignableFrom(cbr);
                } catch (Exception e) {
                    return false;
                }
            }
        }).with(Probe.class).on(named("service"));

    }


    public static DSL wrap(final AgentBuilder ab) {
        final ClassLoader loader = ab.getClass().getClassLoader();
        final ClassFileLocator locator = ForClassLoader.WEAK.classFileLocator(loader, JavaModule.UNSUPPORTED);
        final TypePool pool = AgentBuilder.PoolStrategy.Default.FAST.typePool(locator, loader);
        return new DSL() {

            @Override
            public Logical type(ElementMatcher<? super TypeDescription> matcher) {
                return new InnerLogic(ab.type(matcher));
            }

            @Override
            public Logical type(AgentBuilder.RawMatcher matcher) {
                return new InnerLogic(ab.type(matcher));
            }

            class InnerLogic implements Logical {
                private final Narrowable narrowable;

                public InnerLogic(Narrowable narrowable) {
                    this.narrowable = narrowable;
                }

                @Override
                public Logical or(AgentBuilder.RawMatcher matcher) {
                    return new InnerLogic(narrowable.or(matcher));
                }

                @Override
                public Logical or(ElementMatcher<? super TypeDescription> matcher) {
                    return new InnerLogic(narrowable.or(matcher));
                }

                @Override
                public Logical and(AgentBuilder.RawMatcher matcher) {
                    return new InnerLogic(narrowable.and(matcher));
                }

                @Override
                public Logical and(ElementMatcher<? super TypeDescription> matcher) {
                    return new InnerLogic(narrowable.and(matcher));
                }

                @Override
                public On with(Class<?> probe) {
                    return new InnerOn(narrowable, Advice.to(pool.describe(probe.getName()).resolve(), locator));
                }
            }

            class InnerOn implements On {
                private final Identified identified;
                private Advice advice;

                public InnerOn(Identified identified, Advice advice) {
                    this.identified = identified;
                    this.advice = advice;
                }

                @Override
                public With on(final ElementMatcher<? super MethodDescription> matcher) {
                    return new With() {

                        @Override
                        public On with(Class<?> probe) {
                            return new InnerOn(identified.transform(new Transformer() {
                                @Override
                                public Builder<?> transform(Builder<?> b, TypeDescription td, ClassLoader cl, JavaModule m) {
                                    return b.visit(advice.on(matcher));
                                }
                            }), Advice.to(pool.describe(probe.getName()).resolve(), locator));
                        }
                    };
                }
            }
        };
    }

    interface DSL {
        Logical type(ElementMatcher<? super TypeDescription> matcher);

        Logical type(AgentBuilder.RawMatcher matcher);

        interface With {
            On with(Class<?> probe);
        }

        interface Logical extends With {
            Logical or(AgentBuilder.RawMatcher matcher);

            Logical or(ElementMatcher<? super TypeDescription> matcher);

            Logical and(AgentBuilder.RawMatcher matcher);

            Logical and(ElementMatcher<? super TypeDescription> matcher);

        }

        interface On {
            With on(ElementMatcher<? super MethodDescription> matcher);
        }
    }

    static class Probe {
        @Advice.OnMethodEnter
        public static void enter(@Advice.Origin String name) {
            System.out.println("enter " + name);
        }
    }
}
