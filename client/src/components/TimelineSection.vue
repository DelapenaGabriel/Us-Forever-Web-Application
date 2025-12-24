<script setup>
import { ref, reactive, onMounted } from "vue";
// Adjust the path to match where you saved your file
import TimelineService from "@/service/TimelineService";

// --- CONFIGURATION ---
const CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/dilpitidj/image/upload";
const UPLOAD_PRESET = "usforever";

// --- STATE ---
const events = ref([]);
const isLoading = ref(true);
const isSubmitting = ref(false);
const isModalOpen = ref(false);
const modalMode = ref("add"); // 'add' or 'edit'
const errorMsg = ref("");

// Toast State
const toast = ref({ visible: false, message: "" });

// Form Data
const form = reactive({
  id: null,
  title: "",
  date: "",
  description: "",
  icon: "💖",
  imgUrl: "",
});

const newImageFile = ref(null);
const newImagePreview = ref(null);

// Static Background Hearts
const bgHearts = [
  { top: "10%", left: "5%", size: "20px", rotate: "10deg", delay: "0s" },
  { top: "25%", left: "85%", size: "30px", rotate: "-15deg", delay: "1s" },
  { top: "50%", left: "10%", size: "15px", rotate: "5deg", delay: "2s" },
  { top: "70%", left: "90%", size: "25px", rotate: "20deg", delay: "0.5s" },
  { top: "85%", left: "15%", size: "22px", rotate: "-10deg", delay: "1.5s" },
];

// --- LIFECYCLE ---
onMounted(() => {
  fetchEvents();
});

// --- API ACTIONS ---
const fetchEvents = () => {
  isLoading.value = true;
  TimelineService.getTimeline()
    .then((response) => {
      events.value = response.data;
    })
    .catch((error) => {
      console.error("Failed to fetch events", error);
      showToast("Could not load timeline.");
    })
    .finally(() => {
      isLoading.value = false;
    });
};

const handleDelete = (id) => {
  if (!confirm("Are you sure you want to remove this memory?")) return;

  TimelineService.deleteTimeline(id)
    .then(() => {
      events.value = events.value.filter((e) => e.id !== id);
      showToast("Event removed 🗑️");
    })
    .catch((error) => {
      console.error(error);
      showToast("Failed to delete event.");
    });
};

const handleSubmit = () => {
  if (!form.title || !form.date || !form.description) {
    errorMsg.value = "Please fill in all required fields.";
    return;
  }

  isSubmitting.value = true;
  errorMsg.value = "";

  let imageUploadPromise;

  if (newImageFile.value) {
    const formData = new FormData();
    formData.append("file", newImageFile.value);
    formData.append("upload_preset", UPLOAD_PRESET);
    formData.append("folder", "timeline_photos");

    imageUploadPromise = fetch(CLOUDINARY_URL, {
      method: "POST",
      body: formData,
    })
      .then((res) => {
        if (!res.ok) throw new Error("Image upload failed");
        return res.json();
      })
      .then((data) => data.secure_url);
  } else {
    imageUploadPromise = Promise.resolve(form.imgUrl);
  }

  imageUploadPromise
    .then((finalImageUrl) => {
      const payload = {
        title: form.title,
        date: form.date,
        description: form.description,
        icon: form.icon,
        imgUrl: finalImageUrl || "",
      };

      if (modalMode.value === "add") {
        return TimelineService.createTimeline(payload).then((res) => {
          events.value.push(res.data);
          showToast("New memory added! ✨");
        });
      } else {
        return TimelineService.updateTimeline(form.id, payload).then((res) => {
          const index = events.value.findIndex((e) => e.id === form.id);
          if (index !== -1) events.value[index] = res.data;
          showToast("Memory updated! 📝");
        });
      }
    })
    .then(() => {
      closeModal();
    })
    .catch((error) => {
      console.error(error);
      errorMsg.value = "An error occurred while saving. Please try again.";
    })
    .finally(() => {
      isSubmitting.value = false;
    });
};

