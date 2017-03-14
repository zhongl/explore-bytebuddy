package zhong;

import net.bytebuddy.agent.builder.AgentBuilder;

interface Case {
    AgentBuilder ab();

    void run() throws Exception;
}
