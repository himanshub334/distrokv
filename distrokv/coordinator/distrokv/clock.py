def parse(raw):
    return {k: int(v) for k, v in (part.split(":", 1) for part in raw.split(",") if part)}

def dominates(left, right):
    keys = set(left) | set(right)
    return any(left.get(k, 0) > right.get(k, 0) for k in keys) and all(left.get(k, 0) >= right.get(k, 0) for k in keys)

def newest(records):
    """Choose a causal winner; deterministic lexical tie-break for concurrent versions."""
    winner = records[0]
    for candidate in records[1:]:
        if dominates(parse(candidate["clock"]), parse(winner["clock"])) or candidate["clock"] > winner["clock"]:
            winner = candidate
    return winner
