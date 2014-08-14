package com.minook.zeppa.observer;

/**
 * The purpose of the memory observer is to allow the application context
 * to 
 * @author DrunkWithFunk21
 *
 */
public abstract interface MemoryObserver {

	
	/**
	 * This method should be called when an object is warned of low memory</p>
	 * Things which can easily be loaded back and will not hinder the user
	 * experience should be dumped
	 * 
	 * @return true if object has been deallocated completely
	 */
	public abstract boolean onMemoryWarning();
	
	/**
	 * This method is called when memory is running low</p>
	 * Objects which can easily be recreated should be dumped
	 * 
	 * @return true if object has been deallocated completely
	 */
	public abstract boolean onMemoryLow();
	
	/**
	 * This is called when memory is running critically low</p>
	 * All non-essential objects should be dumped
	 * 
	 * @return true if object has been deallocated completely
	 */
	public abstract boolean onMemoryCritical();
	
	/**
	 * This is called when the application is put into pause state.</p>
	 * Application should tighten its belt as best it can to be a good neighbor and not be greedy.
	 * 
	 * Objects which can per be persisted should be at this point
	 * 
	 * @return true if object has been deallocated completely
	 */
	public abstract boolean onApplicationTerminate();
	
}
