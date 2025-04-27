const apiUrl = "http://localhost:5000/tasks"; // Updated to match Python backend URL

async function fetchTasks() {
    try {
        const allTasksResponse = await fetch(apiUrl);
        if (!allTasksResponse.ok) throw new Error(`Failed to fetch tasks: ${allTasksResponse.statusText}`);
        const allTasks = await allTasksResponse.json();
        renderTasks(allTasks, "tasks");
    } catch (error) {
        console.error("Error fetching tasks:", error);
    }
}

function renderTasks(tasks, containerId) {
    const tasksDiv = document.getElementById(containerId);
    tasksDiv.innerHTML = "";
    tasks.forEach(task => {
        const taskDiv = document.createElement("div");
        taskDiv.className = "task";
        if (task.completed) taskDiv.classList.add("completed");
        if (task.due && new Date(task.due) < new Date() && !task.completed) taskDiv.classList.add("overdue");

        taskDiv.innerHTML = `
            <div class="task-details">
                <strong>${task.name}</strong> (${task.priority})<br>
                Category: ${task.category || "General"}<br>
                Due: ${task.due || "No due date"}
            </div>
            <div class="task-actions">
                <button class="complete" onclick="markCompleted('${task.name}')">Complete</button>
                <button class="delete" onclick="deleteTask('${task.name}')">Delete</button>
            </div>
        `;
        tasksDiv.appendChild(taskDiv);
    });
}

async function addTask(event) {
    event.preventDefault();
    const name = document.getElementById("taskName").value;
    const due = document.getElementById("taskDue").value || null; // Ensure null is sent if no date is provided
    const category = document.getElementById("taskCategory").value || "General";
    const priority = document.getElementById("taskPriority").value || "Medium";

    try {
        const response = await fetch(apiUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, due, category, priority })
        });
        if (!response.ok) throw new Error("Failed to add task");
        document.getElementById("taskForm").reset();
        await fetchTasks(); // Ensure tasks are fetched after adding
    } catch (error) {
        console.error("Error adding task:", error);
    }
}

async function markCompleted(name) {
    try {
        const response = await fetch(`${apiUrl}/complete`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name })
        });
        if (!response.ok) throw new Error("Failed to mark task as completed");
        fetchTasks();
    } catch (error) {
        console.error("Error marking task as completed:", error);
    }
}

async function deleteTask(name) {
    try {
        const response = await fetch(apiUrl, {
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name })
        });
        if (!response.ok) throw new Error("Failed to delete task");
        fetchTasks();
    } catch (error) {
        console.error("Error deleting task:", error);
    }
}

document.getElementById("taskForm").addEventListener("submit", addTask);
fetchTasks();
