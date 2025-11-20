const API_BASE = 'http://localhost:8080/api';

//Registration
async function handleRegister(event) {
    event.preventDefault();

    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            showAlert('Registration successful! Please log in.', 'success');
            document.getElementById('pills-login-tab').click();
        } else {
            showAlert('Registration failed. Email might be taken.', 'danger');
        }
    } catch (error) {
        showAlert('Connection error to server', 'danger');
    }
}

async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    const authHeader = 'Basic ' + btoa(email + ':' + password);

    try {
        const response = await fetch(`${API_BASE}/projects`, {
            method: 'GET',
            headers: { 'Authorization': authHeader }
        });

        if (response.ok) {
            localStorage.setItem('auth', authHeader);
            localStorage.setItem('userEmail', email);
            window.location.href = 'dashboard.html';
        } else {
            showAlert('Invalid email or password', 'danger');
        }
    } catch (error) {
        console.error(error);
        showAlert('Connection error', 'danger');
    }
}

function showAlert(message, type) {
    const alertBox = document.getElementById('alertBox');
    alertBox.className = `alert alert-${type} mt-3`;
    alertBox.textContent = message;
    alertBox.classList.remove('d-none');
}

//Auth Check
function checkAuth() {
    if (!localStorage.getItem('auth')) {
        window.location.href = 'index.html';
    }
}

//Logout
function logout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

//Load Projects
async function loadProjects() {
    const listContainer = document.getElementById('projectsList');
    if (!listContainer) return;

    try {
        const response = await fetch(`${API_BASE}/projects`, {
            method: 'GET',
            headers: { 'Authorization': localStorage.getItem('auth') }
        });

        if (response.ok) {
            const projects = await response.json();
            renderProjects(projects);
        } else if (response.status === 401) {
            logout();
        }
    } catch (error) {
        console.error('Error fetching projects:', error);
        listContainer.innerHTML = '<div class="alert alert-danger">Error loading projects</div>';
    }
}

function renderProjects(projects) {
    const listContainer = document.getElementById('projectsList');

    if (projects.length === 0) {
        listContainer.innerHTML = '<div class="col-12 text-center text-muted">You don\'t have any projects yet. Create your first one!</div>';
        return;
    }

    let html = '';
    projects.forEach(project => {
        html += `
        <div class="col-md-4">
            <div class="card project-card h-100" onclick="openProject(${project.id})">
                <div class="card-body">
                    <h5 class="card-title">${project.name}</h5>
                    <p class="card-text text-muted small">${project.description || 'No description'}</p>
                </div>
                <div class="card-footer bg-white border-top-0 text-end text-muted small">
                    ID: ${project.id}
                </div>
            </div>
        </div>
        `;
    });
    listContainer.innerHTML = html;
}

async function createProject(event) {
    event.preventDefault();
    const name = document.getElementById('newProjectName').value;
    const description = document.getElementById('newProjectDesc').value;

    const modalEl = document.getElementById('createProjectModal');
    const modal = bootstrap.Modal.getInstance(modalEl);
    modal.hide();

    try {
        const response = await fetch(`${API_BASE}/projects`, {
            method: 'POST',
            headers: {
                'Authorization': localStorage.getItem('auth'),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, description })
        });

        if (response.ok) {
            document.getElementById('createProjectForm').reset();
            loadProjects();
        } else {
            alert('Error creating project');
        }
    } catch (error) {
        console.error(error);
    }
}

function openProject(projectId) {
    window.location.href = `editor.html?projectId=${projectId}`;
}



// === EDITOR LOGIC ===

// === EDITOR LOGIC ===

let currentSchemaFileId = null;
let currentDataFileId = null;

// Глобальні змінні для редакторів Ace
let aceSchemaEditor = null;
let aceDataEditor = null;

