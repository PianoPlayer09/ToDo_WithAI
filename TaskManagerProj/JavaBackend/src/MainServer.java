package TaskManagerProj.JavaBackend.src;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class MainServer{
    static TaskManager taskManager = new TaskManager("tasks.json"); // Pass file path to TaskManager

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", new TaskHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server is running on http://localhost:8080");
    }

    static class TaskHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            // Add CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content for preflight requests
                return;
            }

            String method = exchange.getRequestMethod();
            
            if (method.equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                byte[] response;
                if ("sortByDate=true".equals(query)) {
                    response = taskManager.getTasksSortedByDate().toString().getBytes(StandardCharsets.UTF_8);
                } else if (query != null && query.contains("filterByCompletion")) {
                    boolean completed = Boolean.parseBoolean(query.split("=")[1]);
                    response = taskManager.getTasksFilteredByCompletion(completed).toString().getBytes(StandardCharsets.UTF_8);
                } else if ("overdue=true".equals(query)) {
                    response = taskManager.getOverdueTasks().toString().getBytes(StandardCharsets.UTF_8);
                } else if (query != null && query.startsWith("search=")) {
                    String keyword = query.split("=")[1];
                    response = taskManager.searchTasksByKeyword(keyword).toString().getBytes(StandardCharsets.UTF_8);
                } else {
                    response = taskManager.getTasks().toString().getBytes(StandardCharsets.UTF_8);
                }
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();

            } else if (method.equalsIgnoreCase("POST")) {
                String path = exchange.getRequestURI().getPath();
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                try {
                    JSONObject json = new JSONObject(body);
                    if ("/tasks/priority".equals(path)) {
                        String name = json.getString("name");
                        String priority = json.getString("priority");
                        taskManager.setTaskPriority(name, priority);
                        String response = "Task priority updated";
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else if ("/tasks/complete".equals(path)) {
                        String name = json.getString("name");
                        taskManager.markTaskAsCompleted(name);
                        String response = "Task marked as completed";
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        String name = json.getString("name");
                        String category = json.optString("category", "General");

                        if (json.has("due")) {
                            JSONObject dueJson = json.getJSONObject("due");
                            String Priority = json.optString("priority", "Medium");
                            int day = dueJson.getInt("day");
                            int month = dueJson.getInt("month");
                            int year = dueJson.getInt("year");
                            Task.Date dueDate = new Task.Date(month, day, year);
                            taskManager.addTask(name, dueDate, category, Priority);
                        } else {
                            taskManager.addTask(name, category);
                        }

                        String response = "Task added";
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                } catch (JSONException e) {
                    exchange.sendResponseHeaders(400, -1); // Bad Request
                }
            } else if (method.equalsIgnoreCase("DELETE")) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                try {
                    JSONObject json = new JSONObject(body);
                    String name = json.getString("name");
                    taskManager.deleteTask(name);
                    String response = "Task deleted";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (JSONException e) {
                    exchange.sendResponseHeaders(400, -1); // Bad Request
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}