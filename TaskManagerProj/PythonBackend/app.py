from flask import Flask, request, jsonify
import json
from datetime import datetime

app = Flask(__name__)

# In-memory task storage
tasks = []

# Load tasks from a file
def load_tasks(file_path="tasks.json"):
    global tasks
    try:
        with open(file_path, "r") as file:
            tasks = json.load(file)
    except FileNotFoundError:
        tasks = []

# Save tasks to a file
def save_tasks(file_path="tasks.json"):
    with open(file_path, "w") as file:
        json.dump(tasks, file, indent=4)

# Add a new task
@app.route("/tasks", methods=["POST"])
def add_task():
    data = request.json
    name = data.get("name")
    category = data.get("category", "General")
    priority = data.get("priority", "Medium")
    due_date = data.get("due", None)
    completed = False

    if not name:  # Ensure task name is provided
        return jsonify({"error": "Task name is required"}), 400

    if due_date:
        try:
            due_date = datetime.strptime(due_date, "%Y-%m-%d").strftime("%Y-%m-%d")
        except ValueError:
            return jsonify({"error": "Invalid date format. Use YYYY-MM-DD."}), 400

    task = {
        "name": name,
        "category": category,
        "priority": priority,
        "due": due_date,
        "completed": completed
    }
    tasks.append(task)
    save_tasks()
    return jsonify({"message": "Task added successfully"}), 201

# Get all tasks
@app.route("/tasks", methods=["GET"])
def get_tasks():
    return jsonify(tasks), 200

# Get tasks sorted by due date
@app.route("/tasks/sortByDate", methods=["GET"])
def get_tasks_sorted_by_date():
    sorted_tasks = sorted(tasks, key=lambda x: x["due"] if x["due"] else "")
    return jsonify(sorted_tasks), 200

# Get tasks filtered by completion
@app.route("/tasks/filterByCompletion", methods=["GET"])
def filter_tasks_by_completion():
    completed = request.args.get("completed", "false").lower() == "true"
    filtered_tasks = [task for task in tasks if task["completed"] == completed]
    return jsonify(filtered_tasks), 200

# Get overdue tasks
@app.route("/tasks/overdue", methods=["GET"])
def get_overdue_tasks():
    today = datetime.now().strftime("%Y-%m-%d")
    overdue_tasks = [task for task in tasks if task["due"] and task["due"] < today and not task["completed"]]
    return jsonify(overdue_tasks), 200

# Search tasks by keyword
@app.route("/tasks/search", methods=["GET"])
def search_tasks():
    keyword = request.args.get("keyword", "").lower()
    matching_tasks = [task for task in tasks if keyword in task["name"].lower()]
    return jsonify(matching_tasks), 200

# Mark a task as completed
@app.route("/tasks/complete", methods=["POST"])
def mark_task_as_completed():
    data = request.json
    name = data.get("name")
    for task in tasks:
        if task["name"] == name:
            task["completed"] = True
            save_tasks()
            return jsonify({"message": "Task marked as completed"}), 200
    return jsonify({"error": "Task not found"}), 404

# Delete a task
@app.route("/tasks", methods=["DELETE"])
def delete_task():
    data = request.json
    name = data.get("name")
    global tasks
    tasks = [task for task in tasks if task["name"] != name]
    save_tasks()
    return jsonify({"message": "Task deleted successfully"}), 200

if __name__ == "__main__":
    load_tasks()
    app.run(debug=True)
