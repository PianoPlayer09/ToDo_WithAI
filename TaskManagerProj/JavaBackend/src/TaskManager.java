package TaskManagerProj.JavaBackend.src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.json.*;
import org.json.JSONException;

class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private final String filePath;

    public TaskManager(String filePath) {
        this.filePath = filePath;
        loadTasks();
    }

    public void addTask(String name) {
        tasks.add(new Task(name));
        saveTasks();
    }

    public void addTask(String name, Task.Date date) {
        tasks.add(new Task(name, date));
        saveTasks();
    }

    public void addTask(String name, Task.Date date, String category, String Priority) {
        tasks.add(new Task(name, date, category, Priority));
        saveTasks();
    }

    public void addTask(String name, String category) {
        tasks.add(new Task(name, category));
        saveTasks();
    }

    public JSONArray getTasks() {
        return new JSONArray(tasks.stream().map(Task::toJSON).collect(Collectors.toList()));
    }

    public JSONArray getTasksSortedByDate() {
        return new JSONArray(tasks.stream()
                .sorted(Comparator.comparing(task -> task.getDue().year * 10000 + task.getDue().month * 100 + task.getDue().day))
                .map(Task::toJSON)
                .collect(Collectors.toList()));
    }

    public JSONObject getTasksByCategory() {
        JSONObject categorizedTasks = new JSONObject();
        tasks.stream()
                .collect(Collectors.groupingBy(Task::getCategory))
                .forEach((category, taskList) -> categorizedTasks.put(category, taskList.stream().map(Task::toJSON).collect(Collectors.toList())));
        return categorizedTasks;
    }

    public void markTaskAsCompleted(String name) {
        for (Task task : tasks) {
            if (task.getName().equals(name)) {
                task.setCompleted(true);
                saveTasks();
                return;
            }
        }
    }

    public void deleteTask(String name) {
        tasks.removeIf(task -> task.getName().equals(name));
        saveTasks();
    }

    public void setTaskPriority(String name, String priority) {
        for (Task task : tasks) {
            if (task.getName().equals(name)) {
                task.setPriority(priority);
                saveTasks();
                return;
            }
        }
    }

    public JSONArray getTasksFilteredByCompletion(boolean completed) {
        return new JSONArray(tasks.stream()
                .filter(task -> task.isCompleted() == completed)
                .map(Task::toJSON)
                .collect(Collectors.toList()));
    }

    public JSONArray getOverdueTasks() {
        return new JSONArray(tasks.stream()
                .filter(task -> {
                    Task.Date due = task.getDue();
                    LocalDate dueDate = LocalDate.of(due.getYear(), due.getMonth(), due.getDay());
                    return dueDate.isBefore(LocalDate.now()) && !task.isCompleted();
                })
                .map(Task::toJSON)
                .collect(Collectors.toList()));
    }

    public JSONArray searchTasksByKeyword(String keyword) {
        return new JSONArray(tasks.stream()
                .filter(task -> task.getName().toLowerCase().contains(keyword.toLowerCase()))
                .map(Task::toJSON)
                .collect(Collectors.toList()));
    }

    private void saveTasks() {
        try {
            Files.write(Paths.get(filePath), getTasks().toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        try {
            if (Files.exists(Paths.get(filePath))) {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                JSONArray arr = new JSONArray(content);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String name = obj.getString("name");
                    String category = obj.optString("category", "General");
                    String priority = obj.optString("priority", "Medium");
                    Task.Date due = null;
                    if (obj.has("due")) {
                        JSONObject dueJson = obj.getJSONObject("due");
                        int day = dueJson.getInt("day");
                        int month = dueJson.getInt("month");
                        int year = dueJson.getInt("year");
                        due = new Task.Date(month, day, year);
                    }
                    tasks.add(new Task(name, due, category, priority));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
