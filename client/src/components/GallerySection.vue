<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
// Ensure this path matches where you saved your service file
import PhotoService from '@/service/PhotoService'; 

// --- CONFIGURATION ---
// CHANGED: 'image' -> 'auto' to support both images and videos
const CLOUDINARY_URL = 'https://api.cloudinary.com/v1_1/dilpitidj/auto/upload';
const UPLOAD_PRESET = 'usforever'; 

// --- STATE ---
const activeFilter = ref('All');
const isModalOpen = ref(false);
const isUploading = ref(false);
const isLoading = ref(true); 
const uploadError = ref('');

// --- NEW: LIGHTBOX STATE ---
const selectedImage = ref(null);

// Toast State
const toast = ref({
  visible: false,
  message: ''
});

// Form Data
const newImageFile = ref(null);
const newImagePreview = ref(null);
const newFileType = ref('image'); // 'image' or 'video'
const selectedCategory = ref('Trip');

// --- DATA ---
const filters = ['All', 'Trip', 'Date', 'Silly', 'Milestone'];
const categories = filters.filter(f => f !== 'All');
const allImages = ref([]);

// --- LIFECYCLE ---
onMounted(async () => {
  await fetchPhotos();
  window.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown);
});

// Fetch photos
const fetchPhotos = async () => {
  isLoading.value = true;
  try {
    const response = await PhotoService.getAllPhotos();
    allImages.value = response.data;
  } catch (error) {
    console.error("Failed to fetch photos:", error);
    uploadError.value = "Could not load gallery. Please try refreshing.";
  } finally {
    isLoading.value = false;
  }
};

// --- COMPUTED LOGIC ---
const filteredImages = computed(() => {
  if (activeFilter.value === 'All') {
    return allImages.value;
  }
  return allImages.value.filter(img => img.category === activeFilter.value);
});

const setFilter = (filter) => {
  activeFilter.value = filter;
};

// --- HELPER: CHECK IF URL IS VIDEO ---
const isVideo = (url) => {
  if (!url) return false;
  // Check for common video extensions or Cloudinary video resource types
  return url.match(/\.(mp4|webm|ogg|mov|avi|mkv)/i) !== null;
};

// --- LIGHTBOX LOGIC ---
const openLightbox = (image) => {
  selectedImage.value = image;
  document.body.style.overflow = 'hidden'; 
};

const closeLightbox = () => {
  selectedImage.value = null;
  document.body.style.overflow = ''; 
};

const handleKeydown = (e) => {
  if (e.key === 'Escape' && selectedImage.value) {
    closeLightbox();
  }
};

// --- FILE HANDLING ---
const handleFileSelect = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // Validate Image OR Video
  const isImg = file.type.match('image.*');
  const isVid = file.type.match('video.*');

  if (!isImg && !isVid) {
    uploadError.value = 'Please select a valid image or video file.';
    return;
  }

  newImageFile.value = file;
  newFileType.value = isVid ? 'video' : 'image';
  newImagePreview.value = URL.createObjectURL(file);
  uploadError.value = '';
};

// --- DELETE LOGIC ---
const handleDelete = async (id) => {
  if (!confirm("Are you sure you want to delete this memory? This cannot be undone.")) {
    return;
  }

  try {
    await PhotoService.deletePhoto(id);
    allImages.value = allImages.value.filter(img => img.id !== id);
    showToast("Memory deleted successfully 🗑️");
  } catch (error) {
    console.error("Delete failed:", error);
    alert("Failed to delete item. Please try again.");
  }
};

// --- TOAST LOGIC ---
const showToast = (msg) => {
  toast.value.message = msg;
  toast.value.visible = true;
  setTimeout(() => {
    toast.value.visible = false;
  }, 3000);
};

// --- UPLOAD LOGIC ---
const handleUpload = async () => {
  if (!newImageFile.value) {
    uploadError.value = 'Please select a file first.';
    return;
  }

  isUploading.value = true;
  uploadError.value = '';

  const formData = new FormData();
  formData.append('file', newImageFile.value);
  formData.append('upload_preset', UPLOAD_PRESET);
  formData.append('folder', 'usforever'); 
  // Cloudinary resource_type 'auto' usually handles this, but forcing 'video' if needed is safer
  // However, relying on the endpoint /auto/upload is usually sufficient.

  try {
    const cloudinaryResponse = await fetch(CLOUDINARY_URL, {
      method: 'POST',
      body: formData,
    });

    if (!cloudinaryResponse.ok) throw new Error('Failed to upload to cloud storage');
    
    const cloudinaryData = await cloudinaryResponse.json();

    // Use exact same payload structure
    const newPhotoPayload = {
      imgUrl: cloudinaryData.secure_url, 
      category: selectedCategory.value
    };

    const backendResponse = await PhotoService.createPhoto(newPhotoPayload);

    allImages.value.unshift(backendResponse.data);
    closeModal();
    
    if (activeFilter.value !== 'All') {
      activeFilter.value = 'All';
    }

    showToast("New memory saved! ✨");

  } catch (err) {
    console.error(err);
    uploadError.value = 'Something went wrong. Please check your connection and try again.';
  } finally {
    isUploading.value = false;
  }
};

