package com.finlay.geomonsters.battle;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

	private static final String TAG = "GameThread";
	private static final int	MAX_FPS = 50;
	private static final int 	FRAME_PERIOD = 20;
	private static final int 	MAX_FRAME_SKIPS = 1000 / MAX_FPS;

	private SurfaceHolder _surfaceHolder;
	private DrawingPanel _panel;
	private boolean _run = false;

	public GameThread(SurfaceHolder surfaceHolder, DrawingPanel panel) {
		_surfaceHolder = surfaceHolder;
		_panel = panel;
	}

	public void setRunning(boolean run) {
		_run = run;
	}

	@Override
	public void run() {
		Log.v(TAG, "Starting game loop");

		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;

		Canvas c = null;
		while (_run) {
			c = null;
			try {
				c = _surfaceHolder.lockCanvas();
				synchronized (_surfaceHolder) {
					// reset values
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;

					// update game state
					_panel.update();

					// render game state
					_panel.render(c);
					
					// how long did cycle take?
					timeDiff = System.currentTimeMillis() - beginTime;
					
					// calculate sleep time
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					if (sleepTime > 0) {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {}
					}
					
					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						// we need to catch up update without rendering
						_panel.update();
						
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
				}
			} finally {
				if (c != null) {
					_surfaceHolder.unlockCanvasAndPost(c);
				}
			}

		}
	}


}
