package TaskManagerProj.JavaBackend.src;
import java.util.*;
import org.json.*;

class TaskManager {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(String name) {
        tasks.add(new Task(name));
    }

    public void addTask(String name, Task.Date date) {
        tasks.add(new Task(name, date));
    }

    public JSONArray getTasks() {
        JSONArray arr = new JSONArray();
        for (Task task : tasks) {
            arr.put(task.toJSON());
        }
        return arr;
    }
}
