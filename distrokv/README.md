# DistroKV

A compact, runnable distributed key-value store demonstrating consistent-hash partitioning, successor replication, vector-clock conflict resolution, quorum coordination, and a Bully-style leader election.

## Run locally

```bash
docker compose up --build --scale node=3
python -m pip install -r coordinator/requirements.txt
python -m coordinator.cli put profile:42 '{"name":"Ada"}'
python -m coordinator.cli get profile:42
python -m coordinator.cli range profile: profile:z
python -m coordinator.cli health
```

The coordinator discovers nodes through `DISTROKV_NODES` (defaults to the three compose nodes). `put` writes to `W` replicas, and `get` reads `R` replicas, selecting the causally newest vector-clock value. Defaults are `N=3, R=2, W=2`.

## Project layout

- `storage-node/` — Java 21 HTTP storage service, sorted in-memory index, heartbeat/election endpoints.
- `coordinator/` — Python request router, consistent hash ring, quorums, vector clocks, CLI.
- `docker-compose.yml` — three-node local cluster.
- `scripts/benchmark.py` — concurrent write benchmark.

## Tests

```bash
cd storage-node && mvn test
cd coordinator && pytest -q
```

This is an educational/reference implementation: durable storage, authentication, TLS, anti-entropy, and production membership should be added before production use.
