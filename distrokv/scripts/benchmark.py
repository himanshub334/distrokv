"""Usage: python scripts/benchmark.py --operations 1000"""
import argparse, concurrent.futures, time
from pathlib import Path
import sys
sys.path.insert(0, str(Path(__file__).parents[1] / "coordinator"))
from distrokv.client import Coordinator
p=argparse.ArgumentParser(); p.add_argument("--operations",type=int,default=100); p.add_argument("--workers",type=int,default=16); a=p.parse_args(); c=Coordinator(); start=time.perf_counter()
def write(i): c.put(f"bench:{i}", f"payload-{i}")
with concurrent.futures.ThreadPoolExecutor(max_workers=a.workers) as ex: list(ex.map(write,range(a.operations)))
elapsed=time.perf_counter()-start; print(f"writes={a.operations} elapsed={elapsed:.2f}s throughput={a.operations/elapsed:.1f} ops/s")
