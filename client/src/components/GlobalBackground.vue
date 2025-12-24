<script setup>
import { ref, onMounted } from 'vue';

const hearts = ref([]);
const heartCount = 35; // increased density

const colors = [
  '#ffb3c1', // Soft Pink
  '#ff8fa3', // Darker Pink
  '#fff0f3', // Very Light Pink
  '#ffc2d1', // Salmon
  '#ffe5ec', // Blush
  '#ffffff', // White
];

// Generate random attributes for each heart
const generateHearts = () => {
  const newHearts = [];
  for (let i = 0; i < heartCount; i++) {
    newHearts.push({
      id: i,
      left: Math.random() * 100 + '%',
      // Random animation duration between 15s and 35s (slow and calming)
      duration: 15 + Math.random() * 20 + 's',
      // Random delay so they don't all start at once
      delay: Math.random() * -30 + 's', // Negative delay makes them appear already mid-flight
      // Random size
      size: 10 + Math.random() * 30 + 'px',
      color: colors[Math.floor(Math.random() * colors.length)],
      opacity: 0.3 + Math.random() * 0.5,
      // Blur for depth-of-field effect
      blur: Math.random() > 0.7 ? '2px' : '0px' 
    });
  }
  hearts.value = newHearts;
};

onMounted(() => {
  generateHearts();
});
</script>

<template>
  <div class="global-bg">
    <!-- Dynamic Moving Gradient Orbs (Lava lamp effect) -->
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>

    <!-- Floating Hearts -->
    <div class="hearts-container">
      <div 
        v-for="heart in hearts" 
        :key="heart.id"
        class="heart-wrapper"
        :style="{
          left: heart.left,
          animationDuration: heart.duration,
          animationDelay: heart.delay,
          width: heart.size,
          height: heart.size,
          opacity: heart.opacity,
          filter: `blur(${heart.blur})`
        }"
      >
        <!-- SVG Heart for smoother edges -->
        <svg viewBox="0 0 32 29.6" class="heart-shape">
          <path 
            :fill="heart.color" 
            d="M23.6,0c-3.4,0-6.3,2.7-7.6,5.6C14.7,2.7,11.8,0,8.4,0C3.8,0,0,3.8,0,8.4c0,9.4,9.5,11.9,16,21.2
            c6.1-9.3,16-12.1,16-21.2C32,3.8,28.2,0,23.6,0z"
          />
        </svg>
      </div>
    </div>
  </div>
</template>

<style scoped>
.global-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: -1;
  overflow: hidden;
  pointer-events: none;
  background: linear-gradient(135deg, #fff0f3 0%, #ffe6ea 100%);
}

/* --- MOVING BACKGROUND ORBS --- */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: orbFloat 20s infinite ease-in-out alternate;
}

.orb-1 {
  width: 50vw;
  height: 50vw;
  background: #ffc2d1;
  top: -10%;
  left: -10%;
  animation-duration: 25s;
}

.orb-2 {
  width: 40vw;
  height: 40vw;
  background: #ffe5ec;
  bottom: -10%;
  right: -10%;
  animation-duration: 30s;
  animation-direction: alternate-reverse;
}

@keyframes orbFloat {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(10%, 20%) scale(1.1); }
}

/* --- HEARTS CONTAINER --- */
.hearts-container {
  width: 100%;
  height: 100%;
  position: absolute;
}

.heart-wrapper {
  position: absolute;
  bottom: -50px; /* Start below screen */
  /* Combine vertical float with horizontal sway */
  animation-name: floatUp, sway; 
  animation-timing-function: linear, ease-in-out;
  animation-iteration-count: infinite, infinite;
  animation-direction: normal, alternate;
  will-change: transform, opacity;
}

.heart-shape {
  width: 100%;
  height: 100%;
  display: block;
  /* Add a gentle pulse to the SVG itself */
  animation: pulse 3s infinite ease-in-out;
}

/* --- ANIMATIONS --- */

/* 1. Move from bottom to top, fading out at the very end */
@keyframes floatUp {
  0% {
    bottom: -10vh;
    opacity: 0;
    transform: scale(0.5);
  }
  10% {
    opacity: 1; /* Fade in quickly */
  }
  90% {
    opacity: 0.8;
  }
  100% {
    bottom: 110vh; /* Move past top */
    opacity: 0;
    transform: scale(1);
  }
}

/* 2. Sway left and right to look like floating paper/balloons */
@keyframes sway {
  0% { transform: translateX(-20px) rotate(-5deg); }
  100% { transform: translateX(20px) rotate(5deg); }
}

/* 3. Subtle heartbeat pulse */
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}
</style>