/*
 * GeoCritters. Real-world creature encounter game.
 * Copyright (C) 2013 James Finlay
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.finlay.geomonsters.ranch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.finlay.geomonsters.battle.*;
import com.finlay.geomonsters.*;

public class RanchDrawingPanel extends DrawingPanel {

	private static final String TAG = "RanchDrawingPanel";

	private GameThread _Thread;
	private MainActivity _parent;
	private Paint _paint;
	private Grid _grid;

	private RectF _cameraPosition;
	private int canvas_width, canvas_height;

	// Touch coords
	private float prevX = -1;
	private float prevY = -1;

	public RanchDrawingPanel(Context context, AttributeSet attrs) {
		super(context, attrs);

		getHolder().addCallback(this);

		_paint = new Paint();
		_grid = ConfigManager.GetRanchGrid(getContext());
		_Thread = new GameThread(getHolder(), this);

		setFocusable(true);

	}
	
	public void init(MainActivity parent) {
		Log.v(TAG, "init");
		
		_parent = parent;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(TAG, "surfaceChanged");

		Rect surface = holder.getSurfaceFrame();
		canvas_width = surface.width();
		canvas_height = surface.height();
		
		updateDrawingPreferences();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "surfaceCreated");

		Rect surface = holder.getSurfaceFrame();
		canvas_width = surface.width();
		canvas_height = surface.height();
		
		updateDrawingPreferences();
		
		_Thread.setRunning(true);
		_Thread.start();
	}
	public void updateDrawingPreferences() {
	
		_cameraPosition = new RectF();
		_cameraPosition.left = 25;
		_cameraPosition.top = 0;
		_cameraPosition.right = _cameraPosition.left + canvas_width;
		_cameraPosition.bottom = _cameraPosition.top + canvas_height;
		
	}
	
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(TAG, "surfaceDestroyed");

		boolean retry = true;
		_Thread.setRunning(false);
		while (retry) {
			try {
				_Thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will keep trying
			}
		}
	}
	
	public void update() {
	
	}
	public void render(Canvas c) {
		
		// draw ranch grid
		_grid.render(c,  _cameraPosition, _paint);
		
	}
	
	/** TOUCH EVENTS **/
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		
		switch (e.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			prevX = e.getX();
			prevY = e.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			translateCamera((int)(prevX - e.getX()), (int)(prevY - e.getY()));
			prevX = e.getX();
			prevY = e.getY();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			prevX = -1;
			prevY = -1;
			break;
		}
		
		return true;
	}
	
	private void translateCamera(int dx, int dy) {
		// move camera
		_cameraPosition.offset(dx, dy);
		
		// ensure not out of bounds
		if (_cameraPosition.left < 0) 					_cameraPosition.offset(-_cameraPosition.left, 0);
		if (_cameraPosition.top < 0)					_cameraPosition.offset(0, -_cameraPosition.top);
		if (_cameraPosition.right > _grid.getWidth())	_cameraPosition.offset(_grid.getWidth() - _cameraPosition.right, 0);
		if (_cameraPosition.bottom > _grid.getHeight())	_cameraPosition.offset(0, _grid.getHeight() - _cameraPosition.bottom);
	
	}

}
