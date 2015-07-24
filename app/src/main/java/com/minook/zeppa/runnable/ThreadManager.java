package com.minook.zeppa.runnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

	private static ExecutorService executor = Executors.newFixedThreadPool(10);
	
	
	public static void execute(Runnable runnable){
		executor.execute(runnable);
	}
	
//	public static void kill(){
//		List<Runnable> runnable = executor.shutdownNow();
//		
//		// TODO: iterate through and determine if any of these need to be run
//		
//	}
	
}
