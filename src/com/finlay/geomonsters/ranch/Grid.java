package com.finlay.geomonsters.ranch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;

public class Grid {

	public static final String TAG = "Grid";

	private static final int GRID_ROW_COUNT		= 15;
	private static final int GRID_COLUMN_COUNT	= 15;

	private static final int GRID_SIZE = 75;

	private char[][] grid_matrix;

	public Grid(boolean makeNew) {
		grid_matrix = new char[GRID_COLUMN_COUNT][GRID_ROW_COUNT];
		
		if (makeNew) {
			// For now, just create a basic grid_matrix.
			for (int col=0; col<GRID_COLUMN_COUNT; col++)
				for (int row=0; row<GRID_ROW_COUNT; row++)
					if (row < 10 || row > 30)
						grid_matrix[col][row] = 'b';
					else
						grid_matrix[col][row] = 'g';
		}
	}
	public void set(char[][] matrix) {
		for (char[] line : matrix) {
			for (char item : line)
				Log.v(TAG, "Item: " + item);
			Log.v(TAG, "newline");
		}
		grid_matrix = matrix;
	}
	public char[][] getGrid() {
		return grid_matrix;
	}

	public int getWidth() {
		return GRID_COLUMN_COUNT * GRID_SIZE;
	}
	public int getHeight() {
		return GRID_ROW_COUNT * GRID_SIZE;
	}

	public void render(Canvas c, RectF camera, Paint p) {

		c.save();
		c.translate(-camera.left, -camera.top);

		// draw all grid squares intersecting the cameraLocation
		for (int col=0; col<GRID_COLUMN_COUNT; col++)
			for (int row=0; row<GRID_ROW_COUNT; row++) {

				// ensure intersects with cameraLocation
				if (col * GRID_SIZE > camera.right) 	continue; 
				if ((col+1) * GRID_SIZE < camera.left) 	continue;
				if (row * GRID_SIZE > camera.bottom)	continue;
				if ((row+1) * GRID_SIZE < camera.top)	continue;

				// draw
				switch (grid_matrix[col][row]) {
				case 'b':
					p.setColor(Color.BLUE);
					p.setStyle(Style.FILL);
					c.drawRect(col * GRID_SIZE, row * GRID_SIZE, (col+1) * GRID_SIZE, (row+1) * GRID_SIZE, p);
					break;
				case 'g':
					p.setColor(Color.GREEN);
					p.setStyle(Style.FILL);
					c.drawRect(col * GRID_SIZE, row * GRID_SIZE, (col+1) * GRID_SIZE, (row+1) * GRID_SIZE, p);
					break;
				}

				p.setColor(Color.WHITE);
				p.setStyle(Style.STROKE);
				c.drawRect(col * GRID_SIZE, row * GRID_SIZE, (col+1) * GRID_SIZE, (row+1) * GRID_SIZE, p);
			}


		c.restore();
	}
}
