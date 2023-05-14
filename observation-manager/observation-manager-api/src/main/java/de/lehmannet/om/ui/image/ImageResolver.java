package de.lehmannet.om.ui.image;

import java.net.URL;
import java.util.Optional;

public interface ImageResolver {

    /**
     * @param Name
     *            of image to load
     * @return a URL to the image
     */
    Optional<URL> getImageURL(String name);

}