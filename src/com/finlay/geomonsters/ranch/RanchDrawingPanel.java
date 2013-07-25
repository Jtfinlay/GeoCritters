package com.finlay.geomonsters.ranch;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.finlay.geomonsters.battle.*;
import com.finlay.geomonsters.*;

public class RanchDrawingPanel extends DrawingPanel {

	private static final String TAG = "DrawingPanel";
	
	private GameThread _Thread;
	private MainActivity _parent;
	private Paint _paint;
	
	private RectF _cameraPosition;
	private int canvas_width, canvas_height;
	
	private static final int GRID_ROW_COUNT		= 40;
	private static final int GRID_COLUMN_COUNT	= 40;
	
	
	public RanchDrawingPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		getHolder().addCallback(this);
		
		_paint = new Paint();
		_Thread = new GameThread(getHolder(), this);
		
		setFocusable(true);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

}
