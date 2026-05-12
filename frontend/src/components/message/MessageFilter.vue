<template>
  <div class="filter-tabs">
    <button
      v-for="tab in tabs"
      :key="tab.key"
      :class="['filter-tab', { active: activeTab === tab.key }]"
      @click="selectTab(tab.key)"
    >
      {{ tab.label }}
      <span v-if="tab.count > 0" class="badge">{{ tab.count }}</span>
    </button>
  </div>
</template>

<script>
export default {
  name: 'MessageFilter',
  props: {
    activeTab: {
      type: String,
      default: 'all'
    },
    tabs: {
      type: Array,
      default: () => []
    }
  },
  emits: ['tab-change'],
  setup(props, { emit }) {
    const selectTab = (key) => {
      emit('tab-change', key)
    }

    return {
      selectTab
    }
  }
}
</script>

<style scoped>
.filter-tabs {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  padding: 15px;
  border-bottom: 1px solid #eee;
  overflow-x: auto;
}

.filter-tab {
  background: white;
  border: 1px solid #ddd;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
  white-space: nowrap;
}

.filter-tab:hover {
  border-color: #4CAF50;
  color: #4CAF50;
}

.filter-tab.active {
  background-color: #4CAF50;
  border-color: #4CAF50;
  color: white;
}

.badge {
  background-color: #ff4444;
  color: white;
  font-size: 11px;
  min-width: 16px;
  height: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}

.filter-tab.active .badge {
  background-color: white;
  color: #4CAF50;
}
</style>