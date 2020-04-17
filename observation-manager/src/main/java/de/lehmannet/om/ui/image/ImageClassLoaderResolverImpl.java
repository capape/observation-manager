package de.lehmannet.om.ui.image;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class ImageClassLoaderResolverImpl implements ImageResolver {

    private final String imagePath;

    public ImageClassLoaderResolverImpl(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public Optional<URL> getImageURL(String name) {

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Should provide a file name");
        }
		
        URL resource = ImageClassLoaderResolverImpl.class.getClassLoader().getResource(buildImagePath(name));
        
        return Optional.ofNullable(resource);
	}

    private String buildImagePath(String name) {
        return this.imagePath + File.separatorChar + name ;
    }

}