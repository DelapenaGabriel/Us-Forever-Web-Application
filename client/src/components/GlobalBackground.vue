<template>
  <div class="global-bg">
    <!-- The Gradient Background -->
    <div class="bg-gradient"></div>

    <!-- Floating Hearts -->
    <div class="hearts-container" aria-hidden="true">
      <div class="heart h1">♥</div>
      <div class="heart h2">♥</div>
      <div class="heart h3">♥</div>
      <div class="heart h4">♥</div>
      <div class="heart h5">♥</div>
      <div class="heart h6">♥</div>
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
  z-index: -1; /* Behind everything */
  overflow: hidden;
  pointer-events: none; /* Let clicks pass through */
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #FFF0F3; 
  background-image: radial-gradient(circle at center, #fff5f7 0%, #ffeef2 100%);
}

.heart {
  position: absolute;
  color: #ffdde5; /* Subtle pink */
  font-size: 2rem;
  opacity: 0;
  animation: floatUp linear infinite;
}

/* Randomized positioning and timing */
.h1 { left: 10%; animation-duration: 15s; animation-delay: 0s; font-size: 1.5rem; }
.h2 { left: 85%; animation-duration: 18s; animation-delay: 2s; font-size: 2.5rem; }
.h3 { left: 15%; animation-duration: 12s; animation-delay: 5s; font-size: 1.2rem; }
.h4 { left: 70%; animation-duration: 16s; animation-delay: 1s; font-size: 1.8rem; }
.h5 { left: 90%; animation-duration: 20s; animation-delay: 8s; font-size: 3rem; }
.h6 { left: 40%; animation-duration: 14s; animation-delay: 4s; font-size: 2rem; }

@keyframes floatUp {
  0% {
    transform: translateY(110vh) rotate(0deg);
    opacity: 0;
  }
  20% {
    opacity: 0.6;
  }
  80% {
    opacity: 0.6;
  }
  100% {
    transform: translateY(-10vh) rotate(360deg);
    opacity: 0;
  }
}
</style>