import hashlib
from bisect import bisect_right

class ConsistentHashRing:
    """Hash ring with virtual nodes to spread ownership evenly."""
    def __init__(self, nodes, virtual_nodes=64):
        self.positions = []
        self.owners = []
        for node in nodes:
            for v in range(virtual_nodes):
                self.positions.append(self._hash(f"{node}#{v}")); self.owners.append(node)
        pairs = sorted(zip(self.positions, self.owners)); self.positions, self.owners = map(list, zip(*pairs))
    @staticmethod
    def _hash(value): return int.from_bytes(hashlib.sha256(value.encode()).digest()[:8], "big")
    def replicas(self, key, count):
        start = bisect_right(self.positions, self._hash(key)) % len(self.positions)
        result = []
        for offset in range(len(self.positions)):
            node = self.owners[(start + offset) % len(self.positions)]
            if node not in result: result.append(node)
            if len(result) == min(count, len(set(self.owners))): return result
        return result
