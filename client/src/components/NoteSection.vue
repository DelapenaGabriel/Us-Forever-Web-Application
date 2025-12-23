<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import NoteService from "@/service/NoteService"; // Adjust path as needed
import { format } from "date-fns"; // Optional: npm install date-fns for easy formatting

// --- STATE ---
const notes = ref([]);
const isLoading = ref(true);
const isSubmitting = ref(false);
const errorMsg = ref("");

// Modal State
const showModal = ref(false);
const modalMode = ref("create"); // 'create' or 'edit'

// Form Data
const form = reactive({
  id: null,
  title: "",
  content: "",
});

// Color Palette (Yellow, Pink, Blue, Red, Green)
const noteColors = ["#ffffba", "#ffdee9", "#d0e6ff", '#ffb3ba', 'baffc9'];

// --- LIFECYCLE ---
onMounted(() => {
  fetchNotes();
});

// --- API ACTIONS ---

// 1. READ
const fetchNotes = () => {
  isLoading.value = true;
  NoteService.getAllNotes()
    .then((response) => {
      // Sort by newest first (optional)
      notes.value = response.data.sort((a, b) => 
        new Date(b.createdAt) - new Date(a.createdAt)
      );
    })
    .catch((err) => {
      console.error(err);
      errorMsg.value = "Failed to load notes.";
    })
    .finally(() => {
      isLoading.value = false;
    });
};

// 2. CREATE & UPDATE
const handleSubmit = () => {
  if (!form.title || !form.content) {
    alert("Please fill in both title and content.");
    return;
  }

  isSubmitting.value = true;

  const payload = {
    title: form.title,
    content: form.content,
    // Backend handles ID and CreatedAt for new notes
  };

  if (modalMode.value === "create") {
    NoteService.createNote(payload)
      .then((res) => {
        // Add new note to top of list
        notes.value.unshift(res.data);
        closeModal();
      })
      .catch((err) => console.error(err))
      .finally(() => (isSubmitting.value = false));
  } else {
    // Update
    NoteService.updateNote(form.id, payload)
      .then((res) => {
        // Update local list
        const index = notes.value.findIndex((n) => n.id === form.id);
        if (index !== -1) notes.value[index] = res.data;
        closeModal();
      })
      .catch((err) => console.error(err))
      .finally(() => (isSubmitting.value = false));
  }
};

// 3. DELETE
const deleteNote = (id) => {
  if (!confirm("Are you sure you want to throw away this note?")) return;

  // Optimistic UI update: remove immediately, revert if fail
  const previousNotes = [...notes.value];
  notes.value = notes.value.filter((n) => n.id !== id);

  NoteService.deleteNote(id).catch((err) => {
    console.error(err);
    alert("Failed to delete note.");
    notes.value = previousNotes; // Revert
  });
};

// --- HELPER FUNCTIONS ---

const openCreateModal = () => {
  resetForm();
  modalMode.value = "create";
  showModal.value = true;
};

const openEditModal = (note) => {
  form.id = note.id;
  form.title = note.title;
  form.content = note.content;
  modalMode.value = "edit";
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  resetForm();
};

const resetForm = () => {
  form.id = null;
  form.title = "";
  form.content = "";
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  // Format: "Oct 10"
  return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
};

// Assign rotation based on ID to make it look "stuck" naturally
const getRotation = (id) => {
  const rotations = [-2, 1, -1, 2, 0];
  return rotations[id % rotations.length];
};

// Assign color based on index
const getColor = (index) => {
  return noteColors[index % noteColors.length];
};
</script>

