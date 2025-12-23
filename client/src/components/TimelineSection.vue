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
  imgUrl: "", // Stores existing URL or URL after upload
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
  { top: "15%", left: "60%", size: "18px", rotate: "12deg", delay: "2.5s" },
];

// --- LIFECYCLE ---
onMounted(() => {
  fetchEvents();
});

// --- API ACTIONS (Using .then) ---

// 1. Fetch
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

// 2. Delete
const handleDelete = (id) => {
  if (!confirm("Are you sure you want to remove this memory?")) return;

  TimelineService.deleteTimeline(id)
    .then(() => {
      // Remove from UI immediately
      events.value = events.value.filter((e) => e.id !== id);
      showToast("Event removed 🗑️");
    })
    .catch((error) => {
      console.error(error);
      showToast("Failed to delete event.");
    });
};

// 3. Submit (Add or Update)
const handleSubmit = () => {
  if (!form.title || !form.date || !form.description) {
    errorMsg.value = "Please fill in all required fields.";
    return;
  }

  isSubmitting.value = true;
  errorMsg.value = "";

  // Step 1: Define Image Upload Promise
  let imageUploadPromise;

  if (newImageFile.value) {
    const formData = new FormData();
    formData.append("file", newImageFile.value);
    formData.append("upload_preset", UPLOAD_PRESET);
    formData.append("folder", "timeline_photos");

    // Return the fetch promise for the image
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
    // If no new file, resolve with existing URL or null
    imageUploadPromise = Promise.resolve(form.imgUrl);
  }

  // Step 2: Chain Upload -> Backend Save
  imageUploadPromise
    .then((finalImageUrl) => {
      const payload = {
        title: form.title,
        date: form.date,
        description: form.description,
        icon: form.icon,
        imgUrl: finalImageUrl || "./timeline-photos/default.png", // Fallback image
      };

      if (modalMode.value === "add") {
        // Create Logic
        return TimelineService.createTimeline(payload).then((res) => {
          events.value.push(res.data);
          showToast("New memory added! ✨");
        });
      } else {
        // Update Logic
        return TimelineService.updateTimeline(form.id, payload).then((res) => {
          const index = events.value.findIndex((e) => e.id === form.id);
          if (index !== -1) events.value[index] = res.data;
          showToast("Memory updated! 📝");
        });
      }
    })
    .then(() => {
      // Success: Close modal
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
  
  // 🔴 CHANGE THIS LINE
  // form.imgUrl = event.image; 
  
  // 🟢 TO THIS
  form.imgUrl = event.imgUrl; 

  // ... rest of function
  newImageFile.value = null;
  newImagePreview.value = null;
  modalMode.value = "edit";
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  // Small delay to clear form after animation starts
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
  errorMsg.value = ""; // clear error
};

const showToast = (msg) => {
  toast.value.message = msg;
  toast.value.visible = true;
  setTimeout(() => {
    toast.value.visible = false;
  }, 3000);
};

// Helper for Layout (Zig-Zag)
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

      <!-- ADD BUTTON -->
      <button class="add-event-btn" @click="openAddModal">
        <span>+</span> Add Event
      </button>
    </header>

    <!-- Timeline Container -->
    <div class="timeline-container">
      <!-- Loading State -->
      <div v-if="isLoading" class="loading-state">
        <div class="spinner large"></div>
        <p>Loading our story...</p>
      </div>

      <div v-else>
        <!-- The Center Vertical Line -->
        <div class="center-line"></div>

        <!-- Timeline Items -->
        <div
          v-for="(event, index) in events"
          :key="event.id"
          class="timeline-item"
          :class="{ right: isEven(index), left: !isEven(index) }"
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
                @click="openEditModal(event)"
                title="Edit"
              >
                ✎
              </button>
              <button
                class="action-btn delete"
                @click="handleDelete(event.id)"
                title="Delete"
              >
                &times;
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
          <div class="footer-badge">To be continued...</div>
        </div>
      </div>
    </div>

    <!-- === MODAL FORM === -->
    <Transition name="modal-fade">
      <div v-if="isModalOpen" class="modal-backdrop" @click.self="closeModal">
        <div class="modal-card">
          <div class="modal-header">
            <h2>
              {{ modalMode === "add" ? "Add New Memory" : "Edit Memory" }}
            </h2>
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

              <!-- Case 1: No image selected and no existing image -->
              <label
                for="fileInput"
                class="upload-label"
                v-if="!newImagePreview && !form.imgUrl"
              >
                <div class="upload-icon">☁️</div>
                <p>Click to upload photo</p>
              </label>

              <!-- Case 2: Showing Preview or Existing -->
              <div v-else class="preview-container">
                <img :src="newImagePreview || form.imgUrl" alt="Preview" />
                <label for="fileInput" class="change-photo-btn"
                  >Change Photo</label
                >
              </div>
            </div>

            <!-- Inputs -->
            <div class="form-group">
              <label>Title</label>
              <input
                type="text"
                v-model="form.title"
                placeholder="e.g. First Date"
              />
            </div>

            <div class="form-group row">
              <div class="col">
                <label>Date</label>
                <!-- Text input allows flexible formats like "Nov 08, 2015" -->
                <input
                  type="text"
                  v-model="form.date"
                  placeholder="Nov 08, 2015"
                />
              </div>
              <div class="col-small">
                <label>Icon</label>
                <input
                  type="text"
                  v-model="form.icon"
                  placeholder="💌"
                  class="center-text"
                />
              </div>
            </div>

            <div class="form-group">
              <label>Description</label>
              <textarea
                v-model="form.description"
                rows="4"
                placeholder="What happened that day?"
              ></textarea>
            </div>

            <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
          </div>

          <div class="modal-footer">
            <button
              class="btn-cancel"
              @click="closeModal"
              :disabled="isSubmitting"
            >
              Cancel
            </button>
            <button
              class="btn-submit"
              @click="handleSubmit"
              :disabled="isSubmitting"
            >
              <span v-if="isSubmitting" class="spinner"></span>
              {{ isSubmitting ? "Saving..." : "Save Memory" }}
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

/* --- PAGE LAYOUT --- */
.timeline-page {
  background: transparent;
  min-height: 100vh;
  padding: 4rem 1rem;
  font-family: "Quicksand", sans-serif;
  position: relative;
  overflow-x: hidden;
}

/* --- BACKGROUND --- */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}
.floating-heart {
  position: absolute;
  color: rgba(255, 182, 193, 0.4);
  animation: float 6s ease-in-out infinite;
}
@keyframes float {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* --- HEADER --- */
.timeline-header {
  text-align: center;
  margin-bottom: 5rem;
  position: relative;
  z-index: 1;
}
.title {
  font-family: "Dancing Script", cursive;
  font-size: 3.5rem;
  color: #ff4757;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(255, 71, 87, 0.1);
}
.subtitle {
  color: #8899a6;
  font-size: 1rem;
  margin-top: 0.5rem;
}

/* Add Button */
.add-event-btn {
  margin-top: 1.5rem;
  background: #ff4757;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 50px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.3);
  transition: transform 0.2s;
  font-family: "Quicksand", sans-serif;
}
.add-event-btn:hover {
  transform: translateY(-2px);
}

/* --- TIMELINE CONTAINER --- */
.timeline-container {
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
  padding-bottom: 3rem;
  z-index: 1;
}
.center-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
  transform: translateX(-50%);
  z-index: -1;
}

/* --- ITEMS --- */
.timeline-item {
  display: flex;
  justify-content: flex-end;
  padding-bottom: 4rem;
  position: relative;
  width: 50%;
  clear: both;
}
.timeline-item.left {
  float: left;
  text-align: left;
  justify-content: flex-end;
  padding-right: 40px;
  box-sizing: border-box;
}
.timeline-item.right {
  float: right;
  text-align: left;
  justify-content: flex-start;
  padding-left: 40px;
  margin-top: 4rem;
  box-sizing: border-box;
}
.timeline-container::after {
  content: "";
  display: block;
  clear: both;
}

/* --- MARKER --- */
.timeline-marker {
  position: absolute;
  top: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: white;
  border: 2px solid #ffdde1;
  box-shadow: 0 0 0 4px #fff0f3;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  font-size: 1.2rem;
}
.timeline-item.left .timeline-marker {
  right: -20px;
}
.timeline-item.right .timeline-marker {
  left: -20px;
}

/* --- CARD --- */
.timeline-content {
  background: white;
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(255, 71, 87, 0.08);
  width: 100%;
  max-width: 400px;
  transition: transform 0.3s ease;
  position: relative;
}
.timeline-content:hover {
  transform: translateY(-5px);
}

/* Card Actions */
.card-actions {
  position: absolute;
  top: 1rem;
  right: 1rem;
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  gap: 5px;
  z-index: 20;
}
.timeline-content:hover .card-actions {
  opacity: 1;
}
.action-btn {
  background: #f1f5f9;
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  transition: all 0.2s;
}
.action-btn:hover {
  background: #ff4757;
  color: white;
}
.action-btn.delete:hover {
  background: #ef4444;
}

.date-tag {
  color: #ff4757;
  font-weight: 700;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  display: block;
  margin-bottom: 0.5rem;
}
.card-title {
  font-size: 1.5rem;
  color: #2d3436;
  margin: 0 0 1rem 0;
  font-weight: 700;
}
.image-wrapper {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 1rem;
}
.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: scale-down;
  transition: transform 0.5s ease;
}
.timeline-content:hover .image-wrapper img {
  transform: scale(1.05);
}
.description {
  color: #636e72;
  line-height: 1.6;
  font-size: 0.95rem;
  margin: 0;
}