// --- MODAL & FORM HELPERS ---
const openAddModal = () => {
  resetForm();
  modalMode.value = "add";
  isModalOpen.value = true;
};

const openEditModal = (event) => {
  form.id = event.id;
  form.title = event.title;
  form.date = event.date;
  form.description = event.description;
  form.icon = event.icon;
  form.imgUrl = event.imgUrl;
  newImageFile.value = null;
  newImagePreview.value = null;
  modalMode.value = "edit";
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  setTimeout(() => resetForm(), 300);
};

const resetForm = () => {
  form.id = null;
  form.title = "";
  form.date = "";
  form.description = "";
  form.icon = "💖";
  form.imgUrl = "";
  newImageFile.value = null;
  newImagePreview.value = null;
  errorMsg.value = "";
};

const handleFileSelect = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  if (!file.type.match("image.*")) {
    errorMsg.value = "Select a valid image file.";
    return;
  }
  newImageFile.value = file;
  newImagePreview.value = URL.createObjectURL(file);
  errorMsg.value = "";
};

const showToast = (msg) => {
  toast.value.message = msg;
  toast.value.visible = true;
  setTimeout(() => {
    toast.value.visible = false;
  }, 3000);
};

// Helper for Layout
const isEven = (index) => index % 2 !== 0;
</script>

