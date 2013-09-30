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

package com.finlay.geomonsters.battle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class InfoBar {
	
	private static final String TAG = "InfoBar";
	
	private Creature _creature;
	
	private boolean alignLeft = true;
	private RectF 	_drawRect;
	private float	_padding;
	private float	_corners;
	private float 	_nameSize;
	private float	_BARwidth;
	
	public InfoBar(Creature creature) {
		_creature = creature;
	}
	public void alignRight() {
		alignLeft = false;
	}
	public void setDrawRect(RectF destRect) {
		_drawRect = destRect;
		
		_padding = .025f*destRect.width();
		_corners = .01f*destRect.height();
		_nameSize = .1f*destRect.width();
		_BARwidth = .04f*destRect.width();
	}
	public void render(Canvas c, Paint p) {

		
		// Draw white rect w/ black border
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.WHITE);
		c.drawRoundRect(_drawRect, _corners, _corners, p);
		
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		p.setColor(Color.GRAY);
		c.drawRoundRect(_drawRect, _corners, _corners, p);
		
		// Creature name
		p.setTextSize(_nameSize);
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.DKGRAY);
		if (alignLeft) {
			p.setTextAlign(Paint.Align.LEFT);
			c.drawText(_creature.getNickName(), _drawRect.left + _padding, _drawRect.top + _padding+_nameSize, p);
		} else {
			p.setTextAlign(Paint.Align.RIGHT);
			c.drawText(_creature.getNickName(), _drawRect.right - _padding, _drawRect.top + _padding+_nameSize, p);
		}
		
		// Health bar
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(_BARwidth);
		
		float y = _drawRect.top + (_padding+_nameSize)+2*_padding; 	// 2x padding distance below drawn name
		float xi = _drawRect.left + _padding;
		float xf = _drawRect.right - _padding;
		float xm = xi + (xf-xi)*_creature.getHealthPercent();
		
		p.setColor(Color.GREEN);
		c.drawLine(xi, y, xm, y, p);
		
		p.setColor(Color.RED);
		c.drawLine(xm, y, xf, y, p);
		
		p.setColor(Color.GRAY);
		p.setStrokeWidth(2);
		c.drawRect(xi, y-_BARwidth/2, xf, y+_BARwidth/2, p);
		
		// Attack Timer bar
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(_BARwidth);
		
		y += _BARwidth + _padding;
		xm = (float) (xi + (xf-xi)*_creature.getNextAttackPercent());
		
		p.setColor(Color.CYAN);
		c.drawLine(xi, y, xm, y, p);
		
		p.setColor(Color.GRAY);
		p.setStrokeWidth(2);
		c.drawRect(xi, y-_BARwidth/2, xf, y+_BARwidth/2, p);
		
		
	}

}
