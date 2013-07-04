package com.finlay.geomonsters.battle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class InfoBar {
	
	private Creature _creature;
	private boolean alignLeft = true;
	
	public InfoBar(Creature creature) {
		_creature = creature;
	}
	public void alignRight() {
		alignLeft = false;
	}
	public void render(Canvas c, Paint p) {
		
		// TODO: Change these depending on canvas size
		int padding = 8;
		int corners = 5;
		int nameSize = 25;
		int BARwidth = 10;
		RectF outline = new RectF(0, 0, 250, 80);
		
		RectF healthbar = new RectF(outline.left+padding, 
									outline.top+padding, 
									outline.right-padding, 
									outline.bottom-padding);
		
		// Draw white rect w/ black border
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.WHITE);
		c.drawRoundRect(outline, corners, corners, p);
		
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(Color.GRAY);
		c.drawRoundRect(outline, corners, corners, p);
		
		// Creature name
		p.setTextSize(nameSize);
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.DKGRAY);
		if (alignLeft) {
			p.setTextAlign(Paint.Align.LEFT);
			c.drawText(_creature.getName().toUpperCase(), padding, padding+nameSize, p);
		} else {
			p.setTextAlign(Paint.Align.RIGHT);
			c.drawText(_creature.getName().toUpperCase(), outline.right - padding, padding+nameSize, p);
		}
		
		// Health bar
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(BARwidth);
		
		float y = (padding+nameSize)+2*padding; 	// 2x padding distance below drawn name
		float xi = padding;
		float xf = outline.right - padding;
		float xm = xi + (xf-xi)*_creature.getHealthPercent();
		
		p.setColor(Color.GREEN);
		c.drawLine(xi, y, xm, y, p);
		
		p.setColor(Color.RED);
		c.drawLine(xm, y, xf, y, p);
		
		p.setColor(Color.GRAY);
		p.setStrokeWidth(2);
		c.drawRect(xi, y-BARwidth/2, xf, y+BARwidth/2, p);
		
		// Attack Timer bar
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(BARwidth);
		
		y += BARwidth + padding;
		xm = (xf-xi)*.75f;
		
		p.setColor(Color.CYAN);
		c.drawLine(xi, y, xm, y, p);
		
		p.setColor(Color.GRAY);
		p.setStrokeWidth(2);
		c.drawRect(xi, y-BARwidth/2, xf, y+BARwidth/2, p);
		
		
	}

}