/* --- FOOTER --- */
.timeline-footer {
  text-align: center;
  clear: both;
  padding-top: 2rem;
  position: relative;
  z-index: 2;
}
.footer-badge {
  display: inline-block;
  background: white;
  color: #ff4757;
  padding: 10px 24px;
  border-radius: 50px;
  font-size: 0.9rem;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.15);
  border: 1px solid #ffdde1;
}

/* --- MODAL STYLES --- */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-card {
  background: white;
  width: 100%;
  max-width: 500px;
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.2);
  margin: 1rem;
  max-height: 90vh;
  overflow-y: auto;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}
.close-icon {
  background: none;
  border: none;
  font-size: 2rem;
  color: #94a3b8;
  cursor: pointer;
}

/* Form Elements */
.upload-area {
  border: 2px dashed #cbd5e1;
  border-radius: 16px;
  padding: 1rem;
  text-align: center;
  margin-bottom: 1.5rem;
  background: #f8fafc;
  min-height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.upload-area:hover {
  border-color: #ff4757;
  background: #fff0f3;
}
.upload-area.has-image {
  padding: 0;
  border: none;
  background: transparent;
}

.preview-container {
  width: 100%;
  position: relative;
}
.preview-container img {
  width: 100%;
  height: 200px;
  object-fit: contain;
  border-radius: 8px;
}
.change-photo-btn {
  display: inline-block;
  margin-top: 5px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  cursor: pointer;
}

.form-group {
  margin-bottom: 1rem;
}
.form-group.row {
  display: flex;
  gap: 1rem;
}
.col {
  flex: 1;
}
.col-small {
  flex: 0 0 60px;
}
.form-group label {
  display: block;
  font-weight: 600;
  color: #475569;
  margin-bottom: 0.5rem;
}
.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #cbd5e1;
  font-family: "Quicksand", sans-serif;
  font-size: 1rem;
  box-sizing: border-box;
}
.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #ff4757;
}
.center-text {
  text-align: center;
}
.error-msg {
  color: #ef4444;
  font-size: 0.9rem;
  margin-top: 0.5rem;
  text-align: center;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}
