package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.gpxpanel.Track;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;
import org.gpsmaster.gpxpanel.WaypointGroup.WptGrpType;
import org.gpsmaster.markers.Marker;
import org.gpsmaster.markers.WaypointMarker;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


// https://developers.google.com/kml/documentation/?csw=1

/**
 *
 * @author rfuegen
 *
 *
 */

// TODO support .kmz files

public class KmlLoader extends XmlLoader {

	private boolean hasGx = false; // if this document contains google GX extensions

	public KmlLoader() {
		super();
		extensions.add("kml");
		extensions.add("kmz");
		// xsdResource = "/org/gpsmaster/schema/ogckml22.xsd";
	}


	public void open(File file) {

		this.file = file;
		isOpen = true;

	}

	/**
	 *
	 * @param Waypoint
	 * @param coordinates string containing coordinates "long,lat,ele"
	 */
	private Waypoint parseCoordinateLine(String coordinates) {

		String[] items = coordinates.split(",");
		if (items.length < 2) {
			return null;
		}
		double lon = Double.parseDouble(items[0]);
		double lat = Double.parseDouble(items[1]);
		Waypoint wpt = new Waypoint(lat, lon);
		if (items[2].isEmpty() == false) {
			double ele = Double.parseDouble(items[2]);
			if (ele > 0) {
				wpt.setEle(ele);
			}
		}
		return wpt;

	}

	/**
	 * determine type of placemark and call appropriate method
	 * @param placemark Element containing the <Placemark>
	 */
	private void parsePlacemark(Element placemark) {

		Element sub = null;

		// a <Placemark> containing <point> is a waypoint.
		sub = getSubElement(placemark, "Point");
		if (sub != null) {
			parseWaypoint(gpx.getWaypointGroup().getWaypoints(), placemark);
		}

		// a <Placemark> with a <LineString> is a track.
		sub = getSubElement(placemark, "LineString");
		if (sub != null) {
			Track track = new Track(gpx.getColor());
			parseTrack(track, placemark);
			gpx.getTracks().add(track);
		}

		// a <Placemark> containing <gx:Track> is a track in google extension format
		sub = getSubElement(placemark, "gx:Track");
		if (sub != null) {
			Track track = new Track(gpx.getColor());
			parseGxTrack(track, placemark);
			gpx.getTracks().add(track);
		}

		// a <Placemark> containing <gx:Track> is a track in google extension format
		sub = getSubElement(placemark, "MultiGeometry");
		if (sub != null) {
			Track track = new Track(gpx.getColor());
			parseTrack(track, placemark);
			gpx.getTracks().add(track);
		}

	}

	/**
	 * Parse a <Placemark> element into a {@link Waypoint}
	 * @param waypoints
	 * @param placemark
	 */
	private void parseWaypoint(List<Waypoint> waypoints, Element placemark) {

		String coordinates = getSubValue(placemark, "coordinates");
		Waypoint wpt = parseCoordinateLine(coordinates);
		WaypointMarker marker = new WaypointMarker(wpt);
		String name = getSubValue(placemark, "name");
		if (name != null) {
			marker.setName(name);
		}
		String desc = getSubValue(placemark, "description");
		if (desc != null) {
			marker.setDesc(desc);
		}

		waypoints.add(marker);
	}


	/**
	 *
	 * @param wptGrp
	 * @param placemark
	 */
	private void parseCoordinateSection(WaypointGroup wptGrp, Element placemark) {
		Element lineString = getSubElement(placemark, "LineString");
		Element coord = getSubElement(lineString, "coordinates");
		String coordinates = coord.getTextContent();
		for (String line : coordinates.split("\\s+")) {
			Waypoint wpt = parseCoordinateLine(line);
			if (wpt != null) {
				wptGrp.addWaypoint(wpt);
			}
		}
	}


