# Kintrol

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

## Features

 - Support for multiple devices
 - Configurable display name for each device
 - Configurable IP address and port for each device
 - Configurable discrete Volume Value (optional) for each device
 - On/Off and display of Standby status
 - Source selection (-/+) and source name display
 - Volume -/+ and Mute with display of current volume setting
 
### Kinos and Kisto System Controllers

 - Surround Mode selection (-/+) and display of current setting
 - Display of total operation time and software versions in the device info display
      
### Klimax Kontrol Pre-Amplifier

 - Unity Gain detection: in case an input is configured with Unity Gain, the volume 
   and mute control buttons are disabled. 
 - Display of power on time, software and hardware version in the device info display
   Downloads

## Getting Started

### Connecting your Linn device to the network

Your KINO/KISTO/Klimax Kontrol needs to be connected to your network, and a fixed IP address must be configured for it.

#### Kinos and Kisto

#### Klimax Kontrol

### Installing the app

### Starting the app for the first time

When you start the app for the first time, it does not know about your Linn device.
It will prompt you to enter the configuration for your Linn Kinos, Kisto or Klimax Kontrol:

<img src="docu-images/initial_screen_new_device.png" height="500" alt="Initial screen prompting for the device configuration"/>

Enter the required information:

 - The device type: select either *Kinos*, *Kisto*, or *Klimax Kontrol*
 - A name for the device (you can pick that freely)
 - The IP address for your device. 
 
 Optionally, you can enter:
 
  - The port: this defaults to 9004 if you don't specify anything here. Leave it empty
    for Kinos and Kisto. For Klimax Kontrol, enter the port number you configured for
    your Network to RS232 adapter if it differs from 9004.
  - A discrete volume value. If you specify a volume here, an additional button 
    will appear on the device control screen, allowing you to quickly set the 
    volume to this value.

After you filled in the required values, press on the *OK* button and see the 
entry screen with one device listed.

<img src="docu-images/first_device_configured.png" height="500" alt="First device configured"/>

## Using the App

### The Device Chooser Screen

The app allows you to configure multiple Linn devices. 
When the app is started, it shows the *Device Chooser Screen*, whose primary 
function is to select the device you want to control. You will see the list of devices,
which you have configured:

<img src="docu-images/choose_device.png" height="500" alt="Device Chooser Screen"/>

To start controlling a device, simply tap on the device name in the list. This will 
bring you to the *Device Control Screen* (see next section).

To add more devices, open the application menu (the three small dots in the upper right corner):

<img src="docu-images/choose_device_menu.png" height="500" alt="Device Chooser Application Menu"/>

***Add Device*** lets you add a new device to the list. The configuration menu is the same as for 
the first device (see section *Starting the app for the first time*).

***About Kintrol*** will open the screen with the app information (copyright notice,
license information and links to this Github project):
 
<img src="docu-images/about.png" height="500" alt="About Kintrol"/>


### The Device Control Screen

If your device is switched on an reachable via network the device control screen allows you to switch the device on and off, change the volume, mute it, select the input profile, and select the surround mode. Also, the current setting for operation state, volume, input profile, and surround mode is displayed.

Opening the app menu from the device control screen allows you to edit the device settings, delete the device, and display the device information (including software version and total operation time).


## Used Libraries

The following open source libraries are included in this app:

 - [Apache Commons Net](https://commons.apache.org/proper/commons-net/) for the telnet libraries
 - [google-gson](https://code.google.com/p/google-gson/) for JSON handling
 - [AutoFitTextView](https://github.com/AndroidDeveloperLB/AutoFitTextView) for gracefully fitting the status texts into the available space

All three libraries are licenses under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0), which is included in the respective libraries folders of this project.

UI resources for Holo theme created with [Android Holo Colors Generator](http://android-holo-colors.com/) by [Jérôme Van Der Linden](mailto:jeromevdl@android-holo-colors.com). All generated art is licensed under a [Creative Commons Attribution 3.0 Unported License](http://creativecommons.org/licenses/by/3.0/).


## References

The specifications for the control protocol for [KINOS](http://docs.linn.co.uk/wiki/images/3/3f/Kinos_RS232_Spec_v0101.pdf) and [KISTO](http://docs.linn.co.uk/wiki/images/4/4f/Kisto_rs232_commands_v106.pdf) can be found at the [LINNDOCS web site](http://docs.linn.co.uk/wiki/index.php/RS232).


## Acknowledgements

 - App icon by Mr. Truesound
 - Many thanks to the LINN customer support


## Questions and Anwsers

**Is the app collecting any of my data?** 
Absolutely not! The only data the app is sending out are the control codes needed to 
talk to the Linn device, and it only uses the IP address you configure for that device.
There is no data collected and no data sent to any outside party.

**Why is the app not on the Google Play store?**
Registering for the Google Play store as a developer comes with a fee. Since this is the only 
app I developed so far, it didn't seem worth it for me. Maybe this will change in the future,
but there are currently no such plans.

**Why are there no automated tests?**
Shame on me! Normally, I am a big proponent of automated tests and test-driven development. 
However, since this was my first go at an Android app there was a lot of trial and error 
involved for me. Add to that a logic which relies heavily on network communication with a 
device for which I had to figure out some of the behaviour on the go, I saw it as too 
restrictive to add a test double only to find out later that the behavior of the real 
device is different anyhow.

**Why can I add more than one device? Who would ever need that?**
I do! ;-) We have one Kinos and one Klimax Kontrol devices in the house and since I primarily 
wrote the app for ourselves, this is how it turned out. If I find the time and motivation I 
might change the UI in the future to only have the device control screen and have a selection 
possibility for several devices embedded there.

**Is there an iOS version?**
No. The reasons I opted for Android are that I am a Java developer by trade and therefore I 
found it more convenient to not have to learn a new language at the same time as learning the 
SDK and figure out the behavior of the Linn devices. Also, for Android I am not necessarily 
forced to go through the official release process to get the app on my device and out into 
the world. There are no plans currently to port this to iOS. But feel free to start such a 
project yourself.


## Contact Information

Please send general feedback, questions, suggestions to mailto:developer@geekgasm.eu

To report bugs, please create a [new issue in the Github project](https://github.com/Geekgasm/kintrol/issues)


[control_device_menu]: docu-images/control_device_menu.png
[delete_device]: docu-images/delete_device.png
[device_info]: docu-images/device_info.png
[discrete_volume_button]: docu-images/discrete_volume_button.png
[edit_device]: docu-images/edit_device.png
[kinos_conrol_screen]: docu-images/kinos_conrol_screen.png
[klimax_control_screen]: docu-images/klimax_control_screen.png
[not_connected]: docu-images/not_connected.png
[standby]: docu-images/standby.png
[unity_gain]: docu-images/unity_gain.png