<template>
  <div class="timeline-page">
    <!-- Floating Background Hearts -->
    <div class="bg-decoration">
      <div
        v-for="(heart, i) in bgHearts"
        :key="i"
        class="floating-heart"
        :style="{
          top: heart.top,
          left: heart.left,
          fontSize: heart.size,
          transform: `rotate(${heart.rotate})`,
          animationDelay: heart.delay,
        }"
      >
        ♥
      </div>
    </div>

    <!-- Header -->
    <header class="timeline-header">
      <h1 class="title">Our Story</h1>
      <p class="subtitle">How it all began and where we are going.</p>
      <button class="add-event-btn" @click="openAddModal">
        <span>+</span> Add Event
      </button>
    </header>

    <!-- Timeline Container -->
    <div class="timeline-container">
      <div v-if="isLoading" class="loading-state">
        <div class="spinner large"></div>
        <p>Loading our story...</p>
      </div>

      <div v-else class="timeline-wrapper">
        <!-- The Center Vertical Line -->
        <div class="center-line"></div>

        <!-- Timeline Items -->
        <div
          v-for="(event, index) in events"
          :key="event.id"
          class="timeline-item"
          :class="{ 'item-right': isEven(index), 'item-left': !isEven(index) }"
        >
          <!-- Center Marker -->
          <div class="timeline-marker">
            <div class="marker-content">{{ event.icon }}</div>
          </div>

          <!-- Content Card -->
          <div class="timeline-content">
            <!-- Edit/Delete Actions -->
            <div class="card-actions">
              <button
                class="action-btn edit"
                @click.stop="openEditModal(event)"
                aria-label="Edit"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
              </button>
              <button
                class="action-btn delete"
                @click.stop="handleDelete(event.id)"
                aria-label="Delete"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                </svg>
              </button>
            </div>

            <span class="date-tag">{{ event.date }}</span>
            <h2 class="card-title">{{ event.title }}</h2>

            <div class="image-wrapper" v-if="event.imgUrl">
              <img :src="event.imgUrl" :alt="event.title" loading="lazy" />
            </div>

            <p class="description">{{ event.description }}</p>
          </div>
        </div>

        <!-- Footer Badge -->
        <div class="timeline-footer">
          <div class="footer-badge">Us Forever</div>
        </div>
      </div>
    </div>

    <!-- === MODAL FORM === -->
    <Transition name="modal-fade">
      <div v-if="isModalOpen" class="modal-backdrop" @click.self="closeModal">
        <div class="modal-card">
          <div class="modal-header">
            <h2>{{ modalMode === "add" ? "New Memory" : "Edit Memory" }}</h2>
            <button class="close-icon" @click="closeModal">&times;</button>
          </div>

          <div class="modal-body">
            <!-- Image Upload Area -->
            <div
              class="upload-area"
              :class="{ 'has-image': newImagePreview || form.imgUrl }"
            >
              <input
                type="file"
                id="fileInput"
                @change="handleFileSelect"
                accept="image/*"
                hidden
              />

              <!-- Case 1: No image -->
              <label
                for="fileInput"
                class="upload-label"
                v-if="!newImagePreview && !form.imgUrl"
              >
                <div class="upload-icon">📸</div>
                <p>Tap to add photo</p>
              </label>

              <!-- Case 2: Has Image -->
              <div v-else class="preview-container">
                <img :src="newImagePreview || form.imgUrl" alt="Preview" />
                <label for="fileInput" class="change-photo-btn">Change Photo</label>
              </div>
            </div>

            <!-- Inputs -->
            <div class="form-group">
              <label>Title</label>
              <input type="text" v-model="form.title" placeholder="e.g. First Date" />
            </div>

            <div class="form-group row">
              <div class="col">
                <label>Date</label>
                <input type="text" v-model="form.date" placeholder="Nov 08, 2015" />
              </div>
              <div class="col-small">
                <label>Icon</label>
                <input type="text" v-model="form.icon" class="center-text" />
              </div>
            </div>

            <div class="form-group">
              <label>Story</label>
              <textarea
                v-model="form.description"
                rows="4"
                placeholder="Write about this moment..."
              ></textarea>
            </div>

            <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
          </div>

          <div class="modal-footer">
            <button class="btn-cancel" @click="closeModal" :disabled="isSubmitting">
              Cancel
            </button>
            <button class="btn-submit" @click="handleSubmit" :disabled="isSubmitting">
              <span v-if="isSubmitting" class="spinner"></span>
              {{ isSubmitting ? "Saving" : "Save" }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- === TOAST === -->
    <Transition name="toast-slide">
      <div v-if="toast.visible" class="toast-notification">
        {{ toast.message }}
      </div>
    </Transition>
  </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Dancing+Script:wght@700&family=Quicksand:wght@400;500;600;700&display=swap");

/* --- PAGE RESET & LAYOUT --- */
.timeline-page {
  min-height: 100vh;
  padding: 2rem 1rem 6rem 1rem;
  font-family: "Quicksand", sans-serif;
  position: relative;
  overflow-x: hidden;
  color: #4a4a4a;
}

/* --- BACKGROUND ANIMATION --- */
.bg-decoration {
  position: fixed; /* Fixed so they don't scroll with content on mobile */
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
  z-index: 0;
}
.floating-heart {
  position: absolute;
  color: rgba(255, 71, 87, 0.1); /* Softer color */
  animation: float 8s ease-in-out infinite;
  will-change: transform;
}
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

/* --- HEADER --- */
.timeline-header {
  text-align: center;
  margin-bottom: 3rem;
  position: relative;
  z-index: 1;
}
.title {
  font-family: "Dancing Script", cursive;
  font-size: 3rem;
  color: #ff4757;
  margin: 0;
}
.subtitle {
  color: #8899a6;
  font-size: 1rem;
  margin-top: 0.5rem;
}
.add-event-btn {
  margin-top: 1.5rem;
  background: linear-gradient(135deg, #ff6b81 0%, #ff4757 100%);
  color: white;
  border: none;
  padding: 12px 28px;
  border-radius: 50px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.3);
  font-family: "Quicksand", sans-serif;
  font-size: 1rem;
  transition: transform 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.add-event-btn:active { transform: scale(0.96); }

/* --- TIMELINE CONTAINER --- */
.timeline-container {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.timeline-wrapper {
  position: relative;
  padding: 2rem 0;
}

/* The Vertical Line */
.center-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 4px; /* Slightly thicker for visibility */
  background: #ffe0e6;
  transform: translateX(-50%);
  border-radius: 4px;
  z-index: -1;
}

/* --- FLEXBOX ITEM LAYOUT --- */
.timeline-item {
  display: flex;
  margin-bottom: 3rem;
  width: 100%;
  position: relative;
}

/* Desktop: Alternating Sides */
.item-left {
  justify-content: flex-start;
  padding-right: 50px; /* Space for line */
}
.item-left .timeline-content {
  margin-right: auto; /* Pushes content to left */
  text-align: right;
}
.item-left .card-actions {
    left: 1rem; right: auto;
}

.item-right {
  justify-content: flex-end;
  padding-left: 50px;
}
.item-right .timeline-content {
  margin-left: auto;
  text-align: left;
}

/* Content Box Width */
.timeline-content {
  width: 45%; /* Leave 10% for center line gap */
  background: white;
  padding: 1.5rem;
  border-radius: 20px;
  box-shadow: 0 10px 40px -10px rgba(255, 71, 87, 0.15);
  position: relative;
  border: 1px solid rgba(255,255,255,0.5);
}

/* --- MARKER --- */
.timeline-marker {
  position: absolute;
  left: 50%;
  top: 20px; /* Align with top of card */
  transform: translateX(-50%);
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: white;
  
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
  font-size: 1.2rem;
  box-shadow: 0 0 0 4px #fff0f3;
}

/* --- CARD INTERNALS --- */
.date-tag {
  display: inline-block;
  background: #fff0f3;
  color: #ff4757;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 700;
  margin-bottom: 0.8rem;
}

.card-title {
  font-size: 1.4rem;
  color: #2d3436;
  margin: 0 0 0.8rem 0;
  font-weight: 700;
  line-height: 1.2;
}

.image-wrapper {
  width: 100%;
  height: 220px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 1rem;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: scale-down; /* changed from scale-down for better look */
}

.description {
  color: #636e72;
  line-height: 1.6;
  font-size: 0.95rem;
  margin: 0;
  white-space: pre-wrap; /* Preserves new lines in text */
}

/* Card Actions (Edit/Delete) */
.card-actions {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: flex;
  gap: 8px;
  z-index: 10;
}

.action-btn {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #eee;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  transition: all 0.2s;
}

.action-btn.edit:hover { color: #3b82f6; background: #eff6ff; }
.action-btn.delete:hover { color: #ef4444; background: #fef2f2; }

/* --- FOOTER --- */
.timeline-footer {
  text-align: center;
  margin-top: 2rem;
}
.footer-badge {
  display: inline-block;
  background: white;
  color: #ff4757;
  padding: 8px 20px;
  border-radius: 50px;
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.1);
}

/* --- LOADING --- */
.loading-state {
  text-align: center;
  padding: 4rem 1rem;
  color: #94a3b8;
}
.spinner {
  width: 20px; height: 20px;
  border: 3px solid rgba(0,0,0,0.1);
  border-top-color: #ff4757;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
.spinner.large { width: 40px; height: 40px; margin: 0 auto 1rem; }
@keyframes spin { to { transform: rotate(360deg); } }

/* --- MODAL (MOBILE FIRST) --- */
.modal-backdrop {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  z-index: 2000;
  display: flex;
  justify-content: center;
  align-items: flex-end; /* Bottom sheet on mobile */
}

.modal-card {
  background: white;
  width: 100%;
  max-width: 500px;
  height: 90vh; /* Takes most of screen on mobile */
  border-radius: 24px 24px 0 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 -10px 40px rgba(0,0,0,0.2);
}

/* Desktop Modal Override */
@media (min-width: 768px) {
  .modal-backdrop { align-items: center; }
  .modal-card {
    height: auto;
    max-height: 85vh;
    border-radius: 24px;
    margin: 0 1rem;
  }
}

.modal-header {
  padding: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f1f5f9;
}
.modal-header h2 { margin: 0; font-size: 1.25rem; }
.close-icon {
  background: none; border: none;
  font-size: 2rem; color: #94a3b8; cursor: pointer;
  line-height: 1; padding: 0 10px;
}

.modal-body {
  padding: 1.5rem;
  overflow-y: auto; /* Scrollable content */
  flex: 1;
}

/* Form Styles */
.form-group { margin-bottom: 1.2rem; }
.form-group label {
  display: block; font-weight: 600; color: #334155; margin-bottom: 0.5rem; font-size: 0.9rem;
}
.form-group input, .form-group textarea {
  width: 100%; padding: 14px;
  border-radius: 12px; border: 1px solid #e2e8f0;
  font-family: inherit; font-size: 1rem;
  background: #f8fafc; transition: all 0.2s;
  -webkit-appearance: none; /* Remove iOS shadows */
}
.form-group input:focus, .form-group textarea:focus {
  outline: none; border-color: #ff4757; background: white;
  box-shadow: 0 0 0 3px rgba(255, 71, 87, 0.1);
}
.form-group.row { display: flex; gap: 1rem; }
.col { flex: 1; }
.col-small { width: 80px; }
.center-text { text-align: center; }

/* Image Upload */
.upload-area {
  border: 2px dashed #cbd5e1;
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  margin-bottom: 1.5rem;
  background: #f8fafc;
  cursor: pointer;
}
.upload-area.has-image { padding: 0; border: none; background: transparent; }
.preview-container img {
  width: 100%; height: 200px;
  object-fit: scale-down; border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.upload-icon { font-size: 2rem; margin-bottom: 0.5rem; }
.change-photo-btn {
  display: inline-block; margin-top: 10px;
  color: #64748b; font-size: 0.9rem; text-decoration: underline;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #f1f5f9;
  display: flex; gap: 1rem;
  background: white;
}
.btn-cancel, .btn-submit {
  flex: 1; padding: 14px;
  border-radius: 12px; border: none;
  font-weight: 700; cursor: pointer; font-size: 1rem;
}
.btn-cancel { background: #f1f5f9; color: #64748b; }
.btn-submit {
  background: #ff4757; color: white;
  display: flex; justify-content: center; align-items: center; gap: 8px;
}
.btn-submit:disabled { opacity: 0.7; }

/* --- TOAST --- */
.toast-notification {
  position: fixed; bottom: 2rem; left: 50%;
  transform: translateX(-50%);
  background: rgba(30, 41, 59, 0.9);
  color: white;
  padding: 12px 24px;
  border-radius: 50px;
  font-weight: 600;
  z-index: 3000;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  backdrop-filter: blur(4px);
  width: max-content;
  max-width: 90%;
  text-align: center;
}
.toast-slide-enter-active, .toast-slide-leave-active { transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.toast-slide-enter-from, .toast-slide-leave-to { opacity: 0; transform: translate(-50%, 100%); }

.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.2s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }

/* --- MOBILE SPECIFIC RESPONSIVENESS (BREAKPOINT 768px) --- */
@media (max-width: 768px) {
  .title { font-size: 2.2rem; }
  
  /* Reset timeline layout for single column */
  .timeline-wrapper { padding-left: 0; }
  
  /* Move line to left */
  .center-line {
    left: 20px;
    transform: none;
  }

  /* Reset Flex Items */
  .timeline-item, .item-left, .item-right {
    flex-direction: column;
    padding: 0 0 0 50px; /* Indent for line */
    margin-bottom: 2.5rem;
    align-items: flex-start;
  }

  /* Reset Content Boxes */
  .timeline-content {
    width: 100%;
    text-align: left !important; /* Force left align on all text */
    margin: 0 !important;
  }

  /* Reset Marker Position */
  .timeline-marker {
    left: 20px; /* Align with line */
    transform: translateX(-50%);
    width: 36px; height: 36px;
    font-size: 1rem;
  }

  /* Fix Actions Position on Mobile */
  .item-left .card-actions {
    right: 1rem; left: auto; /* Reset left align override */
  }
  
  .action-btn { width: 40px; height: 40px; } /* Larger touch targets */
}
</style>