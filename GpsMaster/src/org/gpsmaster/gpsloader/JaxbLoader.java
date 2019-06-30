package org.gpsmaster.gpsloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gpsmaster.gpxpanel.GPXFile;

import com.topografix.gpx._1._1.GpxType;

/**
 * Load .gpx files via Jaxb/(un)marshalling
 * (for testing purposes, as of now)
 * @author rfu
 *
 */
public class JaxbLoader extends XmlLoader {

	private GpxType gpx = null;
	
	public JaxbLoader() {
		super();
		extensions.add("gpx");
		xsdResource = "/org/gpsmaster/schema/gpx-1.1.xsd";
	}
	
	@Override
	public void open(File file) {
		// TODO Auto-generated method stub
		this.file = file;
		isOpen = true;
	}

	@Override
	public GPXFile load() throws Exception {
		// TODO Auto-generated method stub
		// TEST CODE
		// JAXBContext context = JAXBContext.newInstance(GpxType.class);
		JAXBContext context = JAXBContext.newInstance("com.topografix.gpx._1._1");
		Unmarshaller u = context.createUnmarshaller();
		gpx = (GpxType) u.unmarshal(file);

		System.out.println(gpx.getMetadata().getName());
		
		return null;
	}

	@Override
	public void loadCumulative() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(GPXFile gpx, File file) throws FileNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		isOpen = false;
	}

	@Override
	public GPXFile load(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}


}