package com.finlay.geomonsters;

import android.location.Location;

/**
 * Interface allowing both the EncounterService and MainActivity to allow callbacks from the
 * MyLocationListener object.
 * @author James
 *
 */
public interface LocationListenerParent {

	public void locationFound(Location loc);
}
