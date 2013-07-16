package com.finlay.geomonsters;

public class Weather {

	/** Complete weather IDs can be obtained from: bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes **/

	public static final int[] THUNDERSTORM = {200, 201, 202, 210, 211, 212, 221, 230, 231, 232};
	public static final int[] DRIZZLE = {300, 301, 302, 310, 331, 312, 321 };
	public static final int[] RAIN = {500, 501, 502, 503, 504, 511, 520, 521, 522};
	public static final int[] SNOW = {600, 601, 602, 611, 621};

	// Clouds
	public static final int SKYCLEAR = 800;
	public static final int FEW = 801;
	public static final int SCATTERED = 802;
	public static final int BROKEN = 803;
	public static final int OVERCAST = 804;

	// Extreme
	public static final int TORNADO = 900;
	public static final int TROPICAL_STORM = 901;
	public static final int HURRICANE = 902;
	public static final int COLD = 903;
	public static final int HOT = 904;
	public static final int WINDY = 905;
	public static final int HAIL = 906;

	// Local variables
	public int weatherID;			// weather ID (see static vars above)
	public double sunrise;			// sunrise time
	public double sunset;			// sunset time
	public double wind_speed;		// speed of wind
	public int cloud_cover;			// cloud cover amt
	public double temp;


	public String getWeatherType() {

		for (int i=0; i<THUNDERSTORM.length; i++)
			if (weatherID == THUNDERSTORM[i]) return "thunder";

		for (int i=0; i<RAIN.length; i++)
			if (weatherID == RAIN[i]) return "rain";

		for (int i=0; i<SNOW.length; i++)
			if (weatherID == SNOW[i]) return "snow";

		return "other";
	}
	
	public String getCloudCover() {
		if (cloud_cover == 0)	return "clear";
		if (cloud_cover < 25)	return "few";
		if (cloud_cover < 50)	return "scattered";
		if (cloud_cover < 75)	return "broken";
		return "overcast";		
	}
	
	
}
