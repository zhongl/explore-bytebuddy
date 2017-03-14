package zhong;

import net.bytebuddy.asm.Advice;

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
}
