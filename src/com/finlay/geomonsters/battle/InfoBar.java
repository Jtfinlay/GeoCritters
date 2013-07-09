package com.finlay.geomonsters.battle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class InfoBar {
	
	private static final String TAG = "InfoBar";
	
	private Creature _creature;
	private boolean alignLeft = true;
	
	public InfoBar(Creature creature) {
		_creature = creature;
	}
	public void alignRight() {
		alignLeft = false;
	}
	public void render(RectF destRect, Canvas c, Paint p) {
		
		// TODO: Change these depending on canvas size
		float padding = .025f*destRect.width();
		float corners = .01f*destRect.height();
		float nameSize = .1f*destRect.width();
		float BARwidth = .04f*destRect.width();;
		
		// Draw white rect w/ black border
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.WHITE);
		c.drawRoundRect(destRect, corners, corners, p);
		
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(Color.GRAY);
		c.drawRoundRect(destRect, corners, corners, p);
		
		// Creature name
		p.setTextSize(nameSize);
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.DKGRAY);
		if (alignLeft) {
			p.setTextAlign(Paint.Align.LEFT);
			c.drawText(_creature.getName(), destRect.left + padding, destRect.top + padding+nameSize, p);
		} else {
			p.setTextAlign(Paint.Align.RIGHT);
			c.drawText(_creature.getName(), destRect.right - padding, destRect.top + padding+nameSize, p);
		}
		
		// Health bar
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(BARwidth);
		
		float y = destRect.top + (padding+nameSize)+2*padding; 	// 2x padding distance below drawn name
		float xi = destRect.left + padding;
		float xf = destRect.right - padding;
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
		xm = (float) (xi + (xf-xi)*_creature.getNextAttackPercent());
		
		p.setColor(Color.CYAN);
		c.drawLine(xi, y, xm, y, p);
		
		p.setColor(Color.GRAY);
		p.setStrokeWidth(2);
		c.drawRect(xi, y-BARwidth/2, xf, y+BARwidth/2, p);
		
		
	}

}