const closeModal = () => {
  isModalOpen.value = false;
  setTimeout(() => {
    newImageFile.value = null;
    newImagePreview.value = null;
    newFileType.value = 'image';
    uploadError.value = '';
    selectedCategory.value = 'Trip';
  }, 300); 
};
</script>

<template>
  <div class="gallery-container">
    
    <!-- Header Section -->
    <header class="gallery-header">
      <div class="header-content">
        <h1 class="page-title">Our Gallery</h1>
        <button @click="isModalOpen = true" class="add-btn">
          <span>+</span> Add Memory
        </button>
      </div>
      
      <div class="filters">
        <button 
          v-for="filter in filters" 
          :key="filter"
          class="filter-btn"
          :class="{ active: activeFilter === filter }"
          @click="setFilter(filter)"
        >
          {{ filter }}
        </button>
      </div>
    </header>

    <div v-if="isLoading" class="loading-state">
      <div class="spinner large"></div>
      <p>Loading memories...</p>
    </div>

    <!-- Image Grid -->
    <TransitionGroup v-else name="image-fade" tag="div" class="image-grid">
      <div 
        v-for="image in filteredImages" 
        :key="image.id" 
        class="image-card"
        @click="openLightbox(image)"
      >
        <div class="card-inner">
          <!-- Video vs Image Logic -->
          <video 
            v-if="isVideo(image.imgUrl)" 
            :src="image.imgUrl" 
            class="media-content" 
            muted 
            loop 
            playsinline
            onmouseover="this.play()" 
            onmouseout="this.pause()"
          ></video>
          
          <img 
            v-else 
            :src="image.imgUrl" 
            loading="lazy" 
            :alt="image.category" 
            class="media-content"
          />
          
          <!-- Video Indicator Icon -->
          <div v-if="isVideo(image.imgUrl)" class="video-indicator">▶</div>

          <!-- Hover Overlay -->
          <div class="overlay">
            <div class="overlay-top">
              <div class="icon-heart">♥</div>
              <button 
                class="delete-btn" 
                @click.stop="handleDelete(image.id)" 
                title="Delete Memory"
              >
                🗑
              </button>
            </div>
            <span class="category-tag">{{ image.category }}</span>
          </div>
        </div>
      </div>
    </TransitionGroup>

    <div v-if="!isLoading && filteredImages.length === 0" class="empty-state">
      <p>No memories found here yet. Time to make some! 💕</p>
    </div>

    <!-- LIGHTBOX OVERLAY -->
    <Transition name="modal-fade">
      <div v-if="selectedImage" class="lightbox-backdrop" @click.self="closeLightbox">
        <div class="lightbox-content">
          
          <!-- Lightbox Video -->
          <video 
            v-if="isVideo(selectedImage.imgUrl)" 
            :src="selectedImage.imgUrl" 
            controls 
            autoplay 
            class="lightbox-media"
          ></video>

          <!-- Lightbox Image -->
          <img 
            v-else 
            :src="selectedImage.imgUrl" 
            alt="Full size memory" 
            class="lightbox-media"
          />

          <button class="lightbox-close" @click="closeLightbox">&times;</button>
          <div class="lightbox-caption">{{ selectedImage.category }}</div>
        </div>
      </div>
    </Transition>

    <!-- UPLOAD MODAL -->
    <Transition name="modal-fade">
      <div v-if="isModalOpen" class="modal-backdrop" @click.self="closeModal">
        <div class="modal-card">
          <div class="modal-header">
            <h2>Add a New Memory</h2>
            <button class="close-icon" @click="closeModal">&times;</button>
          </div>

          <div class="modal-body">
            <div class="upload-area" :class="{ 'has-image': newImagePreview }">
              <!-- Accept both image and video -->
              <input type="file" id="fileInput" @change="handleFileSelect" accept="image/*,video/*" hidden />
              
              <label for="fileInput" class="upload-label" v-if="!newImagePreview">
                <div class="upload-icon">☁️</div>
                <p>Click to upload photo or video</p>
              </label>

              <div v-else class="preview-container">
                <!-- Preview Logic -->
                <video 
                  v-if="newFileType === 'video'" 
                  :src="newImagePreview" 
                  controls 
                  class="preview-media"
                ></video>
                <img 
                  v-else 
                  :src="newImagePreview" 
                  alt="Preview" 
                  class="preview-media"
                />
                
                <button class="remove-btn" @click="newImagePreview = null">Change File</button>
              </div>
            </div>

            <div class="form-group">
              <label>Select Category</label>
              <select v-model="selectedCategory">
                <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
              </select>
            </div>
            <p v-if="uploadError" class="error-msg">{{ uploadError }}</p>
          </div>

          <div class="modal-footer">
            <button class="btn-cancel" @click="closeModal" :disabled="isUploading">Cancel</button>
            <button class="btn-submit" @click="handleUpload" :disabled="isUploading || !newImageFile">
              <span v-if="isUploading" class="spinner"></span>
              {{ isUploading ? 'Uploading...' : 'Save Memory' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- TOAST NOTIFICATION -->
    <Transition name="toast-slide">
      <div v-if="toast.visible" class="toast-notification">
        {{ toast.message }}
      </div>
    </Transition>

  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Dancing+Script:wght@700&family=Quicksand:wght@400;500;600;700&display=swap');

/* --- GLOBAL LAYOUT --- */
.gallery-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  font-family: 'Quicksand', sans-serif;
  padding-bottom: 5rem; 
}

.gallery-header { margin-bottom: 3rem; }
.header-content { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem; }
.page-title { font-family: 'Dancing Script', cursive; font-size: 3rem; color: #ff4757; margin: 0; text-shadow: 2px 2px 4px rgba(0,0,0,0.05); }
.add-btn { background: #ff4757; color: white; border: none; padding: 10px 24px; border-radius: 50px; font-family: 'Quicksand', sans-serif; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 8px; box-shadow: 0 4px 15px rgba(255, 71, 87, 0.4); transition: transform 0.2s, box-shadow 0.2s; }
.add-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255, 71, 87, 0.6); }
.filters { display: flex; flex-wrap: wrap; gap: 0.8rem; }
.filter-btn { background-color: #ffffff; border: 1px solid #e2e8f0; padding: 8px 24px; border-radius: 50px; font-family: 'Quicksand', sans-serif; font-weight: 600; font-size: 0.9rem; color: #64748b; cursor: pointer; transition: all 0.3s ease; }
.filter-btn:hover { color: #ff4757; border-color: #ff4757; background-color: #fff0f3; }
.filter-btn.active { background-color: #ff4757; color: white; border-color: #ff4757; box-shadow: 0 4px 12px rgba(255, 71, 87, 0.3); }

/* --- GRID LAYOUT --- */
.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  width: 100%;
}

.image-card {
  position: relative;
  aspect-ratio: 4/5;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  background-color: #f1f5f9;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.image-card:hover { transform: translateY(-5px); box-shadow: 0 12px 25px rgba(0,0,0,0.1); }

/* Shared styles for IMG and VIDEO in grid */
.card-inner { width: 100%; height: 100%; position: relative; }
.media-content { width: 100%; height: 100%; object-fit: cover; display: block; }

.video-indicator {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 40px; height: 40px;
  background: rgba(0,0,0,0.4);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  pointer-events: none;
  backdrop-filter: blur(2px);
}

.overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(to top, rgba(0,0,0,0.6) 0%, rgba(0,0,0,0) 50%, rgba(0,0,0,0.2) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between; 
  padding: 1rem;
  box-sizing: border-box;
}
.image-card:hover .overlay { opacity: 1; }
.image-card:hover .video-indicator { opacity: 0; } /* Hide play icon when hovering (video plays) */

.overlay-top { display: flex; justify-content: space-between; align-items: flex-start; width: 100%; }
.icon-heart { color: white; font-size: 1.2rem; text-shadow: 0 2px 4px rgba(0,0,0,0.3); }

.delete-btn {
  background: white; color: #ef4444; border: none; width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; cursor: pointer;
  font-size: 1rem; transition: all 0.2s; box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}
.delete-btn:hover { background: #ef4444; color: white; transform: scale(1.1); }
.category-tag { color: white; font-size: 0.85rem; font-weight: 600; background: rgba(255, 255, 255, 0.25); backdrop-filter: blur(4px); padding: 4px 12px; border-radius: 20px; align-self: flex-start; }

/* --- LIGHTBOX STYLES --- */
.lightbox-backdrop {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(0, 0, 0, 0.9); backdrop-filter: blur(8px);
  display: flex; justify-content: center; align-items: center; z-index: 3000;
  padding: 2rem; box-sizing: border-box;
}
.lightbox-content {
  position: relative; max-width: 90vw; max-height: 90vh;
  display: flex; flex-direction: column; align-items: center;
}
.lightbox-media {
  max-width: 100%; max-height: 85vh;
  object-fit: contain; border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.5);
  background: black;
}
.lightbox-close {
  position: absolute; top: -40px; right: -20px;
  background: none; border: none; color: white;
  font-size: 2.5rem; cursor: pointer; opacity: 0.8; transition: opacity 0.2s;
}
.lightbox-close:hover { opacity: 1; }
.lightbox-caption { margin-top: 1rem; color: white; font-size: 1.2rem; font-weight: 500; letter-spacing: 1px; }

/* --- MODAL & HELPERS --- */
.toast-notification {
  position: fixed; bottom: 2rem; left: 50%; transform: translateX(-50%);
  background-color: #333; color: white; padding: 12px 24px;
  border-radius: 50px; font-family: 'Quicksand', sans-serif; font-weight: 600;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2); z-index: 2000;
}
.loading-state, .empty-state { text-align: center; padding: 4rem; color: #94a3b8; grid-column: 1 / -1; }
.modal-backdrop { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-card { background: white; width: 100%; max-width: 500px; border-radius: 24px; padding: 2rem; box-shadow: 0 20px 50px rgba(0,0,0,0.2); margin: 1rem; max-height: 90vh; overflow-y: auto; }
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.close-icon { background: none; border: none; font-size: 2rem; color: #94a3b8; cursor: pointer; }
.upload-area { border: 2px dashed #cbd5e1; border-radius: 16px; padding: 2rem; text-align: center; margin-bottom: 1.5rem; background: #f8fafc; min-height: 200px; display: flex; flex-direction: column; justify-content: center; align-items: center; }
.upload-area:hover { border-color: #ff4757; background: #fff0f3; }
.upload-area.has-image { padding: 0; border: none; overflow: hidden; background: black; position: relative; }
.upload-label { cursor: pointer; width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.upload-icon { font-size: 3rem; margin-bottom: 0.5rem; opacity: 0.7; }
.preview-container { width: 100%; height: 100%; position: relative; display: flex; align-items: center; justify-content: center;}
.preview-media { width: 100%; height: 250px; object-fit: contain; }
.remove-btn { position: absolute; bottom: 1rem; left: 50%; transform: translateX(-50%); background: rgba(0,0,0,0.6); color: white; border: none; padding: 6px 16px; border-radius: 20px; cursor: pointer; z-index: 10; }
.form-group label { display: block; font-weight: 600; color: #475569; margin-bottom: 0.5rem; }
.form-group select { width: 100%; padding: 12px; border-radius: 12px; border: 1px solid #cbd5e1; font-family: 'Quicksand', sans-serif; font-size: 1rem; }
.error-msg { color: #ef4444; font-size: 0.9rem; margin-top: 1rem; text-align: center; }
.modal-footer { display: flex; gap: 1rem; margin-top: 2rem; }
.btn-cancel, .btn-submit { flex: 1; padding: 12px; border-radius: 12px; border: none; font-weight: 600; cursor: pointer; font-size: 1rem; }
.btn-cancel { background: #f1f5f9; color: #64748b; }
.btn-submit { background: #ff4757; color: white; display: flex; justify-content: center; align-items: center; gap: 8px; }
.btn-submit:disabled { background: #fda4af; cursor: not-allowed; }
.spinner { width: 16px; height: 16px; border: 2px solid white; border-top-color: transparent; border-radius: 50%; animation: spin 0.8s linear infinite; }
.spinner.large { width: 40px; height: 40px; border-width: 4px; border-color: #ff4757; border-top-color: transparent; }
@keyframes spin { to { transform: rotate(360deg); } }
.image-fade-enter-active, .image-fade-leave-active { transition: all 0.5s ease; }
.image-fade-enter-from, .image-fade-leave-to { opacity: 0; transform: scale(0.9); }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.toast-slide-enter-active, .toast-slide-leave-active { transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.toast-slide-enter-from, .toast-slide-leave-to { opacity: 0; transform: translate(-50%, 100%); }
</style>