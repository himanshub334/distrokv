# DistroKV

A fault-tolerant distributed key-value storage and query system built using Java and Python, inspired by the architecture of modern distributed databases such as Amazon Dynamo, Apache Cassandra, and Riak.

DistroKV demonstrates core distributed systems concepts including consistent hashing, replication, quorum consensus, vector clocks, leader election, distributed indexing, fault tolerance, and horizontal scalability.

---

## Overview

DistroKV is designed as a distributed storage engine capable of serving read and write requests even when multiple nodes fail.

The system automatically partitions data across cluster nodes using consistent hashing with virtual nodes, replicates data for high availability, performs quorum-based consistency, and automatically elects a new leader whenever failures occur.

The project focuses on building a scalable, reliable, and operationally simple storage platform suitable for cloud-native deployments.

---

## Features

- Distributed Key-Value Storage
- Consistent Hashing with Virtual Nodes
- Automatic Data Replication
- Quorum Reads and Writes
- Vector Clock Conflict Resolution
- Distributed B-Tree Index
- Leader Election (Bully Algorithm)
- Heartbeat Failure Detection
- Automatic Node Registration
- Online Rebalancing
- Zero-Downtime Node Addition & Removal
- Python Cluster Administration CLI
- Performance Benchmarking
- Docker Deployment
- AWS EC2 Deployment
- JUnit 5 Unit Testing
- Pytest Integration Testing

---

# System Architecture

```

                    +----------------------+
                    |    Client Request    |
                    +----------+-----------+
                               |
                               |
                     Python Query Coordinator
                               |
        -------------------------------------------------
        |               |                |              |
        |               |                |              |
 +------v------+ +------v------+ +------v------+ +------v------+
 |   Node A    | |   Node B    | |   Node C    | |   Node D    |
 +------+------+ +------+------+ +------+------+ +------+------+
        |               |                |               |
        |<-----Replication Protocol--------------------->|
        |<----------Heartbeat Messages------------------>|
        |<----------Leader Election--------------------->|

Each node contains:

• Storage Engine
• B-Tree Index
• Replication Manager
• Failure Detector
• Network Server

```

---

# Technology Stack

## Backend

- Java
- Python

## Distributed Systems

- Consistent Hashing
- Virtual Nodes
- Data Replication
- Vector Clocks
- Quorum Consensus
- Bully Leader Election
- Heartbeat Failure Detection

## Infrastructure

- Docker
- AWS EC2
- Shell Scripts

## Testing

- JUnit 5
- Pytest

---

# Core Components

## 1. Consistent Hashing

Keys are mapped onto a hash ring.

Each physical node owns multiple virtual nodes to ensure even key distribution and minimize data movement when scaling.

Benefits:

- Balanced load
- Efficient horizontal scaling
- Minimal resharding
- High availability

---

## 2. Replication

Every key is replicated to **R successor nodes**.

Example:

```

Replication Factor = 3

Key

↓

Primary Node

↓

Replica 1

↓

Replica 2

```

The cluster continues serving requests even if multiple nodes become unavailable.

---

## 3. Quorum Consensus

Read and write operations use quorum-based consistency.

```

R + W > N

```

Where:

- **R** = Read quorum
- **W** = Write quorum
- **N** = Number of replicas

This ensures clients receive the most recent committed value while balancing consistency and availability.

---

## 4. Vector Clocks

Concurrent updates are tracked using vector clocks.

Instead of overwriting values blindly, the coordinator compares version histories and returns the latest non-conflicting value.

This prevents lost updates during concurrent writes.

---

## 5. Distributed B-Tree Index

Each storage node maintains an in-memory B-Tree index.

Performance:

- Point Lookup → **O(log n)**
- Range Query → **O(k)**

This enables efficient retrieval while supporting ordered scans.

---

## 6. Leader Election

The cluster uses a Bully Algorithm variant.

If heartbeat messages stop arriving:

1. Failure detected
2. Election triggered
3. Highest-priority node becomes leader
4. Cluster resumes operation

Leader election typically completes within three communication rounds.

---

## 7. Failure Detection

Nodes continuously exchange heartbeat messages.

Failures automatically trigger:

- Leader election
- Replica reassignment
- Cluster recovery

---

## 8. Automatic Rebalancing

Adding a new node requires no manual data migration.

The cluster:

- Registers the node
- Recomputes hash ranges
- Migrates affected keys
- Continues serving traffic

No downtime is required.

---

## Performance Optimization

Benchmarking identified Java serialization as a throughput bottleneck.

### Before

- Java Serialization

### After

- Lightweight Binary Protocol

Result:

- **3.2× higher write throughput**
- Lower serialization overhead
- Reduced latency

Benchmarks were executed across varying:

- Cluster sizes
- Replication factors
- Read/write workloads

---

## Testing

### Unit Testing

JUnit 5 verifies:

- Hash Ring
- Replication Logic
- Leader Election
- B-Tree Operations
- Vector Clocks

---

### Integration Testing

Pytest validates:

- Node failures
- Network partitions
- Replica recovery
- Cluster rebalancing
- Simultaneous failures

---

## Project Structure

```

DistroKV/

├── coordinator/
├── storage-node/
├── replication/
├── leader-election/
├── index/
├── admin-cli/
├── benchmarks/
├── docker/
├── scripts/
├── tests/
│   ├── junit/
│   └── pytest/
├── docs/
└── README.md

```

---

## Running Locally

### Clone Repository

```bash
git clone https://github.com/himanshub334/distrokv.git
cd distrokv
```

---

### Build

```bash
./gradlew build
```

or

```bash
mvn clean install
```

---

### Start Cluster

```bash
docker compose up --build
```

---

### Example Commands

Start Coordinator

```bash
python coordinator.py
```

Start Storage Node

```bash
java -jar storage-node.jar
```

Cluster Status

```bash
python admin.py status
```

Add Node

```bash
python admin.py add-node
```

Remove Node

```bash
python admin.py remove-node
```

Run Benchmarks

```bash
python benchmark.py
```

---

## Future Improvements

- Raft Consensus
- Gossip Protocol
- Merkle Trees
- Anti-Entropy Repair
- Snapshotting
- Log Compaction
- Multi-Datacenter Replication
- Prometheus Metrics
- Grafana Dashboards
- Kubernetes Deployment
- gRPC Communication
- Bloom Filters
- LSM Trees
- SSTables
- Automatic Shard Splitting

---

## Learning Outcomes

This project provided hands-on experience with:

- Distributed Systems
- Fault-Tolerant Storage
- Distributed Indexing
- Consistent Hashing
- Replication Strategies
- Leader Election
- Consensus Concepts
- Vector Clocks
- Cloud Deployment
- Docker
- AWS EC2
- Performance Engineering
- Scalability
- Reliability
- Distributed Testing

---

## Inspiration

This project draws inspiration from the design principles behind:

- Amazon Dynamo
- Apache Cassandra
- Riak
- Google Bigtable

It is intended as an educational implementation demonstrating core distributed systems concepts rather than a production-ready database.

---

## Author

**Himanshu Barde**

- GitHub: https://github.com/himanshub334
- LinkedIn: https://linkedin.com/in/himanshub334
- LeetCode: https://leetcode.com/himanshub334
