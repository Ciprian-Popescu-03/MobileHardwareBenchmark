# Mobile Hardware Benchmark 📱

Welcome to the **Mobile Hardware Benchmark**, a comprehensive diagnostic and stress-testing application developed for **Android**.  
Its purpose is to provide users with an in-depth analysis of their device's raw performance. By pushing the hardware to its limits, the app evaluates the stability and efficiency of core components, helping users understand their device's true capabilities.

---

# Description 📖

Upon launching the app, users are presented with a centralized **Dashboard** that provides real-time system information. The application features a modular navigation system allowing users to test specific subsystems:

- **CPU Benchmarking** (Single & Multi-core)
- **GPU 3D Rendering** (OpenGL ES)
- **RAM & I/O Performance**
- **Battery & Thermal Health**
- **System Specifications**

The app doesn't just run tests; it visualizes the data through **custom real-time charts**, allowing users to monitor performance fluctuations, thermal throttling, and hardware efficiency as they happen.

---

# Features 🪄

- **CPU Stress-Testing**: Rigorous tests using **Fibonacci**, **Sieve of Eratosthenes**, and **Matrix Multiplication** algorithms to evaluate processing power.
- **GPU 3D Benchmark**: A high-intensity rendering environment built with **OpenGL ES 2.0** to measure frame rate stability and graphical throughput.
- **Memory Analytics**: Sequential and random access tests to measure **RAM** latency and bandwidth.
- **Disk I/O Testing**: Evaluates internal storage read and write speeds to identify potential data bottlenecks.
- **Battery Monitor**: Tracks temperature spikes, voltage drops, and health status during sustained high-load cycles.
- **Custom Data Visualization**: Real-time **Line and Bar Charts** built from scratch to provide instant performance feedback.
- **System Information**: Detailed reporting on device model, OS version, hardware architecture, and sensor availability.
- **Responsive UI**: A modern, fragment-based interface designed for easy navigation and clear result reporting.

---

# Tech Stack 🛠

### Core:
- **Java** (Android SDK)
- **OpenGL ES 2.0** (for 3D Graphics rendering)
- **Multithreading** (for concurrent stress-testing)

### Front-End:
- **XML** (Android Layouts)
- **Custom View Components** (for real-time Charting)
- **Material Design**

### Tools:
- **Android Studio**
- **Gradle** (Dependency & Build Management)
- **Git/GitHub** (Version Control)

---

# Installation ⚙️

## Prerequisites:
- Android Studio (Ladybug or newer recommended)
- Java JDK 11 or 17
- Android Device or Emulator (API Level 24+)

## Steps:
1. **Clone the repository**:
   ```bash
   git clone [https://github.com/Ciprian-Popescu-03/MobileHardwareBenchmark.git](https://github.com/Ciprian-Popescu-03/MobileHardwareBenchmark.git)
