import api from './api';
import { UserProfileResponse, UserProfileUpdateRequest } from '../types';

export const userService = {
  getProfile: async (): Promise<UserProfileResponse> => {
    const response = await api.get<UserProfileResponse>('/user/profile');
    return response.data;
  },

  updateProfile: async (data: UserProfileUpdateRequest): Promise<UserProfileResponse> => {
    const response = await api.put<UserProfileResponse>('/user/profile', data);
    return response.data;
  },
};

