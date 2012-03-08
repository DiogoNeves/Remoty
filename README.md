Use your keyboard media keys to control your smartphone's media player, when connected to your desktop/laptop

### To connect using USB

`adb forward tcp:3232 tcp:3232`

### Test commands

1. Start the app
2. Press start button
3. `telnet locahost 3232`
4. type `1234` and return (later this key will be generated)
5. Type a valid, case-sensitive, command (followed by line break)
  - **PP** play/pause
  - **P** previous
  - **N** next
  - **M** mute
  - **VU** volume up
  - **VD** volume down

