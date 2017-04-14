Kintrol
=======

Remote control app for [LINN&reg;](http://www.linn.co.uk/) KINOS&trade;, KISTO&trade; and Klimax Kontrol&trade; system controllers.

Copyright &copy; 2015-2017 Oliver Götz

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

On first startup the app prompts you for the settings of your KINOS, KISTO or Klimax Kontrol device:
 - Device type: either Kinos, Kisto or Klimax Kontrol. Please select the model you want to control.
 - Device name: pick a name for your device. Please note that this should be a unique name in case you have more than one devices.
 - IP address: the IPV4 network address under which the device can be reached. 
 - Port (optional): the port of the device. You can leave this empty unless you want to use a different port than the default 9004. That is the port the Kinos or Kisto device uses for network communication. But in case you are using an network-to-RS232 adapter (i.e. when using a Klimax Kontrol) the port might be different.
 - Discrete Volume Value (optional): if you specify a numeric value, the app will show an additional button next to the mute button, which sets the volume to a fixed value. This can be useful, if you have a calibrated volume level for movies in your home theatre, but your configured initial volume setting is different. If you leave this empty, the button will not appear.
 
Your KINO/KISTO/Klimax Kontrol needs to be connected to your network, and a fixed IP address must be configured for it.
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

UI resources for Holo theme created with [Android Holo Colors Generator](http://android-holo-colors.com/) by [Jérôme Van Der Linden](mailto:jeromevdl@android-holo-colors.com). All generated art is licensed under a [Creative Commons Attribution 3.0 Unported License](http://creativecommons.org/licenses/by/3.0/).


References
----------

The specifications for the control protocol for [KINOS](http://docs.linn.co.uk/wiki/images/3/3f/Kinos_RS232_Spec_v0101.pdf) and [KISTO](http://docs.linn.co.uk/wiki/images/4/4f/Kisto_rs232_commands_v106.pdf) can be found at the [LINNDOCS web site](http://docs.linn.co.uk/wiki/index.php/RS232).


Acknowledgements
----------------

 - App icon by Mr. Truesound
 - Many thanks to the LINN customer support


Open Issues
-----------

 - There is no proper release build
 - There are no tests

Questions and Anwsers
---------------------

Q: Why are there no automated tests?
A: Shame on me! Normally, I am a big proponent of automated tests and test-driven development. However, since this was my first go at an Android app there was a lot of trial and error involved for me. Add to that a logic which relies heavily on network communication with a device for which I had to figure out some of the behaviour on the go, I saw it as too restrictive to add a test double only to find out later that the behavior of the real device is different anyhow.

Q: Why can I add more than one device? Who would ever need that?
A: I do! ;-) We have two Kinos devices in the house and since I primarily wrote the app for ourselves, this is how it turned out. If I find the time and motivation I might change the UI in the future to only have the device control screen and have a selection possibility for several devices embedded there.

Q: Is there an iOS version?
A: No. The reasons I opted for Android are that I am a Java developer by trade and therefore I found it more convenient to not have to learn a new language at the same time as learning the SDK and figure out the behavior of the Linn devices. Also, for Android I am not necessarily forced to go through the official release process to get the app on my device and out into the world. There are no plans currently to port this to iOS. But feel free to start such a project yourself.


Contact Information
-------------------

Please send feedback to mailto:developer@geekgasm.eu
