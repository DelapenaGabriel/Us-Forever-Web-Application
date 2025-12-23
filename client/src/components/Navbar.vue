<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from "vue";
import { useRoute } from "vue-router";

// --- NAVIGATION LOGIC ---
const route = useRoute();
const navItems = [
  {
    name: "Home",
    path: "/",
    d: "M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12z",
  },
  {
    name: "Gallery",
    path: "/gallery",
    d: "M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z",
  },
  {
    name: "Timeline",
    path: "/timeline",
    d: "M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 012.25-2.25h13.5A2.25 2.25 0 0121 7.5v11.25m-18 0A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75m-18 0v-7.5A2.25 2.25 0 015.25 9h13.5A2.25 2.25 0 0121 11.25v7.5m-9-6h.008v.008H12v-.008zM12 15h.008v.008H12V15zm0 2.25h.008v.008H12v-.008zM9.75 15h.008v.008H9.75V15zm0 2.25h.008v.008H9.75v-.008zM7.5 15h.008v.008H7.5V15zm0 2.25h.008v.008H7.5v-.008zM14.25 15h.008v.008H14.25V15zm0 2.25h.008v.008H14.25v-.008zM16.5 15h.008v.008H16.5V15zm0 2.25h.008v.008H16.5v-.008z",
  },
  {
    name: "Notes",
    path: "/notes",
    d: "M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10",
  },
];

// --- MUSIC PLAYER LOGIC ---
const audioElement = ref(null);
const isWidgetOpen = ref(false); // Controls minimized vs expanded
const isPlaying = ref(false);
const currentTime = ref(0);
const duration = ref(0);

// Playlist Data
const tracks = [
  {
    title: "Crazier",
    artist: "Taylor Swift",
    cover:
      "https://m.media-amazon.com/images/M/MV5BZmExMDBiYjYtYjMyNi00N2Y2LThjNWYtOWVhYWE0Y2I1MzkxXkEyXkFqcGc@._V1_.jpg",
    src: "./music/crazier.mp3",
  },
  {
    title: "Obsession",
    artist: "Frankie J.",
    // REPLACE with your actual image URL
    cover:
      "https://i.discogs.com/Wl2meVIab2RRUi_6DDVW88zQW9fwuzrLCyDqJdMTcQE/rs:fit/g:sm/q:40/h:300/w:300/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTEzNjky/NjQtMTI4MTc3MTc5/NS5qcGVn.jpeg",
    // REPLACE with your actual mp3 file path (e.g., '/music/song.mp3')
    src: "./music/obsession.mp3",
  },
  {
    title: "What A God",
    artist: "SEU Worship",
    cover:
      "https://www.worshiptogether.com/media/qkcjxenw/what-a-god-seu-worship.jpeg?anchor=center&mode=crop&width=400&rnd=133929344350800000",
    src: "./music/whatagod.mp3",
  }
];

const currentTrackIndex = ref(0);
const currentTrack = computed(() => tracks[currentTrackIndex.value]);
const progressPercent = computed(() => {
  if (!duration.value) return 0;
  return (currentTime.value / duration.value) * 100;
});

// Audio Controls
const togglePlay = () => {
  if (!audioElement.value) return;
  if (isPlaying.value) {
    audioElement.value.pause();
  } else {
    audioElement.value
      .play()
      .catch((e) => console.error("Playback failed:", e));
  }
  isPlaying.value = !isPlaying.value;
};

const nextTrack = () => {
  currentTrackIndex.value = (currentTrackIndex.value + 1) % tracks.length;
  resetAndPlay();
};

const prevTrack = () => {
  currentTrackIndex.value =
    (currentTrackIndex.value - 1 + tracks.length) % tracks.length;
  resetAndPlay();
};

const resetAndPlay = () => {
  isPlaying.value = false;
  setTimeout(() => {
    if (audioElement.value) {
      audioElement.value.load();
      togglePlay();
    }
  }, 100);
};

// Update Progress Bar
const updateTime = () => {
  if (audioElement.value) {
    currentTime.value = audioElement.value.currentTime;
    duration.value = audioElement.value.duration;
  }
};

