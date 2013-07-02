package com.finlay.geomonsters.battle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.XMLParser;

public class Attack extends Action {
	
	private static final String TAG = "Attack";
	
	public Attack(String name, String type, int animation) {
		_name = name;
		_type = type;
		_animation = animation;
	}
	

}
