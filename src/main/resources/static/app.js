// app.js
window.addEventListener('DOMContentLoaded', function() {
    // Add event listeners for mark as finished links
    var markFinishedLinks = document.getElementsByClassName('mark-finished');
    for (var i = 0; i < markFinishedLinks.length; i++) {
        markFinishedLinks[i].addEventListener('click', markAsFinished);
    }

    // Add event listeners for delete task links
    var deleteTaskLinks = document.getElementsByClassName('delete-task');
    for (var i = 0; i < deleteTaskLinks.length; i++) {
        deleteTaskLinks[i].addEventListener('click', deleteTask);
    }

    // Add event listener for create task form
    var createTaskForm = document.getElementById('create-task-form');
    if (createTaskForm) {
        createTaskForm.addEventListener('submit', createTask);
    }
});

function markAsFinished(event) {
    event.preventDefault();
    var taskId = event.target.getAttribute('data-task-id');

    // Send an AJAX request to mark the task as finished
    var xhr = new XMLHttpRequest();
    xhr.open('PATCH', '/tasks/mark_as_finished/' + taskId);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 204) {
                // Task marked as finished successfully
                // You can update the UI or perform any other action here
                console.log('Task marked as finished');
            } else {
                // Handle error response
                console.error('Error marking task as finished');
            }
        }
    };
    xhr.send();
}

function deleteTask(event) {
    event.preventDefault();
    var taskId = event.target.getAttribute('data-task-id');

    // Send an AJAX request to delete the task
    var xhr = new XMLHttpRequest();
    xhr.open('DELETE', '/tasks/' + taskId);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 204) {
                // Task deleted successfully
                // You can update the UI or perform any other action here
                console.log('Task deleted');
            } else {
                // Handle error response
                console.error('Error deleting task');
            }
        }
    };
    xhr.send();
}

function createTask(event) {
    event.preventDefault();
    var form = event.target;
    var formData = new FormData(form);

    // Validate form input fields
    // Implement your own form validation logic here

    // Send an AJAX request to create the task
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/tasks');
    xhr.setRequestHeader('Accept', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                // Task created successfully
                // You can update the UI or perform any other action here
                console.log('Task created');
                form.reset();
            } else {
                // Handle error response
                console.error('Error creating task');
            }
        }
    };
    var object = {};
    formData.forEach((value, key) => object[key] = value);
    var json = JSON.stringify(object);
    xhr.send(json);
}
