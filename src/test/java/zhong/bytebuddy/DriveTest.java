package zhong.bytebuddy;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;

@RunWith(Parameterized.class)
public class DriveTest {

    private final ResettableClassFileTransformer transformer;
    private final Case c;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Case> transformers() {
        return Arrays.asList(
                new Transformations.Multiples(),
                new Transformations.Decorating()
        );
    }

    public DriveTest(Case c) {
        this.c = c;
        this.transformer = c.ab().installOn(ByteBuddyAgent.getInstrumentation());
    }

    @BeforeClass
    public static void setUp() throws Exception {
        ByteBuddyAgent.install();
    }

    @After
    public void tearDown() throws Exception {
        transformer.reset(ByteBuddyAgent.getInstrumentation(), RETRANSFORMATION);
    }

    @Test
    public void run() throws Exception {
        c.run();
    }
}
