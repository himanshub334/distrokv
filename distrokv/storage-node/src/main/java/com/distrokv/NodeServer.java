package com.distrokv;
import com.sun.net.httpserver.*;
import java.io.*; import java.net.*; import java.nio.charset.*; import java.time.*; import java.util.*; import java.util.concurrent.*;

public final class NodeServer {
  private static final SortedIndex index = new SortedIndex();
  private static final String NODE = System.getenv().getOrDefault("NODE_ID", "node-1");
  private static String read(InputStream in) throws IOException { return new String(in.readAllBytes(), StandardCharsets.UTF_8); }
  private static void reply(HttpExchange x, int status, String body) throws IOException { byte[] b=body.getBytes(StandardCharsets.UTF_8); x.getResponseHeaders().set("Content-Type","application/json"); x.sendResponseHeaders(status,b.length); try(var out=x.getResponseBody()){out.write(b);} }
  private static Map<String,String> form(String raw) { var result=new HashMap<String,String>(); for(var p:raw.split("&")){var kv=p.split("=",2); if(kv.length==2) result.put(URLDecoder.decode(kv[0],StandardCharsets.UTF_8),URLDecoder.decode(kv[1],StandardCharsets.UTF_8));} return result; }
  private static String json(ValueRecord r) { return "{\"value\":\""+r.value().replace("\\","\\\\").replace("\"","\\\"")+"\",\"clock\":\""+r.clock().encode()+"\"}"; }
  public static void main(String[] args) throws Exception {
    int port=Integer.parseInt(System.getenv().getOrDefault("PORT","8080")); var server=HttpServer.create(new InetSocketAddress(port),0);
    server.createContext("/health", x -> reply(x,200,"{\"node\":\""+NODE+"\",\"keys\":"+index.size()+",\"time\":\""+Instant.now()+"\"}"));
    server.createContext("/kv", x -> { var path=x.getRequestURI().getPath().split("/",3); if(path.length<3){reply(x,400,"{\"error\":\"key required\"}");return;} String key=URLDecoder.decode(path[2],StandardCharsets.UTF_8); if(x.getRequestMethod().equals("GET")){var r=index.get(key); reply(x,r.isPresent()?200:404,r.map(NodeServer::json).orElse("{\"error\":\"not found\"}"));} else if(x.getRequestMethod().equals("PUT")){var f=form(read(x.getRequestBody())); index.put(key,new ValueRecord(f.getOrDefault("value",""),VectorClock.decode(f.getOrDefault("clock","")).increment(NODE))); reply(x,201,"{\"ok\":true}");} else reply(x,405,"{}"); });
    server.createContext("/range", x -> { var q=form(x.getRequestURI().getRawQuery()==null?"":x.getRequestURI().getRawQuery()); var rows=index.range(q.getOrDefault("from",""),q.getOrDefault("to","~")); var body=rows.entrySet().stream().map(e->"\""+e.getKey()+"\":"+json(e.getValue())).reduce((a,b)->a+","+b).orElse(""); reply(x,200,"{"+body+"}"); });
    server.createContext("/election", x -> reply(x,200,"{\"leader\":\""+NODE+"\",\"status\":\"alive\"}"));
    server.setExecutor(Executors.newVirtualThreadPerTaskExecutor()); server.start(); System.out.println("DistroKV node "+NODE+" listening on "+port);
  }
}