<template>
  <div class="notes-container">
    
    <!-- HEADER -->
    <header class="notes-header">
      <h1 class="page-title">Love Notes</h1>
      <button class="btn-add" @click="openCreateModal">
        <span>+</span> Write Note
      </button>
    </header>

    <!-- LOADING STATE -->
    <div v-if="isLoading" class="loading-container">
      <div class="spinner"></div>
    </div>

    <!-- ERROR STATE -->
    <div v-else-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

    <!-- NOTES GRID -->
    <div v-else class="notes-grid">
      <div
        v-for="(note, index) in notes"
        :key="note.id"
        class="sticky-note"
        :style="{
          backgroundColor: getColor(index),
          transform: `rotate(${getRotation(note.id)}deg)`
        }"
      >
        <!-- The Pin Visual -->
        <div class="pin"></div>

        <!-- Note Content -->
        <div class="note-body">
          <h3 class="note-title">{{ note.title }}</h3>
          <p class="note-content">{{ note.content }}</p>
        </div>

        <!-- Footer: Date & Actions -->
        <div class="note-footer">
          <div class="actions">
            <button class="action-btn edit" @click="openEditModal(note)" title="Edit">✎</button>
            <button class="action-btn delete" @click="deleteNote(note.id)" title="Delete">&times;</button>
          </div>
          <span class="note-date">{{ formatDate(note.createdAt) }}</span>
        </div>
      </div>
    </div>

    <!-- EMPTY STATE -->
    <div v-if="!isLoading && notes.length === 0" class="empty-state">
      <p>No notes yet. Write something sweet! 💌</p>
    </div>

    <!-- MODAL -->
    <Transition name="fade">
      <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal-card">
          <h2>{{ modalMode === 'create' ? 'New Love Note' : 'Edit Note' }}</h2>
          
          <div class="form-group">
            <label>Subject</label>
            <input 
              v-model="form.title" 
              type="text" 
              placeholder="e.g. Good Morning" 
              maxlength="50"
            />
          </div>

          <div class="form-group">
            <label>Message</label>
            <textarea 
              v-model="form.content" 
              placeholder="Write your heart out..." 
              rows="5"
            ></textarea>
          </div>

          <div class="modal-actions">
            <button class="btn-cancel" @click="closeModal">Cancel</button>
            <button class="btn-save" @click="handleSubmit" :disabled="isSubmitting">
              {{ isSubmitting ? 'Posting...' : 'Post Note' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Dancing+Script:wght@700&family=Quicksand:wght@400;500;600;700&family=Patrick+Hand&display=swap');

/* --- LAYOUT --- */
.notes-container {
  /* Transparent background to let App.vue background show through */
  background: transparent; 
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 80vh;
}

/* --- HEADER --- */
.notes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 3rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-title {
  font-family: 'Dancing Script', cursive;
  font-size: 3.5rem;
  color: #ff4757;
  margin: 0;
  text-shadow: 2px 2px 0px rgba(255, 255, 255, 0.5);
}

.btn-add {
  background: #ff4757;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 50px;
  font-family: 'Quicksand', sans-serif;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.3);
  transition: transform 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-add:hover {
  transform: scale(1.05);
  background: #ff6b81;
}

/* --- GRID --- */
.notes-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
}

/* --- STICKY NOTE CARD --- */
.sticky-note {
  width: 260px;
  min-height: 260px;
  padding: 1.5rem;
  padding-top: 2.5rem; /* Space for pin */
  position: relative;
  box-shadow: 2px 4px 15px rgba(0,0,0,0.15);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  font-family: 'Patrick Hand', 'Quicksand', cursive; /* Handwritten feel */
  display: flex;
  flex-direction: column;
}

.sticky-note:hover {
  transform: scale(1.05) rotate(0deg) !important; /* Straighten on hover */
  box-shadow: 5px 10px 25px rgba(0,0,0,0.2);
  z-index: 10;
}

/* The Pin Visual */
.pin {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 15px;
  height: 15px;
  background: #ff6b6b; /* Red pin head */
  border-radius: 50%;
  box-shadow: inset -2px -2px 2px rgba(0,0,0,0.2), 2px 2px 5px rgba(0,0,0,0.2);
  z-index: 2;
}
.pin::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  width: 2px;
  height: 10px;
  background: rgba(0,0,0,0.2); /* Pin shadow */
}

/* Content */
.note-body {
  flex-grow: 1;
}

.note-title {
  font-size: 1.4rem;
  margin: 0 0 0.5rem 0;
  color: #2d3436;
  font-weight: 700;
}

.note-content {
  font-size: 1.1rem;
  color: #4b4b4b;
  line-height: 1.4;
  white-space: pre-wrap; /* Keeps line breaks */
}

/* Footer */
.note-footer {
  margin-top: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.note-date {
  font-size: 0.85rem;
  color: #7f8c8d;
  font-weight: 600;
}

/* Edit/Delete Buttons (Hidden until hover) */
.actions {
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  gap: 5px;
}
.sticky-note:hover .actions {
  opacity: 1;
}

.action-btn {
  background: rgba(255,255,255,0.5);
  border: none;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  cursor: pointer;
  color: #555;
  display: flex;
  align-items: center;
  justify-content: center;
}
.action-btn:hover {
  background: white;
  color: #ff4757;
}

/* --- STATES --- */
.loading-container, .empty-state {
  text-align: center;
  padding: 4rem;
  color: #fff;
  font-family: 'Quicksand', sans-serif;
  font-size: 1.2rem;
  width: 100%;
}
.spinner {
  width: 40px; height: 40px;
  border: 4px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* --- MODAL --- */
.modal-overlay {
  position: fixed; top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  backdrop-filter: blur(3px);
  display: flex; justify-content: center; align-items: center;
  z-index: 1000;
}

.modal-card {
  background: white;
  width: 90%; max-width: 500px;
  padding: 2rem;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0,0,0,0.2);
  font-family: 'Quicksand', sans-serif;
}

.modal-card h2 {
  color: #ff4757;
  font-family: 'Dancing Script', cursive;
  font-size: 2.5rem;
  margin-top: 0;
  text-align: center;
}

.form-group {
  margin-bottom: 1.2rem;
}
.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #555;
}
.form-group input, .form-group textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #eee;
  border-radius: 12px;
  font-family: 'Quicksand', sans-serif;
  font-size: 1rem;
  box-sizing: border-box;
}
.form-group input:focus, .form-group textarea:focus {
  outline: none;
  border-color: #ff4757;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}
.btn-save, .btn-cancel {
  flex: 1;
  padding: 12px;
  border-radius: 50px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}
.btn-save {
  background: #ff4757;
  color: white;
}
.btn-save:disabled {
  background: #ffb8c1;
  cursor: not-allowed;
}
.btn-cancel {
  background: #f1f2f6;
  color: #555;
}

/* Transitions */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* Responsive */
@media (max-width: 600px) {
  .page-title { font-size: 2.5rem; }
  .sticky-note { width: 100%; }
}
</style>