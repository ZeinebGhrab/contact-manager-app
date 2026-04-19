# 📱 Android Contact Manager App

A simple and well-structured Android application for managing contacts, designed using **Clean Architecture principles** and **layered separation of concerns**.

---

## 🚀 Features

- ➕ Add new contacts
- ✏️ Edit existing contacts
- ❌ Delete contacts
- 🔍 Search contacts by name
- 📋 Display contacts using RecyclerView
- ✅ Input validation (name & phone number)

---

## 🏗️ Architecture

This project follows a **3-layer architecture**:
```
Presentation (UI)
   └── MainActivity, RecyclerView, Adapter

Business Logic (Service)
   └── ContactService

Data Layer (DAO)
   └── ContactDAO, SQLite Database
```

### 🔹 Key Principles
- Separation of Concerns
- Single Responsibility Principle (SRP)
- Clean and maintainable code structure

---

## 🧠 Technologies Used

- Java (Android)
- SQLite Database
- RecyclerView & Adapter
- Material Design Components

---

## 📂 Project Structure

```
com.example.contactlist

├── adapter/        # RecyclerView Adapter
├── database/       # DAO & Database Helper
├── model/          # Data models
├── service/        # Business logic
└── MainActivity    # UI layer

```
---

## ⚙️ Installation

1. Clone the repository:
```
   git clone https://github.com/ZeinebGhrab/contact-manager-app.git
```
2. Open the project in Android Studio

3. Run the app on an emulator or physical device

---

## 💡 Future Improvements

- Implement MVVM (ViewModel + LiveData)
- Add Room Database
- Improve UI/UX design
- Add unit testing
- Integrate REST API (optional)

---

## 👩‍💻 Author

Zeineb Ghrab  
Data & Decisional Systems Engineer Student

---

## 📄 License

This project is open-source and available under the MIT License.
