import api from './api';
import { RoadmapResponse } from '../types';

export const roadmapService = {
  getRoadmap: async (): Promise<RoadmapResponse> => {
    const response = await api.get<RoadmapResponse>('/roadmap');
    return response.data;
  },
  completeTask: async (taskId: number): Promise<void> => {
    await api.post(`/roadmap/tasks/${taskId}/complete`);
  },
  uncompleteTask: async (taskId: number): Promise<void> => {
    await api.post(`/roadmap/tasks/${taskId}/uncomplete`);
  },
};

