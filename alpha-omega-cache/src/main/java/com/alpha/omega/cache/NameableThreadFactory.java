package com.alpha.omega.cache;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alpha.omega.cache.client.CacheConstants.PERIOD;

public class NameableThreadFactory implements ThreadFactory {

	private final AtomicInteger threadCount = new AtomicInteger();
	final String name;

	private static String generateThreadName(String name, Integer threadCount){
		return new StringBuilder(name)
				.append(PERIOD)
				.append(threadCount)
				.toString();
	}

	public NameableThreadFactory(String name) {
		this.name = generateThreadName(name, threadCount.incrementAndGet());
	}

	public NameableThreadFactory(String name, Integer initialCount) {
		this.threadCount.set(initialCount);
		this.name = generateThreadName(name, threadCount.get());
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setName(generateThreadName(name, threadCount.get()));
		thread.setDaemon(true);
		return thread;
	}
}
