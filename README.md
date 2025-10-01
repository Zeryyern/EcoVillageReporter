# 🌱 EcoVillage Reporter

EcoVillage Reporter is an Android application built with **Jetpack Compose** that empowers citizens to report environmental issues such as waste dumping.  
The app combines **camera**, **location tracking**, and a simple **reporting system** to help create cleaner and more sustainable communities.

---

## 📱 Features
- 📷 Capture photos of waste or pollution  
- 📝 Add descriptions for your report  
- 📍 Automatically fetch and attach your current location (latitude/longitude)  
- 🌓 Toggle between **Light** and **Dark Mode**  
- 🌐 Switch app language (English, Bahasa Indonesia, Javanese)  
- 📜 View a **history** of all submitted reports  
- 🎨 Clean and modern UI powered by **Material 3**  

---

## 🛠 Tech Stack
- **Kotlin**  
- **Jetpack Compose (Material 3)**  
- **Coil** (for image loading)  
- **Google Play Services Location API**  
- **FileProvider** for camera image capture  
- Android **Permissions API**  

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version recommended)  
- Gradle (comes with Android Studio)  
- Android device or emulator with **Camera & Location permissions enabled**  

### Setup
1. Clone this repository:
   ```bash
   git clone https://github.com/Zeryyern/EcoVillageReporter.git
   cd EcoVillageReporter
2. Open the project in Android Studio
3. Sync Gradle files to install dependencies
4. Build & Run on your device or emulato

📂 Project StructureEcoVillageReporter/
│
├── app/
│   ├── src/main/java/com/example/ecovillage/MainActivity.kt   # Main UI & logic
│   ├── src/main/res/                                         # UI resources
│   └── src/main/AndroidManifest.xml                          # Permissions & app config
│
└── README.md

🔒 Permissions
The app requires the following:
CAMERA → to take photos for reports
ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION → to attach location data to reports

🤝 Contributing
Contributions are welcome! 🎉
If you have suggestions, feel free to:
Open an Issue
Fork the Repository and submit a Pull Request
Or reach out directly to discuss improvements
📌 Whenever upgrades are suggested, you can also fork the project directly and make the changes.

📬 Contact

Author: Zayyanu Awwal (Zeryyern)

GitHub: Zeryyern


---

⭐ If you find this project useful, don’t forget to star the repo!
