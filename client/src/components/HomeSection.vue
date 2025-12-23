<script setup>
// Home.vue
</script>

<template>
  <section class="home-container">
    <div class="content-wrapper">
      
      <!-- Hero Icon -->
      <div class="hero-icon-wrapper">
        <div class="heart-circle">
          <svg viewBox="0 0 24 24" fill="currentColor" class="main-heart-icon">
            <path d="M11.645 20.91l-.007-.003-.022-.012a15.247 15.247 0 01-.383-.218 25.18 25.18 0 01-4.244-3.17C4.688 15.36 2.25 12.174 2.25 8.25 2.25 5.322 4.714 3 7.688 3A5.5 5.5 0 0112 5.052 5.5 5.5 0 0116.313 3c2.973 0 5.437 2.322 5.437 5.25 0 3.925-2.438 7.111-4.739 9.256a25.175 25.175 0 01-4.244 3.17 15.247 15.247 0 01-.383.219l-.022.012-.007.004-.003.001a.752.752 0 01-.704 0l-.003-.001z" />
          </svg>
        </div>
      </div>

      <!-- Title -->
      <h1 class="title">
        Our Memories
        <span class="title-decor">
          <svg viewBox="0 0 24 24" fill="currentColor" class="decor-heart d1"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
          <svg viewBox="0 0 24 24" fill="currentColor" class="decor-heart d2"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
        </span>
      </h1>

      <p class="subtitle">
        A little digital place for our love story. Every photo, every note, every moment we cherish.
      </p>

      <div class="actions">
        <router-link to="/gallery" class="btn btn-white">View Gallery</router-link>
        <router-link to="/notes" class="btn btn-pink">Write a Note</router-link>
      </div>
    </div>

    <div class="footer-text">Est. 2016 • Us Forever</div>
  </section>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Dancing+Script:wght@700&family=Quicksand:wght@400;500;600&display=swap');

/* Simplified Container - Background is now handled by GlobalBackground.vue */
.home-container {
  min-height: 100vh; /* Takes up most of screen but lets nav fit */
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 2rem;
  box-sizing: border-box;
  background: transparent;
}

.content-wrapper {
  animation: fadeInUp 0.8s ease-out;
  max-width: 600px;
}

/* --- REUSED STYLES (Same as before) --- */
.hero-icon-wrapper { display: flex; justify-content: center; margin-bottom: 1.5rem; }
.heart-circle {
  width: 80px; height: 80px;
  background-color: rgba(255, 182, 193, 0.3);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 30px rgba(244, 63, 94, 0.1);
}
.main-heart-icon { width: 40px; height: 40px; color: #ff8fa3; filter: drop-shadow(0 4px 6px rgba(244, 63, 94, 0.2)); }

.title {
  font-family: 'Dancing Script', cursive; font-size: 3.5rem; color: #9F1239;
  margin: 0 0 1rem 0; line-height: 1.2; position: relative; display: inline-block;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.05);
}
.title-decor { position: absolute; top: -10px; right: -40px; display: flex; }
.decor-heart { width: 24px; height: 24px; color: #F43F5E; }
.d1 { transform: rotate(15deg) scale(1); filter: drop-shadow(0 2px 4px rgba(244, 63, 94, 0.3)); }
.d2 { transform: rotate(30deg) scale(0.6); margin-top: -10px; margin-left: -5px; opacity: 0.8; }

.subtitle { font-size: 1rem; color: #4B5563; line-height: 1.6; margin-bottom: 2.5rem; font-weight: 500; padding: 0 1rem; }

.actions { display: flex; justify-content: center; gap: 1.5rem; flex-wrap: wrap; }
.btn { text-decoration: none; padding: 12px 32px; border-radius: 50px; font-weight: 600; font-size: 0.95rem; transition: transform 0.2s ease, box-shadow 0.2s ease; display: inline-block; }
.btn:active { transform: scale(0.96); }
.btn-white { background-color: #ffffff; color: #374151; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 1px solid #f3f4f6; }
.btn-white:hover { box-shadow: 0 6px 20px rgba(0,0,0,0.08); transform: translateY(-2px); }
.btn-pink { background: linear-gradient(135deg, #fb7185 0%, #f43f5e 100%); color: white; box-shadow: 0 4px 15px rgba(244, 63, 94, 0.3); }
.btn-pink:hover { box-shadow: 0 6px 20px rgba(244, 63, 94, 0.4); transform: translateY(-2px); }

.footer-text { position: absolute; bottom: 20px; font-size: 0.75rem; color: #9CA3AF; letter-spacing: 0.5px; }

@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 600px) {
  .title { font-size: 2.5rem; }
  .title-decor { right: -25px; top: -5px; }
  .decor-heart { width: 20px; height: 20px; }
  .btn { width: 100%; text-align: center; }
  .home-container { padding-bottom: 60px; }
}
</style>