	/**
	 * parse coordinates in GX extensions
	 * @param wptGrp Track segment to add coordinates to
	 * @param placemark <Placemark> element
	 */
	private void parseGxCoordinates(WaypointGroup wptGrp, Element placemark) {
		Element trackSeg = getSubElement(placemark, "gx:Track");
		NodeList times = trackSeg.getElementsByTagName("when");
		NodeList coordinates = trackSeg.getElementsByTagName("gx:coord");
		if (times.getLength() != coordinates.getLength()) {
			throw new IllegalArgumentException("coordinates & time count mismatch");
		}
		for (int i = 0; i < times.getLength(); i++) {
			String time = times.item(i).getTextContent();
			String coord = coordinates.item(i).getTextContent();
			String[] items = coord.split("[ ]+");
			double lon = Double.parseDouble(items[0]);
			double lat = Double.parseDouble(items[1]);
			double ele = Double.parseDouble(items[2]);
			Waypoint wpt = new Waypoint(lat, lon);
			if (ele > 0) {
				wpt.setEle(ele);
			}
			DateTime dt = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(time);
            wpt.setTime(dt.toDate());
			wptGrp.addWaypoint(wpt);
		}
	}

	/**
	 *
	 * @param track
	 * @param folder
	 */
	private void parseTrack(Track track, Element placemark) {

		track.setName(getSubValue(placemark, "name"));
		String desc = getSubValue(placemark, "description");
		if (desc != null) { track.setDesc(desc); }
		WaypointGroup trkSeg = new WaypointGroup(track.getColor(), WptGrpType.TRACKSEG);
		// check if the coordinates are contained within a <MultiGeometry>
		Element multi = getSubElement(placemark, "MultiGeometry");
		if (multi == null) {
			parseCoordinateSection(trkSeg, placemark);
		} else {
			parseCoordinateSection(trkSeg, multi);
		}
		if (trkSeg.getWaypoints().size() > 0) {
			track.getTracksegs().add(trkSeg);
		}

	}

	/**
	 * <Folder>/<PlaceMark>/<gx:Track>
	 * @param track
	 * @param folder
	 */
	private void parseGxTrack(Track track, Element placemark) {

		track.setName(getSubValue(placemark, "name"));
		String desc = getSubValue(placemark, "description");
		if (desc != null) { track.setDesc(desc); }
		WaypointGroup trkSeg = new WaypointGroup(track.getColor(), WptGrpType.TRACKSEG);
		parseGxCoordinates(trkSeg, placemark);

		if (trkSeg.getWaypoints().size() > 0) {
			track.getTracksegs().add(trkSeg);
		}
	}


	/**
	 *
	 * @param element
	 */
	private void scanForPlacemark(Element parent) {
		for (Element element : getSubElements(parent)) {
			if (element.getTagName().equals("Placemark")) {
				parsePlacemark(element);
			} else if (element.getTagName().equals("Folder")) {
				scanForPlacemark(element);
			}
		}
	}

	/**
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 *
	 */
	public GPXFile load() throws Exception {

		gpx = new GPXFile();
		ZipFile zipFile = null;
		InputStream fis = null;

		if (file.getName().endsWith(".kml")) {
			fis = new FileInputStream(file);
			gpx = load(fis);
			fis.close();
		} else if (file.getName().endsWith(".kmz")) {
			zipFile = new ZipFile(file);
			ZipEntry entry = zipFile.getEntry("doc.kml");
			if (entry == null) {
				zipFile.close();
				throw new FileNotFoundException("KMZ file does not contain doc.kml");
			}
			fis = zipFile.getInputStream(entry);
			gpx = load(fis);
			fis.close();
			zipFile.close();
		} else {
			throw new UnsupportedOperationException("unsupported file type");
		}

		return gpx;
	}

	@Override
	public GPXFile load(InputStream inputStream) throws Exception {
		// TODO Auto-generated method stub

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		Document xml = builder.parse(inputStream);

		Element root = xml.getDocumentElement();
		String gx = root.getAttribute("xmlns:gx");
		hasGx = !gx.isEmpty();
		Element document = getSubElement(root, "Document");
		if (document == null) {
			throw new NoSuchElementException("Document");
		}
		// set GPXFile name to <Document>/<name>
		String name = getSubValue(document, "name");
		gpx.getMetadata().setName(name);

		String description = getSubValue(document, "description");
		gpx.getMetadata().setDesc(description);
		// TODO gpx.SetTime(...)
		// TODO read style information & set track/gpx color

		scanForPlacemark(document);

		return gpx;
	}

	@Override
	public void loadCumulative() throws Exception {
		gpxFiles.put(file, load());

	}

	@Override
	public void save(GPXFile gpx, OutputStream out) {
		throw new NotImplementedException();
	}

	public void save(GPXFile gpx, File file) {
		throw new NotImplementedException();
	}

	public void close() {
		this.file = null;
		isOpen = false;
	}






}
