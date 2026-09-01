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
				// 1. Intercept your application methods
				.type(ElementMatchers.nameStartsWith("net.ice"))
				.transform((builder, typeDescription, classLoader, module, protectionDomain) ->
						builder.visit(Advice.to(MethodCallAdvice.class).on(ElementMatchers.isMethod()))
				)
				// 2. Intercept JDK ByteBuffer methods (requires ignoring default exclusions)
				.ignore(ElementMatchers.none())
				.type(ElementMatchers.isSubTypeOf(java.nio.Buffer.class))
				.transform((builder, typeDescription, classLoader, module, protectionDomain) ->
						builder.visit(Advice.to(ByteBufferAdvice.class).on(ElementMatchers.named("put")))
				)
				.installOn(inst);
	}

	public static class MethodCallAdvice {
		@Advice.OnMethodEnter
		public static void enter(@Advice.Origin String method, @Advice.AllArguments Object[] args) {
			System.out.println("[Method Call] " + method + " called with args: " + Arrays.toString(args));
		}
	}

	public static class ByteBufferAdvice {
		@Advice.OnMethodEnter
		public static void enter(@Advice.Origin String method, @Advice.AllArguments Object[] args, @Advice.This Object byteBuffer) {
			System.out.println("[ByteBuffer Alert] .put() called on " + byteBuffer.getClass().getName());
			if (args.length > 0) {
				System.out.println("  -> Data being written: " + Arrays.toString(args));
			}
		}
	}
}
