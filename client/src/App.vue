<script setup>
import NavBar from './components/Navbar.vue';
import GlobalBackground from './components/GlobalBackground.vue';
</script>

<template>
  <div class="app-layout">
    <GlobalBackground />
    
    <main class="main-content">
      <!-- Router view loads Home, Gallery, etc. here -->
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <NavBar />
  </div>
</template>

<style>
/* Global Resets */
body {
  margin: 0;
  padding: 0;
  font-family: 'Quicksand', sans-serif; /* Matching the Home font */
  -webkit-font-smoothing: antialiased;
}

/* Page Transition Animation */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 
  Layout Logic to handle the Navbar spacing 
  (Replaces the logic I gave you in step 1)
*/
.app-layout {
  position: relative;
  min-height: 100vh;
}

/* Mobile: Add padding to bottom so content isn't hidden behind bottom bar */
.main-content {
  padding-bottom: 90px; 
}

/* Desktop: Add padding to left so content isn't hidden behind sidebar */
@media (min-width: 768px) {
  .main-content {
    padding-bottom: 0;
    padding-left: 88px;
  }
}
</style>