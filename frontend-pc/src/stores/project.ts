import { defineStore } from 'pinia';
import { ref } from 'vue';
import { projectApi, type Project } from '@/api/project';

export const useProjectStore = defineStore('project', () => {
  const projects = ref<Project[]>([]);
  const currentProject = ref<Project | null>(null);
  const loading = ref(false);

  async function fetchProjects(params?: { page?: number; size?: number; status?: string }) {
    loading.value = true;
    try {
      const res = await projectApi.list(params);
      projects.value = res.data.records;
      return res.data;
    } finally {
      loading.value = false;
    }
  }

  function setCurrentProject(project: Project | null) {
    currentProject.value = project;
  }

  return { projects, currentProject, loading, fetchProjects, setCurrentProject };
});