// --- Ініціалізація редактора ---
async function initEditor() {
    const params = new URLSearchParams(window.location.search);
    const projectId = params.get('projectId');

    if (!projectId) {
        showToast('No project ID specified', 'danger');
        setTimeout(() => window.location.href = 'dashboard.html', 2000);
        return;
    }

    // 1. Налаштування Ace Editor
    aceSchemaEditor = ace.edit("schemaEditorAce");
    aceSchemaEditor.setTheme("ace/theme/chrome"); // Світла тема
    aceSchemaEditor.session.setMode("ace/mode/json");
    aceSchemaEditor.setFontSize(14);

    aceDataEditor = ace.edit("dataEditorAce");
    aceDataEditor.setTheme("ace/theme/chrome");
    aceDataEditor.session.setMode("ace/mode/json");
    aceDataEditor.setFontSize(14);

    try {
        // Отримуємо назву проекту
        const projectResponse = await fetch(`${API_BASE}/projects/${projectId}`, {
            method: 'GET',
            headers: { 'Authorization': localStorage.getItem('auth') }
        });

        if (projectResponse.ok) {
            const project = await projectResponse.json();
            document.getElementById('projectNameDisplay').textContent = project.name;
        }

        // Отримуємо файли
        const filesResponse = await fetch(`${API_BASE}/projects/${projectId}/files`, {
            method: 'GET',
            headers: { 'Authorization': localStorage.getItem('auth') }
        });

        if (filesResponse.ok) {
            const files = await filesResponse.json();

            const schemaFile = files.find(f => f.fileType === 'SCHEMA');
            const dataFile = files.find(f => f.fileType === 'JSON_DATA');

            if (schemaFile) {
                currentSchemaFileId = schemaFile.id;
                // Записуємо дані в Ace Editor
                aceSchemaEditor.setValue(schemaFile.currentContent, -1);
            }
            if (dataFile) {
                currentDataFileId = dataFile.id;
                // Записуємо дані в Ace Editor
                aceDataEditor.setValue(dataFile.currentContent, -1);
            }
        } else {
            showToast('Failed to load project files', 'danger');
        }
    } catch (error) {
        console.error(error);
    }
}

// --- Збереження ---
async function saveProjectFiles(showNotification = true) {
    // Читаємо дані з Ace Editor (.getValue())
    const schemaContent = aceSchemaEditor.getValue();
    const dataContent = aceDataEditor.getValue();

    const statusLabel = document.getElementById('saveStatus');

    try {
        await fetch(`${API_BASE}/projects/files/${currentSchemaFileId}`, {
            method: 'POST',
            headers: {
                'Authorization': localStorage.getItem('auth'),
                'Content-Type': 'text/plain'
            },
            body: schemaContent
        });

        await fetch(`${API_BASE}/projects/files/${currentDataFileId}`, {
            method: 'POST',
            headers: {
                'Authorization': localStorage.getItem('auth'),
                'Content-Type': 'text/plain'
            },
            body: dataContent
        });

        if (showNotification) {
            statusLabel.style.opacity = '1';
            setTimeout(() => { statusLabel.style.opacity = '0'; }, 2000);
        }

    } catch (error) {
        showToast('Error saving files', 'danger');
    }
}

// --- Валідація ---
async function validateProject() {
    // "Тихе" збереження перед валідацією (щоб сервер мав актуальні дані)
    await saveProjectFiles(false);

    const alertBox = document.getElementById('validationAlert');
    alertBox.classList.add('d-none'); // Ховаємо попередній результат

    try {
        const response = await fetch(`${API_BASE}/projects/validate?schemaFileId=${currentSchemaFileId}&dataFileId=${currentDataFileId}`, {
            method: 'POST',
            headers: { 'Authorization': localStorage.getItem('auth') }
        });

        const result = await response.json();

        alertBox.classList.remove('d-none', 'alert-success', 'alert-danger');

        if (result.valid) {
            alertBox.classList.add('alert-success');
            alertBox.innerHTML = '<strong>✅ Valid!</strong> The JSON data matches the Schema.';
        } else {
            alertBox.classList.add('alert-danger');
            let errorHtml = '';
            if (result.errors && result.errors.length > 0) {
                errorHtml = result.errors.map(e => `<li>${e}</li>`).join('');
            } else {
                errorHtml = '<li>Unknown validation error</li>';
            }
            alertBox.innerHTML = `<strong>❌ Invalid!</strong> Found ${result.errorCount} errors:<ul>${errorHtml}</ul>`;
        }

    } catch (error) {
        showToast('Validation request failed', 'danger');
    }
}

// --- Експорт Markdown ---
async function exportMarkdown() {
    await saveProjectFiles(false); // Тихе збереження

    try {
        const response = await fetch(`${API_BASE}/projects/export/markdown/${currentSchemaFileId}`, {
            method: 'GET',
            headers: { 'Authorization': localStorage.getItem('auth') }
        });

        const markdown = await response.text();
        document.getElementById('markdownOutput').value = markdown;
        const modal = new bootstrap.Modal(document.getElementById('markdownModal'));
        modal.show();

    } catch (error) {
        showToast('Export failed', 'danger');
    }
}

function copyMarkdown() {
    const copyText = document.getElementById("markdownOutput");
    copyText.select();
    document.execCommand("copy");

    // Закриваємо модал
    const modalEl = document.getElementById('markdownModal');
    const modal = bootstrap.Modal.getInstance(modalEl);
    // modal.hide(); // Можна закрити, або лишити

    showToast('Copied to clipboard!', 'success');
}

// --- Допоміжна функція для гарних повідомлень (Toast) ---
function showToast(message, type = 'danger') {
    const toastEl = document.getElementById('liveToast');
    const toastBody = document.getElementById('toastMessage');

    // Змінюємо колір
    toastEl.className = `toast align-items-center text-white border-0 bg-${type}`;
    toastBody.textContent = message;

    const toast = new bootstrap.Toast(toastEl);
    toast.show();
}



