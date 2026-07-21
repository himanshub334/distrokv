package com.distrokv;

import java.util.*;

/** Immutable causal version; compare returns 1 newer, -1 older, 0 equal/concurrent. */
public record VectorClock(Map<String, Long> entries) {
  public VectorClock { entries = Map.copyOf(entries); }
  public VectorClock increment(String node) { var next = new HashMap<>(entries); next.merge(node, 1L, Long::sum); return new VectorClock(next); }
  public int compare(VectorClock other) {
    boolean greater = false, less = false;
    var keys = new HashSet<>(entries.keySet()); keys.addAll(other.entries.keySet());
    for (var key : keys) { long a = entries.getOrDefault(key, 0L), b = other.entries.getOrDefault(key, 0L); greater |= a > b; less |= a < b; }
    return greater && !less ? 1 : less && !greater ? -1 : 0;
  }
  public String encode() { return entries.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> e.getKey()+":"+e.getValue()).reduce((a,b)->a+","+b).orElse(""); }
  public static VectorClock decode(String raw) { var map = new HashMap<String, Long>(); if (!raw.isBlank()) for (var bit : raw.split(",")) { var p=bit.split(":",2); map.put(p[0], Long.parseLong(p[1])); } return new VectorClock(map); }
}
