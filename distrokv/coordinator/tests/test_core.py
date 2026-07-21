from distrokv.ring import ConsistentHashRing
from distrokv.clock import newest
from distrokv.client import Coordinator, QuorumError
import pytest

def test_replica_selection_is_deterministic_and_unique():
    ring=ConsistentHashRing(["a","b","c"]); assert ring.replicas("customer:5",2)==ring.replicas("customer:5",2); assert len(set(ring.replicas("customer:5",3)))==3
def test_vector_clock_prefers_newer_record():
    assert newest([{"value":"old","clock":"a:1"},{"value":"new","clock":"a:2"}])["value"]=="new"
def test_invalid_quorum_rejected():
    with pytest.raises(ValueError): Coordinator(nodes=["a","b"],replication=2,read_quorum=1,write_quorum=1)
