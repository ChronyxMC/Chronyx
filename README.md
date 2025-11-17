[![License: GPL-3.0](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)  
[![GitHub stars](https://img.shields.io/github/stars/ChronyxMC/Chronyx)](https://github.com/ChronyxMC/Chronyx)  
[![GitHub forks](https://img.shields.io/github/forks/ChronyxMC/Chronyx)](https://github.com/ChronyxMC/Chronyx)  

**Chronyx** is a high-performance fork of Canvas (build #557), building upon the optimizations of Folia. It focuses on performance enhancements, bug fixes, and additional features for dedicated Minecraft servers.

---

## Features & Highlights

### Built on Canvas Build #557
- Chronyx is built on top of Canvas build #557, inheriting all of Canvas's performance optimizations including the rewritten scheduler and optimized chunk generation.
- It aims to be extremely stable.
---

## Getting Started

### Downloading & Running

1. Download the latest server JAR from the **Releases** page or build artifacts.
2. Launch using Java (Java 21+ required) with your preferred arguments and configuration.

### Building from Source

**Requirements:**

- Java 21
- Git (configured with name/email)

**Common build commands:**

```bash
./gradlew applyAllPatches
./gradlew buildMojmapPublisherJar
./gradlew runDevServer
```

There is also a helper script:

```bash
./rebuildPatches.sh
```

which regenerates patches for modified directories.

---

## Contributing

We welcome many forms of contributions:

* Code (bug fixes, features)
* Documentation improvements
* Testing & bug reporting
* Community help & support

---

## Compatibility & Notes

* Chronyx is a fork of **Canvas** (which is based on **Folia**) and is *not* a drop-in replacement for Purpur, Paper, or other non-Folia forks. It is intended primarily for environments already using Canvas, Folia, or Folia-based forks.
* The project adheres strictly to Folia's threading and safety rules and does *not* permit bypassing them.

---

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

---

## Acknowledgments & Inspiration

Chronyx is built on top of **Canvas**, which incorporates patches inspired by or derived from other high-performance projects (e.g. **Lithium**), along with its own custom optimizations. We are grateful to the Canvas, Folia, Paper, and Spigot teams for their excellent work.
