# Bireller: Your Bird Watching Companion

## Introduction

Bireller is a comprehensive bird watching application designed to enhance the experience for bird enthusiasts. The app seamlessly integrates powerful features to assist bird watchers in navigating between bird hotspots, recording observations, exploring bird data, and creating a personalized bird-watching diary. Bireller is built using Android Studio and Kotlin (Version 5), utilizing OpenStreet Maps, Firebase, and various API integrations to provide a user-friendly interface with a wide array of functionalities.

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Credits](#credits)
- [License](#license)
- [Author](#author)
- [Troubleshooting](#troubleshooting)
- [Requirements](#requirements)

## Version Information

Bireller Version: 5.0

## Features

### Map Integration
- **Hotspots and Observations:** Explore bird hotspots and points where users have made observations on an interactive map.
- **Filtering:** Easily filter the map based on unit systems (metric/imperial) and the desired travel distance.

### User Authentication
- **Login and Registration:** Securely log in or register to access personalized features and sync data across devices.
- **Profile Setup:** Customize your profile with personal details and preferences.

### Observation Management
- **Observation History:** View a log of all recorded bird observations.
- **Details View:** Expand RecyclerView items to see detailed information, including bird images, location details, bird species, and geographical coordinates.

### Recording Feature
- **Session Recording:** Record bird-watching sessions or use it as a personal diary for future reference.

### Additional Features
- **Website Explorer:** Explore bird-related APIs from eBird to enrich your bird-watching experience.
- **Bird Explorer:** Access information about well-known bird species worldwide.
- **Tic Tac Toe:** Enjoy a game of Tic Tac Toe for added entertainment.

### User Management
- **Profile Updates:** Modify and update your profile information as needed.
- **Account Deletion:** Easily delete your account if desired.

### User Interface
- **Dark/Light Mode:** Switch between dark and light modes for a comfortable viewing experience.

### Miscellaneous
- **About Us:** Learn more about the app and the development team.
- **Rate Us:** Share your feedback and rate the app to help us improve.

## Installation

To install Bireller from the Git repository, follow these steps:

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/Bireller.git
    ```

2. Open the project in Android Studio.

3. Build and run the project on an Android emulator or a connected Android device.

## Usage

Once installed, follow these steps to use Bireller:

1. **Registration/Login:**
   - Open the app and either log in with your existing account or register a new one.
   - Create your profile.

2. **Map Exploration:**
   - Navigate to the map section to explore bird hotspots and observation points.
   - Use the filtering options to customize the map based on unit systems and travel distance.

3. **Observation Recording:**
   - Record bird observations by tapping on the pin location or using the dedicated recording feature.
   - View your observation history and details in the Observation Management section.

4. **Additional Features:**
   - Explore additional features such as the Website Explorer, Bird Explorer, and Tic Tac Toe for added entertainment.

5. **Profile Management:**
   - Update your profile information as needed.
   - Optionally, delete your account if desired.

6. **Customization:**
   - Adjust app settings, including the choice between dark and light modes.

7. **Providing Feedback:**
   - Share your thoughts by rating the app and providing feedback in the "Rate Us" section.

## Configuration

Bireller uses Firebase for data storage. To set up Firebase for your project, follow these steps:
1. Create a Firebase project on the [Firebase Console](https://console.firebase.google.com/).
2. Obtain your Firebase configuration (apiKey, authDomain, projectId, etc.).
3. Replace the placeholder values in the `firebaseConfig` object in the `src/firebase.kt` file with your configuration.


## Credits

Bireller is developed and maintained by Hailey Jade Govender.

## License

Bireller is licensed under the [MIT License](LICENSE). See the [LICENSE](LICENSE) file for details.

## Author

- Hailey Jade Govender
- Contact: haileygovender15@gmail.com

## Troubleshooting

If you encounter any issues or have questions, please check contact haileygovender15@gmail.com.

## Requirements

### Functional Requirements
- **User Authentication:** Users should be able to log in and register to access personalized features.
- **Map Integration:** The app should provide an interactive map displaying bird hotspots and user observations.
- **Observation Management:** Users should be able to view and expand on their bird observations, including detailed information.
- **Recording Feature:** The app should allow users to record bird-watching sessions or use it as a personal diary.
- **Additional Features:** Explore bird-related APIs, access information about well-known bird species, and enjoy additional entertainment features.

### Non-Functional Requirements
- **Performance:** The app should respond promptly to user interactions and provide a smooth experience.
- **Security:** User data should be securely stored and transmitted using best practices.
- **Scalability:** The app should handle a growing user base and data load effectively.
- **Usability:** The user interface should be intuitive, providing a pleasant experience for users of all levels.
- **Compatibility:** The app should work seamlessly on a variety of Android devices.

---

We hope Bireller enhances your bird-watching experience and brings you closer to the fascinating world of birds. Happy birding!

*Note: Make sure to check the app settings for customization options and additional features.*
