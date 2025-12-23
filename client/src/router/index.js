import { createRouter, createWebHistory } from 'vue-router'
import { useStore } from "vuex";


/**
 * Define all application routes
 */

import HomeView from '@/views/HomeView.vue';
import GalleryView from '@/views/GalleryView.vue';
import TimelineView from '@/views/TimelineView.vue';
import NoteView from '@/views/NoteView.vue';
const routes =[
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: false },
  },
  {
    path: '/gallery',
    name: 'gallery',
    component: GalleryView,
    meta: { requiresAuth: false },
  },
  {
    path: '/timeline',
    name: 'timeline',
    component: TimelineView,
    meta: { requiresAuth: false },
  },
  {
    path: '/notes',
    name: 'notes',
    component: NoteView,
    meta: { requiresAuth: false },
  },
]


// Create the router
const router = createRouter({
  history: createWebHistory(),
  routes: routes,
});

router.beforeEach((to) => {
  // Get the Vuex store
  const store = useStore();

  // Determine if the route requires Authentication
  const requiresAuth = to.matched.some((x) => x.meta.requiresAuth);

  // If it does and they are not logged in, send the user to "/login"
  if (requiresAuth && store.state.token === "") {
    return { name: "login" };
  }
  // Otherwise, do nothing and they'll go to their next destination
});

export default router;
