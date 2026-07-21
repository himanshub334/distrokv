import argparse, json
from .client import Coordinator
def main():
    p=argparse.ArgumentParser(description="DistroKV coordinator/admin CLI"); s=p.add_subparsers(dest="command",required=True)
    put=s.add_parser("put"); put.add_argument("key"); put.add_argument("value"); put.add_argument("--clock",default="")
    get=s.add_parser("get"); get.add_argument("key")
    s.add_parser("health"); args=p.parse_args(); c=Coordinator()
    if args.command=="put": c.put(args.key,args.value,args.clock); print(json.dumps({"ok":True,"replicas":c.targets(args.key)}))
    elif args.command=="get": print(json.dumps(c.get(args.key)))
    else: print(json.dumps(c.health(),indent=2))
if __name__ == "__main__": main()
