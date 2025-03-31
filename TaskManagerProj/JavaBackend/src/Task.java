import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

package TaskManagerProj.JavaBackend.src;

public class Task {
    String name;
    boolean Completed;
    int complete;
    public Date due;
    
    public Task(String name, Date due) {
        this.name = name;
        this.due = due;
        this.Completed = false;
    }

    public Task(String name) {
        this.name = name;
        this.due = new Date(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        this.Completed = false;
    }



JSONObject toJSON() {
    JSONObject obj = new JSONObject();
    obj.put("name", name);
    return obj;
}




// class TaskManager{
//     private final List<Task> tasks = new ArrayList<>();

//     public void addTask(String name){
//         tasks.add(new Task(name));
//     }
//     public void addTask(String name, Date date){
//         tasks.add(new Task(name,date));
//     }

//     JSONArray getTasks() {
//         JSONArray arr = new JSONArray();
//         for (Task task : tasks) {
//             arr.put(task.toJSON());
//         }
//         return arr;
//   







    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isCompleted() {
        return Completed;
    }
    public void setCompleted(boolean completed) {
        this.Completed = completed;
    }
    public int getComplete() {
        return complete;
    }
    public void setComplete(int complete) {
       this.complete = complete;
   }
    public Date getDue() {
        return due;
    }
    public void setDue(Date due) {
        this.due = due;
    }
}


