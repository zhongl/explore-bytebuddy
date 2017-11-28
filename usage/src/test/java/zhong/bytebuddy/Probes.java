package zhong.bytebuddy;

import net.bytebuddy.asm.Advice;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface Probes {

    class PrintEnter {
        @Advice.OnMethodEnter
        public static void enter(@Advice.Origin("#t") String type) {
            System.out.println("enter " + type);
        }

    }

    class PrintDecorate {
        @Advice.OnMethodEnter
        public static void enter(@Advice.Origin("#t") String type) {
            System.out.println("decorate " + type);
        }

    }

    class BindClassParam {
        @Advice.OnMethodEnter
        public static void enter(@Custom Object cls) {
            System.out.println(cls);
        }
    }

    @Retention(RUNTIME)
    @interface Custom {}
}
