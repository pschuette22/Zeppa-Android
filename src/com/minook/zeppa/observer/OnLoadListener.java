package com.minook.zeppa.observer;

/**
 * This class defines an interface for objects waiting on loads happening in
 * background threads
 * 
 * @author DrunkWithFunk21
 * 
 */
public abstract interface OnLoadListener {

	/**
	 * @return true if the initial load has occured
	 */
	public abstract boolean didLoadInitial();

	/**
	 * This method is called when the current loading threads have completed and
	 * objects should update themselves
	 */
	public abstract void onFinishLoad();

	
}
