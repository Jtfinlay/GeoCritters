package com.finlay.geomonsters.creatures;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.res.Resources;
import android.util.Log;

import com.finlay.geomonsters.R;

public class XMLParser {

	private static final String TAG = "XMLParser";



	public Document getDomElement(InputStream resStream){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;


		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			doc = db.parse(resStream);

		} catch (SAXException e) {
			Log.e(TAG, e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return doc;

	}

	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return getElementValue(n.item(0));
	}
	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for ( child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}



}
