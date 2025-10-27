# Hymn-Book 📖

An Android application for managing hymns with customizable settings.  
This project uses **PreferenceFragmentCompat** for managing user settings.

---

## 📂 Branches & History

This project is maintained with multiple feature branches.  
Use this section to quickly identify which branch contains which feature / experiment.

| Branch Name  | Description / Feature | Related Files / Notes |
|--------------|-----------------------|-----------------------|
| `branch-001` | Initial **Settings Screen** with `root_preferences.xml` | `res/xml/root_preferences.xml` |
| `branch-000` | Implemented **Navigation Drawer** | `MainActivity.kt`, `drawer_menu.xml` |
| `branch-000` | Added **Room Database** for storing hymns | `HymnDao.kt`, `AppDatabase.kt` |
| `branch-000` | UI overhaul with **Material 3 theme** | `themes.xml`, `colors.xml` |
| `branch-000` | Added **Search feature** for hymns | `SearchFragment.kt` |

---

## ⚙️ Current Settings (branch-001)

Defined in: `res/xml/root_preferences.xml`

- **Signature (`signature`)** – User text signature.
- **General Settings (`general_settings`)** – Enable/disable UI-related options.
- **System UI (`system_ui`)** – Choose default screen.
- **Index Item Language (`index_item_language`)** – Select language.
- **Drawer Attachment (`drawer_attachment`)** – Enable/disable drawer attachment.
- **History Tracker (`history_tracker`)** – Enable/disable history tracking.
- **Typing Mode (`typing_mode`)** – Enable/disable typing mode.
- **Typing Speed (`typing_speed`)** – Choose speed (1f, 1s, … → values 1, 2, 4, …).

---

## 🛠️ Tech Stack

- **Kotlin**
- **PreferenceFragmentCompat**
- **AndroidX Preferences**
- **Material Components**

---

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/Hardik-Vaghani/Hymn-Book.git
