# Distributed Key-Value Store

This project is a **work-in-progress distributed key-value store** built from scratch in Java.  
It started as a simple single-node, in-memory store backed by `ConcurrentHashMap` and a TCP server, and is now evolving into a **multi-node distributed cluster**.

---

## 🚀 Project Goal

I’m building this project **purely for learning purposes** — to gain hands-on experience with concepts in **distributed systems** such as:

- Node communication over TCP
- Cluster formation & membership management
- Heartbeat-based health monitoring
- Data replication and consistency models
- Fault tolerance and recovery strategies

This is **not** intended for production use and may not follow all best practices — the focus is on understanding *how things work under the hood*.

---

## 📜 Features (Work in Progress)

- **Single Node Mode** – In-memory key-value store with TCP communication.
- **Cluster Mode** – Multiple nodes with static IPs, connecting to each other.
- **Heartbeat Monitoring** – Detect node failures.
- **Planned**:
  - Replication between nodes.
  - Dynamic cluster membership.
  - Snapshotting & persistence.
  - Basic consensus/leader election.

---

## 🛠️ Tech Stack

- **Language:** Java  
- **Data Store:** `ConcurrentHashMap` (in-memory)  
- **Networking:** Java TCP Server  
- **Build Tool:** Maven  

---

## 📚 Learning Focus

This project is my personal journey to explore:

- **Distributed systems fundamentals**
- **Cluster management**
- **Consistency & availability trade-offs**
- **System fault tolerance**

---

## ⚠️ Disclaimer

This repository is **not production-ready**.  
It’s a sandbox for experimentation, learning, and breaking things to understand how they work.

---

## 💡 Contributing

Since this is a learning-focused repository, I’m open to suggestions, feedback, and ideas that can help me learn better.  
Feel free to open an issue or submit a PR with improvements or explanations.