//Flat View
async function showFlatView() {
    // 1. Зберігаємо актуальні дані
    await saveProjectFiles(false);

    try {
        // 2. Викликаємо API (передаємо ID файлу з Даними, а не Схемою)
        const response = await fetch(`${API_BASE}/projects/files/${currentDataFileId}/flat`, {
            method: 'GET',
            headers: { 'Authorization': localStorage.getItem('auth') }
        });

        const flatJson = await response.text();

        // 3. Показуємо результат
        document.getElementById('flatViewOutput').value = flatJson;
        const modal = new bootstrap.Modal(document.getElementById('flatViewModal'));
        modal.show();

    } catch (error) {
        showToast('Error generating Flat View', 'danger');
    }
}

function copyFlatView() {
    const copyText = document.getElementById("flatViewOutput");
    copyText.select();
    document.execCommand("copy");
    showToast('Copied Flat View!', 'success');
}


//VISUAL EDITOR
function openVisualEditor() {
    const rawJson = aceSchemaEditor.getValue();
    let schema;

    try {
        schema = JSON.parse(rawJson);
    } catch (e) {
        showToast('Invalid JSON in Schema editor. Please fix syntax errors first.', 'danger');
        return;
    }

    if (!schema.properties) {
        schema.properties = {}; // Створюємо, якщо немає
    }

    const tbody = document.getElementById('visualEditorTableBody');
    tbody.innerHTML = ''; // Очищаємо таблицю

    const requiredFields = Array.isArray(schema.required) ? schema.required : [];

    Object.keys(schema.properties).forEach(key => {
        const prop = schema.properties[key];
        const isRequired = requiredFields.includes(key);
        addPropertyRow(key, prop.type, prop.description, isRequired);
    });

    if (Object.keys(schema.properties).length === 0) {
        addPropertyRow();
    }

    // Показуємо модал
    const modal = new bootstrap.Modal(document.getElementById('visualEditorModal'));
    modal.show();
}

function addPropertyRow(name = '', type = 'string', desc = '', required = false) {
    const tbody = document.getElementById('visualEditorTableBody');
    const row = document.createElement('tr');

    row.innerHTML = `
        <td>
            <input type="text" class="form-control form-control-sm prop-name" value="${name}" placeholder="field_name">
        </td>
        <td>
            <select class="form-select form-select-sm prop-type">
                <option value="string" ${type === 'string' ? 'selected' : ''}>String</option>
                <option value="integer" ${type === 'integer' ? 'selected' : ''}>Integer</option>
                <option value="number" ${type === 'number' ? 'selected' : ''}>Number</option>
                <option value="boolean" ${type === 'boolean' ? 'selected' : ''}>Boolean</option>
                <option value="array" ${type === 'array' ? 'selected' : ''}>Array</option>
                <option value="object" ${type === 'object' ? 'selected' : ''}>Object</option>
            </select>
        </td>
        <td>
            <input type="text" class="form-control form-control-sm prop-desc" value="${desc || ''}" placeholder="Description...">
        </td>
        <td class="text-center">
            <input type="checkbox" class="form-check-input prop-required" ${required ? 'checked' : ''}>
        </td>
        <td class="text-center">
            <button class="btn btn-outline-danger btn-sm" onclick="this.closest('tr').remove()">🗑</button>
        </td>
    `;
    tbody.appendChild(row);
}

function saveVisualChanges() {
    const rawJson = aceSchemaEditor.getValue();
    let schema;
    try { schema = JSON.parse(rawJson); } catch (e) { schema = {}; }

    const newProperties = {};
    const newRequired = [];

    const rows = document.querySelectorAll('#visualEditorTableBody tr');

    rows.forEach(row => {
        const name = row.querySelector('.prop-name').value.trim();
        const type = row.querySelector('.prop-type').value;
        const desc = row.querySelector('.prop-desc').value.trim();
        const isRequired = row.querySelector('.prop-required').checked;

        if (name) {
            // Формуємо об'єкт властивості
            newProperties[name] = {
                type: type
            };
            if (desc) {
                newProperties[name].description = desc;
            }

            // Додаємо в required масив
            if (isRequired) {
                newRequired.push(name);
            }
        }
    });

    schema.properties = newProperties;

    if (newRequired.length > 0) {
        schema.required = newRequired;
    } else {
        delete schema.required; // Видаляємо, якщо масив порожній
    }

    aceSchemaEditor.setValue(JSON.stringify(schema, null, 2), -1);
    const modalEl = document.getElementById('visualEditorModal');
    const modal = bootstrap.Modal.getInstance(modalEl);
    modal.hide();

    showToast('Schema updated from Visual Editor!', 'success');
}