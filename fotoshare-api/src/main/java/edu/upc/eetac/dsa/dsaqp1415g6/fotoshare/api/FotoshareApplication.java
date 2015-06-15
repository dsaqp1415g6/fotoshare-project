package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api;

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class FotoshareApplication extends ResourceConfig {

	public FotoshareApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
		ResourceBundle bundle = ResourceBundle.getBundle("application");

		register(MultiPartFeature.class);
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			property(key, bundle.getObject(key));
		}
	}
}
