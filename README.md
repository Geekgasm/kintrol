Kintrol
=======

Remote control app for [LINN&reg;](http://www.linn.co.uk/) KINOS&trade; and KISTO&trade; system controllers.

Copyright (C) 2015 Oliver Götz

This program is free software: you can redistribute it and/or modify
it under the terms of the [GNU General Public License version 3](http://www.gnu.org/licenses/gpl.html).

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.


Usage
-----

On first startup the app prompts you for a name for your KINOS or KISTO device and the IP address. Your KINO/KISTO needs to be connected to your network, and a fixed IP address must be configured for it.
You can add additional devices in the application menu.

Once you added a device you can select it from the device list in the main screen.

If your device is switched on an reachable via network the device control screen allows you to switch the device on and off, change the volume, mute it, select the input profile, and select the surround mode. Also, the current setting for operation state, volume, input profile, and surround mode is displayed.

Opening the app menu from the device control screen allows you to edit the device settings, delete the device, and display the device information (including software version and total operation time).


Used Libraries
--------------

The following open source libraries are included in this app:

 - [Apache Commons Net](https://commons.apache.org/proper/commons-net/) for the telnet libraries
 - [google-gson](https://code.google.com/p/google-gson/) for JSON handling
 - [AutoFitTextView](https://github.com/AndroidDeveloperLB/AutoFitTextView) for gracefully fitting the status texts into the available space

All three libraries are licenses under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0), which is included in the respective libraries folders of this project.


References
----------

The specifications for the control protocol for [KINOS](http://docs.linn.co.uk/wiki/images/3/3f/Kinos_RS232_Spec_v0101.pdf) and [KISTO](http://docs.linn.co.uk/wiki/images/4/4f/Kisto_rs232_commands_v106.pdf) can be found at the [LINNDOCS web site](http://docs.linn.co.uk/wiki/index.php/RS232).


Acknowledgements
----------------

 - App icon by Mr. Truesound
 - Many thanks to the LINN customer support


Contact Information
-------------------

Please send feedback to mailto:developer@geekgasm.eu
