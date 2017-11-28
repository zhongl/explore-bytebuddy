package zhong.bytebuddy;

import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class XxxPlugin implements Plugin {

    @Override
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription) {

        return builder.method(named("toString")).intercept(FixedValue.value("transform"));
    }

    @Override
    public boolean matches(TypeDescription target) {
        return target.getDeclaredAnnotations().isAnnotationPresent(Foo.class);
    }

    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    @interface Foo {}
}
