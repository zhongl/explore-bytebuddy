package zhong.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;

public abstract class Case {
    public AgentBuilder ab() {
        return new AgentBuilder.Default().disableClassFormatChanges().with(RETRANSFORMATION);
    }

    public abstract void run() throws Exception;

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
