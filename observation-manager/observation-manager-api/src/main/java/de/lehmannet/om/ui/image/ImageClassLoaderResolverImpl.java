package de.lehmannet.om.ui.image;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageClassLoaderResolverImpl implements ImageResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageClassLoaderResolverImpl.class);

    private final String imagePath;

    public ImageClassLoaderResolverImpl(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public Optional<URL> getImageURL(String name) {

        if (StringUtils.isBlank(name)) {
            LOGGER.error("Not image name provided");
            throw new IllegalArgumentException("Should provide a file name");
        }

        final String buildImagePath = buildImagePath(name);
        URL resource = ImageClassLoaderResolverImpl.class.getClassLoader().getResource(buildImagePath);
        if (resource == null) {
            LOGGER.debug("No image resource for for path {}", buildImagePath);
            return Optional.empty();
        }
        LOGGER.debug("Image resource: {} for path {}", resource.toString(), buildImagePath);
        return Optional.ofNullable(resource);
    }

    private String buildImagePath(String name) {

        return this.imagePath + File.separator + name;
    }

}