.btn-cancel,
.btn-submit {
  flex: 1;
  padding: 12px;
  border-radius: 12px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  font-family: "Quicksand", sans-serif;
}
.btn-cancel {
  background: #f1f5f9;
  color: #64748b;
}
.btn-submit {
  background: #ff4757;
  color: white;
  display: flex;
  justify-content: center;
  gap: 8px;
  align-items: center;
}
.btn-submit:disabled {
  background: #fda4af;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid white;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
.spinner.large {
  width: 40px;
  height: 40px;
  border-width: 4px;
  border-color: #ff4757;
  border-top-color: transparent;
  margin: 0 auto 1rem;
}
.loading-state {
  text-align: center;
  padding: 2rem;
  color: #94a3b8;
  width: 100%;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Toast */
.toast-notification {
  position: fixed;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  background-color: #333;
  color: white;
  padding: 12px 24px;
  border-radius: 50px;
  font-weight: 600;
  z-index: 2000;
}
.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.4s;
}
.toast-slide-enter-from,
.toast-slide-leave-to {
  opacity: 0;
  transform: translate(-50%, 100%);
}
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

/* --- RESPONSIVE --- */
@media (max-width: 768px) {
  .center-line {
    left: 30px;
  }
  .timeline-item {
    width: 100%;
    padding-left: 70px;
    padding-right: 0;
  }
  .timeline-item.left,
  .timeline-item.right {
    float: none;
    margin-top: 0;
    margin-bottom: 2rem;
  }
  .timeline-item.left .timeline-marker,
  .timeline-item.right .timeline-marker {
    left: 10px;
    right: auto;
  }
  .title {
    font-size: 2.5rem;
  }
  .card-actions {
    opacity: 1;
  } /* Always show buttons on mobile */
}
</style>
