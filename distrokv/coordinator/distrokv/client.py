import os
from urllib.parse import quote
import requests
from .ring import ConsistentHashRing
from .clock import newest

class QuorumError(RuntimeError): pass

class Coordinator:
    def __init__(self, nodes=None, replication=2, read_quorum=1, write_quorum=2, session=requests):
        self.nodes = nodes or os.getenv("DISTROKV_NODES", "http://localhost:8080,http://localhost:8081,http://localhost:8082").split(",")
        self.replication, self.read_quorum, self.write_quorum, self.session = replication, read_quorum, write_quorum, session
        if read_quorum + write_quorum <= replication: raise ValueError("require R + W > N")
        self.ring = ConsistentHashRing(self.nodes)
    def targets(self, key): return self.ring.replicas(key, self.replication)
    def put(self, key, value, clock=""):
        acknowledgements = 0
        for node in self.targets(key):
            try:
                response = self.session.put(f"{node}/kv/{quote(key, safe='')}", data={"value": value, "clock": clock}, timeout=1.5)
                acknowledgements += response.ok
            except requests.RequestException: pass
        if acknowledgements < self.write_quorum: raise QuorumError(f"write quorum not met: {acknowledgements}/{self.write_quorum}")
    def get(self, key):
        records = []
        for node in self.targets(key):
            try:
                response = self.session.get(f"{node}/kv/{quote(key, safe='')}", timeout=1.5)
                if response.ok: records.append(response.json())
            except requests.RequestException: pass
        if len(records) < self.read_quorum: raise QuorumError(f"read quorum not met: {len(records)}/{self.read_quorum}")
        return newest(records) if records else None
    def health(self):
        result = {}
        for node in self.nodes:
            try: result[node] = self.session.get(f"{node}/health", timeout=1).json()
            except requests.RequestException: result[node] = {"status": "unreachable"}
        return result
