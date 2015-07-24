package uk.co.senab.actionbarpulltorefresh.library.sdk;

import android.annotation.SuppressLint;
import android.view.View;

class CompatV16 {

    @SuppressLint("NewApi")
	static void postOnAnimation(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }

}
