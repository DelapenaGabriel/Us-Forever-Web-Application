<template>
    <div class="timeline-view">
        <TimelineSection />
    </div>
</template>


<script>
import TimelineSection from '@/components/TimelineSection.vue';
export default {
  name: 'TimelineView',
  components: {
    TimelineSection,
  },
};
</script>