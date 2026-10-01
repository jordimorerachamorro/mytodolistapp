// app.js
document.addEventListener('DOMContentLoaded', () => {
    const dialog = document.getElementById('task-dialog');
    const form = document.getElementById('task-form');
    const dialogTitle = document.getElementById('task-dialog-title');
    const idField = document.getElementById('task-id');
    const errorMessage = document.getElementById('task-form-error');
    const saveButton = document.getElementById('save-task-btn');

    function openDialogForCreate() {
        form.reset();
        idField.value = '';
        dialogTitle.textContent = 'New task';
        errorMessage.textContent = '';
        dialog.showModal();
        document.getElementById('title').focus();
    }

    function openDialogForEdit(button) {
        form.reset();
        idField.value = button.dataset.taskId;
        document.getElementById('title').value = button.dataset.taskTitle;
        document.getElementById('description').value = button.dataset.taskDescription || '';
        document.getElementById('eta').value = button.dataset.taskEta || '';
        dialogTitle.textContent = 'Edit task';
        errorMessage.textContent = '';
        dialog.showModal();
    }

    document.getElementById('new-task-btn').addEventListener('click', openDialogForCreate);
    document.getElementById('cancel-task-btn').addEventListener('click', () => dialog.close());
    // Cerrar el modal al hacer clic fuera del formulario
    dialog.addEventListener('click', event => {
        if (event.target === dialog) dialog.close();
    });
    document.querySelectorAll('.edit-task').forEach(button => button.addEventListener('click', () => openDialogForEdit(button)));
    document.querySelectorAll('.mark-finished').forEach(button => button.addEventListener('click', markAsFinished));
    document.querySelectorAll('.delete-task').forEach(button => button.addEventListener('click', deleteTask));

    form.addEventListener('submit', async event => {
        event.preventDefault();
        const id = idField.value;
        const task = {
            title: document.getElementById('title').value.trim(),
            description: document.getElementById('description').value.trim(),
            eta: document.getElementById('eta').value
        };

        saveButton.disabled = true;
        try {
            const response = await fetch(id ? `/tasks/${id}` : '/tasks', {
                method: id ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(task)
            });
            if (response.ok) {
                window.location.reload();
                return;
            }
            errorMessage.textContent = 'Something went wrong saving the task. Please try again.';
        } catch (e) {
            errorMessage.textContent = 'Could not reach the server. Please try again.';
        }
        saveButton.disabled = false;
    });

    // Filtros (solo cliente): All / Pending / Done
    const filterButtons = document.querySelectorAll('.filter');
    const cards = document.querySelectorAll('.task-card');
    const filterEmpty = document.getElementById('filter-empty');

    filterButtons.forEach(button => button.addEventListener('click', () => {
        const filter = button.dataset.filter;
        filterButtons.forEach(b => {
            const active = b === button;
            b.classList.toggle('active', active);
            b.setAttribute('aria-selected', String(active));
        });
        let visible = 0;
        cards.forEach(card => {
            const show = filter === 'all' || card.dataset.status === filter;
            card.hidden = !show;
            if (show) visible++;
        });
        filterEmpty.hidden = visible > 0 || cards.length === 0;
    }));
});

async function markAsFinished(event) {
    const button = event.currentTarget;
    button.disabled = true;
    const response = await fetch(`/tasks/mark_as_finished/${button.dataset.taskId}`, { method: 'PATCH' });
    if (response.ok) {
        window.location.reload();
    } else {
        button.disabled = false;
        console.error('Error marking task as finished');
    }
}

async function deleteTask(event) {
    const button = event.currentTarget;
    if (!confirm('Delete this task?')) return;
    button.disabled = true;
    const response = await fetch(`/tasks/${button.dataset.taskId}`, { method: 'DELETE' });
    if (response.ok) {
        window.location.reload();
    } else {
        button.disabled = false;
        console.error('Error deleting task');
    }
}
