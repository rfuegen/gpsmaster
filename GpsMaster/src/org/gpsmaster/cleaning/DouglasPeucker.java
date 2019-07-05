package org.gpsmaster.cleaning;

import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

public class DouglasPeucker extends CleaningAlgorithm {

	/**
	 *
	 * @param waypointGroup
	 */
	public DouglasPeucker() {
		super();
	}

	@Override
	public String getName() {
		return "DouglasPeucker";
	}

	@Override
	public String getDescription() {
		return "Find a similar track with fewer points.";
	}

	private void calculateRange() {
		Waypoint start = waypointGroup.getStart();
		Waypoint end = waypointGroup.getEnd();

	}
	@Override
	protected void applyAlgorithm() {

		if (trackpoints.size() < 3) {
			return;
		}
		clear();

	}

	private void compressSegment(int segStart, int segEnd, double threshold) {
		Waypoint start = trackpoints.get(segStart);
		Waypoint end = trackpoints.get(segEnd);


	}

	/**
	 * find the trackpoint furthest away from the startpoint
	 * @param startIndex
	 * @param endIndex
	 */
	private int getFurthestIdx(int startIndex, int endIndex) {
		double maxDist = 0.0f;
		int furthest = -1;
		Waypoint start = trackpoints.get(startIndex);
		for (int i = startIndex; i < endIndex; i++) {
			double dist = start.getDistance(trackpoints.get(i));
			if (dist > maxDist) {
				furthest = i;
				maxDist = dist;
			}
		}
		return furthest;
	}
}
