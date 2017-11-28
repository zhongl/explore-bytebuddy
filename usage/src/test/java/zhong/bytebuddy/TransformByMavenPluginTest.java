package zhong.bytebuddy;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TransformByMavenPluginTest {

    @XxxPlugin.Foo
    static class Bar {

    }

    @Test
    public void should_rebase_bar() throws Exception {
        assertThat(new Bar().toString(), is("transform"));
    }
}
