package com.finlay.geomonsters.battle;

/**
 * This interface provides a means for the DrwingPanel to send messages through the BattleActivity
 * to the bottom Panel.
 * @author James
 *
 */
public interface DrawingPanelListener {

		public void showButtonView();
		public void showMessage(final String s);

}
