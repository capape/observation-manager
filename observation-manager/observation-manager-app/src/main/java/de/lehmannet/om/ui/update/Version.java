package de.lehmannet.om.ui.update;

import org.apache.commons.lang3.StringUtils;

public class Version implements Comparable<Version> {

    final Integer major;
    final Integer minor;
    final Integer patch;
    final String modifier;

    private Version(Integer major, Integer minor, Integer patch, String modifier) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.modifier = modifier;
    }

    public static Version createVersion(String version) {

        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("Invalid version format");
        }

        if (!isValidVersion(version)) {
            throw new IllegalArgumentException("Invalid version format");
        }

        final String[] parts = version.split("\\.");

        try {
            final Integer vmajor = Integer.parseInt(parts[0]);
            final Integer vminor = Integer.parseInt(parts[1]);

            final String modifier = parts[2].replaceFirst("[0-9]+", "");
            final Integer vpatch = Integer.parseInt(parts[2].replaceAll(modifier, ""));

            return new Version(vmajor, vminor, vpatch, modifier);
        } catch (NumberFormatException nfe) {

            throw new IllegalArgumentException("Invalid version format");

        }

    }

    public static boolean isValidVersion(String version) {
        return version.matches("[0-9]+\\.[0-9]+\\.[0-9]+.*");
    }

    @Override
    public int compareTo(Version o) {
        if (this.major.equals(o.major)) {
            if (this.minor.equals(o.minor)) {
                if (this.patch.equals(o.patch)) {
                    return 0;
                } else {
                    return this.patch.compareTo(o.patch);
                }
            }
            return this.minor.compareTo(o.minor);
        }
        return this.major.compareTo(o.major);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((major == null) ? 0 : major.hashCode());
        result = prime * result + ((minor == null) ? 0 : minor.hashCode());
        result = prime * result + ((patch == null) ? 0 : patch.hashCode());
        result = prime * result + ((modifier == null) ? 0 : modifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Version other = (Version) obj;
        if (major == null) {
            if (other.major != null) {
                return false;
            }
        } else if (!major.equals(other.major)) {
            return false;
        }
        if (minor == null) {
            if (other.minor != null) {
                return false;
            }
        } else if (!minor.equals(other.minor)) {
            return false;
        }
        if (patch == null) {
            if (other.patch != null) {
                return false;
            }
        } else if (!patch.equals(other.patch)) {
            return false;
        }
        if (modifier == null) {
            if (other.modifier != null) {
                return false;
            }
        } else if (!modifier.equals(other.modifier)) {
            return false;
        }
        return true;
    }

    

}