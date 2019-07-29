package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import org.gpsmaster.xml.bind.ValidationException;

import org.gpsmaster.gpxpanel.GPXFile;


/**
 * base class for file format specific loader classes
 *
 * @author rfu
 *
 */
public abstract class GpsLoader {

	protected boolean isDefault = false;
	protected boolean isOpen = false;
	protected boolean isAdding = false;

	protected File file = null;
	protected GPXFile gpx = null;
	protected List<String> extensions = new ArrayList<String>();
	protected Hashtable<File, GPXFile> gpxFiles = new Hashtable<File, GPXFile>();

	protected LoaderConfig loaderConfig = null;

	protected Locale numLocale = new Locale("en", "US"); // locale for . as comma separator

	/**
	 * Constructor
	 */
	public GpsLoader() {

	}

	/**
	 *
	 * @return
	 */
	public boolean isDefaultLoader() {
		return isDefault;
	}

	/**
	 *
	 * @return {@link true} if the loader is adding; meaning that the content of multiple
	 *  files being loaded is added to a single GPX file, i.e. for geo-referenced images.
	 */
	public boolean isCumulative() {
		return isAdding;
	}

	/**
	 * set loader-specific configuration
	 */
	public void setConfig(LoaderConfig config) {
		loaderConfig = config;
	}


	/**
	 * Gets all GPX files loaded via loadCumulative() so far.
	 * @return
	 */
	public Hashtable<File, GPXFile> getFiles() {
		return gpxFiles;
	}

	/**
	 * Creates a {@link GPXFile} from an {@link InputStream}
	 * @param inStream stream {@link InputStream} containing data
	 * @param format three character "sourceFmt" specifying the format (i.e. gpx, kmz, ...) usually ignored, but required by loaders supporting more than one format
	 * @return
	 * @throws Exception
	 */
	public abstract GPXFile load(InputStream inStream, String format) throws Exception;

	/**
	 *
	 * @param inStream
	 * @throws Exception
	 */
	public abstract void loadCumulative(InputStream inStream) throws Exception;

	/**
	 *
	 * @param gpx
	 * @param outStream
	 */
	public abstract void save(GPXFile gpx, OutputStream outStream);

	/**
	 * get if this loader can validate delivered data
	 * @return
	 */
	public abstract boolean canValidate();


	/**
	 * Validate given data
	 * @param inStream {@link InputStream} containing data to validate. it is assumed that
	 * a) this stream contains exactly the same data as the stream passed to load()
	 * b) this stream will be rewound to position 0 before being passed to load()
	 * @throws ValidationException
	 * @throws NotBoundException
	 */
	public abstract void validate(InputStream inStream) throws ValidationException, NotBoundException;

	/**
	 * returns a list of supported file types for loading
	 * (as sourceFmt of filename, i.e. .gpx)
	 * @return
	 */
	public List<String> getLoadFormats() {

		return extensions;
	}

	/**
	 *
	 */
	public void clear() {
		gpxFiles.clear();
	}

}
