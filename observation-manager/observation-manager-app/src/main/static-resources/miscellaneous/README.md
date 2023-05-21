# OBSERVATION MANAGER

For by Antonio Capapé

    https://github.com/capape/observation-manager/

Original project

    http://observation.sourceforge.net/

## Requirements

---------------------

ObservationManager can run on all operating systems that provide a Java Runtime Environment Version 17 or higher is tested from us on:

- Linux (Debian)
- Microsoft Windows 10,11
- Mac OS

## Installation

---------------------

- Download last release from

    https://github.com/capape/observation-manager/releases/

- Unzip  observation-manager-distribution-XXX-distribution.zip

That's it. This will install Observation Manager with the following embbeded extensions:

- Solar System
- DeepSky
- Variable Stars
- Imaging
- Skychart/Cartes du Ciel

## Starting

---------------------

### WINDOWS

Goto folder /observationManager and call obs.bat

### LINUX

Goto folder /observationManager and call obs.sh (requires execute permissions)

### MAC

Goto folder /observationManager and call obs.command

## Configuration File

---------------------

Configuration file for
Observation Manager. By default this file gets stored in the
users home directory.

### WINDOWS (default)

``` C:\Documents and Settings\{USERNAME}\.observationManager\config ```

### UNIX/LINUX

``` ~/.observationManager/config ```

If you want to store/load the configfile from a different location,
please pass the following startup parameters:
obs.bat "config={SOMEHWHERE}"  (Windows)   (MIND THE QUOTES!)
obs.sh config={SOMEHWHERE}   (Linux)

## Troubleshooting

---------------------
If you run into problems, please contact us via the projects page.
Please send a detailed description, containing the problem, your OM Version, OS, Java Version. Also if you're running into some Java exceptions please attach the corresponding log file to your message. The log file can be found at:

``` {YOUR_INSTALL_PATH}/observationManager/.obs/obs.log ```

## Version

---------------------

Beyond  version 1.6.0 The ObservationManager version number will follow SemVer and it won't be a combination of the application version itself (e.g.) 0.9 and the version of the used OAL XML schema (e.g. 2.1).

The the first part reflects the OM application version, while the second part reflects the contained XML version.

### Log file

---------------------

Since version 1.6.0 Observation Manager will store its log file in the observationManager folder

#### WINDOWS LOG

``` {YOUR_INSTALL_PATH}/observationManager/.obs/obs.log ```

#### UNIX/LINUX LOG

``` {YOUR_INSTALL_PATH}/observationManager/.obs/obs.log ```

## About Observation Manager

### License

ObservationManager is distributed under the Apache Software Licence 2.0. Please see LICENSE-2.0.txt for details.

### Authors

Please see CREDITS file for authors

### Legal issues and acknowledgments

Please see NOTICE.txt file
