package net.ice.relic.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.tinylog.Logger;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class RelicAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		Logger.debug("[RelicAgent]: Starting Relic with debugging agent");

		new AgentBuilder.Default()
				.type(ElementMatchers.nameStartsWith("org.lwjgl.sdl"))
				.transform((builder, typeDescription, classLoader, module, protectionDomain) ->
						builder.visit(Advice.to(MethodCallAdvice.class)
								.on(ElementMatchers.isMethod()
										.and(ElementMatchers.isPublic())
										.and(ElementMatchers.not(ElementMatchers.isNative()))
										.and(ElementMatchers.not(ElementMatchers.nameContains("getLibrary")))
										.and(ElementMatchers.not(ElementMatchers.nameContains("nSDL")))
								)
						)
				)
				.ignore(ElementMatchers.none())
				.installOn(inst);
	}

	public static class MethodCallAdvice {
		@Advice.OnMethodEnter
		public static void enter(@Advice.Origin String method, @Advice.AllArguments Object[] args) {
			System.out.println("[Method Call] " + method + " called with args: " + Arrays.toString(args));
		}
	}
}
