package TaskManagerProj.JavaBackend.src;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.json.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Task {
    String name;
    boolean Completed;
    int complete;
    public Date due;
    String category; // New field for category
    String priority; // New field for priority
    
    public Task(String name, Date due, String category, String priority) {
        this.name = name;
        this.due = due;
        this.Completed = false;
        this.category = category;
        this.priority = priority;
    }

    public Task(String name, String category) {
        this.name = name;
        this.due = new Date(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        this.Completed = false;
        this.category = category;
        this.priority = "Medium"; // Default priority
    }

    public Task(String name, Date due) {
        this.name = name;
        this.due = due;
        this.Completed = false;
        this.category = "General"; // Default category
        this.priority = "Medium"; // Default priority
    }

    public Task(String name) {
        this.name = name;
        this.due = new Date(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        this.Completed = false;
        this.category = "General"; // Default category
        this.priority = "Medium"; // Default priority
    }

    public void toggleCompletion() {
        this.Completed = !this.Completed;
    }

    JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("category", category != null ? category : "General"); // Handle null category
        obj.put("priority", priority != null ? priority : "Medium"); // Default to "Medium" if missing
        JSONObject dueObj = new JSONObject();
        dueObj.put("day", due.getDay());
        dueObj.put("month", due.getMonth());
        dueObj.put("year", due.getYear());
        obj.put("due", dueObj); // Include due date in JSON
        obj.put("completed", Completed); // Include completion status
        return obj;
    }

    public static class Date{
        int month;
        int day;
        int year;
        public Date(int month, int day, int year){
            this.day = day;
            this.month = month;
            this.year = year;
        }
        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getYear() {
            return year;
        }
    }

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
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
}