// Format seconds to mm:ss
const formatTime = (seconds) => {
  if (!seconds) return "0:00";
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs < 10 ? "0" : ""}${secs}`;
};

// Toggle Widget Size
const toggleWidget = () => {
  isWidgetOpen.value = !isWidgetOpen.value;
};

// Lifecycle Hooks
onMounted(() => {
  if (audioElement.value) {
    audioElement.value.addEventListener("timeupdate", updateTime);
    audioElement.value.addEventListener("ended", nextTrack);
  }
});

onUnmounted(() => {
  if (audioElement.value) {
    audioElement.value.removeEventListener("timeupdate", updateTime);
    audioElement.value.removeEventListener("ended", nextTrack);
  }
});
</script>

<template>
  <nav class="navbar" role="navigation" aria-label="Main Navigation">
    <!-- Desktop Logo -->
    <div class="brand">
      <router-link to="/">Us</router-link>
    </div>

    <!-- Navigation Links -->
    <ul class="nav-list">
      <li v-for="item in navItems" :key="item.name" class="nav-item">
        <router-link :to="item.path" class="nav-link" active-class="active">
          <div class="icon-wrapper">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.8"
              stroke="currentColor"
              class="icon"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                :d="item.d"
              />
            </svg>
          </div>
          <span class="label">{{ item.name }}</span>
          <div class="active-indicator"></div>
        </router-link>
      </li>
    </ul>

    <!-- --- MUSIC PLAYER COMPONENT --- -->
    <div class="music-widget-container" :class="{ 'is-open': isWidgetOpen }">
      <!-- Hidden Audio Element -->
      <audio ref="audioElement" :src="currentTrack.src"></audio>

      <!-- Minimized Floating Button (FAB) -->
      <button v-if="!isWidgetOpen" class="music-fab" @click="toggleWidget">
        <div class="note-icon" :class="{ 'animate-bounce': isPlaying }">🎵</div>
      </button>

      <!-- Expanded Player Card -->
      <div v-else class="music-card">
        <button class="close-btn" @click="toggleWidget">&times;</button>

        <div class="track-info">
          <div class="album-art" :class="{ spinning: isPlaying }">
            <img :src="currentTrack.cover" alt="Album Art" />
            <div class="center-hole"></div>
          </div>

          <div class="text-details">
            <h4 class="song-title">{{ currentTrack.title }}</h4>
            <p class="artist-name">{{ currentTrack.artist }}</p>
          </div>
        </div>

        <!-- Progress Bar -->
        <div class="progress-container">
          <div class="progress-bar">
            <div
              class="progress-fill"
              :style="{ width: progressPercent + '%' }"
            ></div>
          </div>
          <div class="time-stamp">
            <span>{{ formatTime(currentTime) }}</span>
            <span>{{ formatTime(duration) }}</span>
          </div>
        </div>

        <!-- Controls -->
        <div class="controls">
          <button class="ctrl-btn" @click="prevTrack">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="currentColor"
              viewBox="0 0 24 24"
            >
              <path d="M11 19V5l-8 7 8 7zm11 0V5l-8 7 8 7z" />
            </svg>
          </button>

          <button class="play-btn" @click="togglePlay">
            <svg
              v-if="!isPlaying"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="w-6 h-6"
            >
              <path
                fill-rule="evenodd"
                d="M4.5 5.653c0-1.426 1.529-2.33 2.779-1.643l11.54 6.348c1.295.712 1.295 2.573 0 3.285L7.28 19.991c-1.25.687-2.779-.217-2.779-1.643V5.653z"
                clip-rule="evenodd"
              />
            </svg>
            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="w-6 h-6"
            >
              <path
                fill-rule="evenodd"
                d="M6.75 5.25a.75.75 0 01.75-.75H9a.75.75 0 01.75.75v13.5a.75.75 0 01-.75.75H7.5a.75.75 0 01-.75-.75V5.25zm7.5 0A.75.75 0 0115 4.5h1.5a.75.75 0 01.75.75v13.5a.75.75 0 01-.75.75H15a.75.75 0 01-.75-.75V5.25z"
                clip-rule="evenodd"
              />
            </svg>
          </button>

          <button class="ctrl-btn" @click="nextTrack">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="currentColor"
              viewBox="0 0 24 24"
            >
              <path d="M4 19V5l8 7-8 7zm11 0V5l8 7-8 7z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<style scoped>
/* --- EXISTING VARIABLES --- */
:root {
  --nav-bg: #fefdfb;
  --nav-text-idle: #94a3b8;
  --nav-text-active: #ff4757;
  --nav-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
  --nav-border: #f1f5f9;
  --font-cursive: "Brush Script MT", "cursive", sans-serif;
}

/* --- EXISTING NAV CSS (Preserved) --- */
.navbar {
  background-color: #fefdfb;
  z-index: 1000;
  box-sizing: border-box;
}
.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
}
.nav-link {
  text-decoration: none;
  color: #94a3b8;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: color 0.3s ease;
}
.icon {
  width: 26px;
  height: 26px;
  transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.label {
  font-size: 0.75rem;
  font-weight: 500;
  margin-top: 4px;
  opacity: 0.9;
}
.nav-link.active {
  color: #ff4757;
}
.nav-link.active .icon {
  transform: scale(1.1);
  stroke-width: 2.2;
}

/* Mobile Base */
.navbar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 80px;
  border-top: 1px solid #f1f5f9;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
  padding-bottom: env(safe-area-inset-bottom);
}
.brand {
  display: none;
}
.nav-list {
  width: 100%;
  height: 100%;
  justify-content: space-around;
  align-items: center;
}
.nav-item {
  display: flex;
  justify-content: center;
}
.active-indicator {
  display: none;
}
@media (hover: hover) {
  .nav-link:hover {
    color: #ff4757;
  }
}

/* Desktop Base */
@media (min-width: 768px) {
  .navbar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    width: 88px;
    border-top: none;
    border-right: 1px solid #f1f5f9;
    display: flex;
    flex-direction: column;
    padding: 2rem 0;
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.02);
  }
  .brand {
    display: flex;
    justify-content: center;
    margin-bottom: 3rem;
  }
  .brand a {
    font-family: "Brush Script MT", "cursive", sans-serif;
    color: #ff4757;
    font-size: 2.5rem;
    text-decoration: none;
    font-weight: bold;
    font-style: italic;
  }
  .nav-list {
    flex-direction: column;
    justify-content: flex-start;
    gap: 1.5rem;
  }
  .nav-link {
    width: 100%;
    height: 60px;
    flex-direction: row;
  }
  .icon {
    width: 28px;
    height: 28px;
  }
  .label {
    position: absolute;
    left: 70px;
    background: #fbf1e8;
    color: #ff4757;
    font-weight: 700;
    padding: 5px 10px;
    border-radius: 6px;
    font-size: 0.85rem;
    opacity: 0;
    visibility: hidden;
    transform: translateX(-10px);
    transition: all 0.2s ease;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    white-space: nowrap;
    z-index: 1001;
  }
  .label::before {
    content: "";
    position: absolute;
    left: -4px;
    top: 50%;
    transform: translateY(-50%);
    border-width: 5px 5px 5px 0;
    border-style: solid;
    border-color: transparent #ff4757 transparent transparent;
  }
  .nav-link:hover .label {
    opacity: 1;
    visibility: visible;
    transform: translateX(0);
  }
  .nav-link.active .active-indicator {
    display: block;
    position: absolute;
    width: 50px;
    height: 50px;
    background-color: rgba(255, 71, 87, 0.08);
    border-radius: 14px;
    z-index: -1;
    transition: all 0.3s ease;
  }
}

/* 
  ---------------------------------
  --- NEW MUSIC WIDGET STYLES --- 
  ---------------------------------
*/

.music-widget-container {
  position: fixed;
  right: 20px;
  /* On mobile, sit above the bottom nav (80px + padding) */
  bottom: 100px;
  z-index: 2000;
  display: flex;
  align-items: flex-end;
  flex-direction: column;
}

/* FAB (Floating Action Button) */
.music-fab {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: white;
  border: none;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.25);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s, box-shadow 0.2s;
  color: #ff4757;
  font-size: 1.5rem;
}

.music-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(255, 71, 87, 0.4);
}

.note-icon.animate-bounce {
  animation: bounce 1s infinite;
}

/* Expanded Card */
.music-card {
  background: white;
  width: 280px;
  padding: 1.5rem;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  position: relative;
  animation: slideUp 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 15px;
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 1.5rem;
  cursor: pointer;
}

/* Track Info */
.track-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.album-art {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  flex-shrink: 0;
}

.album-art img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.center-hole {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: white;
  border-radius: 50%;
}

.spinning {
  animation: spin 8s linear infinite;
}

.text-details {
  flex: 1;
  overflow: hidden;
}

.song-title {
  margin: 0;
  font-size: 1rem;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.artist-name {
  margin: 4px 0 0 0;
  font-size: 0.8rem;
  color: #94a3b8;
}

/* Progress Bar */
.progress-container {
  width: 100%;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #f1f5f9;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 6px;
}

.progress-fill {
  height: 100%;
  background: #ff4757;
  border-radius: 10px;
  transition: width 0.1s linear;
}

.time-stamp {
  display: flex;
  justify-content: space-between;
  font-size: 0.7rem;
  color: #cbd5e1;
  font-weight: 600;
}

/* Controls */
.controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
}

.ctrl-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #94a3b8;
  width: 24px;
  transition: color 0.2s;
}

.ctrl-btn:hover {
  color: #ff4757;
}

.play-btn {
  background: #ff4757;
  color: white;
  border: none;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(255, 71, 87, 0.4);
  transition: transform 0.2s, box-shadow 0.2s;
}

.play-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(255, 71, 87, 0.5);
}

/* Desktop Positioning */
@media (min-width: 768px) {
  .music-widget-container {
    bottom: 30px;
    right: 30px;
  }
}

/* Animations */
@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}
@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
