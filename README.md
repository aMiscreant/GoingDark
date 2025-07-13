# GoingDark

GoingDark is an Android app designed to provide a one-tap lockdown mode that enhances your device's privacy and security by disabling key services and clearing sensitive data. It requires root access to perform various system-level operations such as toggling airplane mode, disabling Wi-Fi and Bluetooth, clearing logs, and more.

---

## Features

- Toggle lockdown mode with a simple tap.
- Disable Wi-Fi, Bluetooth, mobile data, and airplane mode.
- Flush DNS and ARP caches.
- Disable location services and clear clipboard.
- Clear system logs and network stats.
- Disable ADB and clear ADB keys.
- Reset Advertising ID (if supported).
- Disable lockscreen notifications.
- Disable background Wi-Fi and BLE scanning.
- Removes the app from recent tasks while lockdown is active.
- Persistent foreground notification to indicate lockdown status.
- Requires root access (tested with Magisk).

---

## Requirements

- Android device with **root access** (Magisk recommended).
- Android 6.0+ (some features depend on Android version).
- Android 13+ requires notification permission for persistent notification.

---

## Installation

1. Clone or download the repository.
2. Build the APK using Android Studio or your preferred toolchain.
3. Install the APK on your rooted Android device.
4. Grant notification permission if prompted (Android 13+).
5. Open the app and tap the lock icon to enable or disable lockdown mode.

---

## Usage

- Tap the lock icon to activate lockdown mode.
- A confirmation dialog will warn you about the actions.
- Once active, the app disables network interfaces, clears logs, disables location, and more.
- Tap again to disable lockdown and restore previous settings.

---

## Important Notes

- **Root access is mandatory** for the app to function properly. Without root, the commands will fail.
- Some commands and features depend on the Android version and device manufacturer.
- Use with caution as this app changes system settings that may affect normal device operation.
- Tested with Magisk root on Pixel devices; behavior may vary on other devices.

---

## Screenshots

*(Add some screenshots here if possible)*

---

## License

MIT License — see the [LICENSE](LICENSE) file for details.

---

## Contributing

Feel free to open issues or submit pull requests for bug fixes and improvements.

---

## Contact

Developed by aMiscreant  
GitHub: [https://github.com/aMiscreant/GoingDark](https://github.com/aMiscreant/GoingDark)

---

