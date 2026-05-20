import api from './api';
import type {
  AdminTaskDto,
  DisplayRuleGroupDto,
  DisplayRuleGroupRequest,
  OperatorMeta,
  ParameterMeta,
} from '../types/admin';

export const adminService = {
  getTasks: (): Promise<AdminTaskDto[]> =>
    api.get<AdminTaskDto[]>('/admin/tasks').then((r) => r.data),

  getGroupNames: (): Promise<string[]> =>
    api.get<string[]>('/admin/tasks/groups').then((r) => r.data),

  getTask: (id: number): Promise<AdminTaskDto> =>
    api.get<AdminTaskDto>(`/admin/tasks/${id}`).then((r) => r.data),

  createTask: (payload: {
    title: string;
    groupName?: string | null;
    description?: string | null;
    daysToComplete?: number | null;
  }): Promise<AdminTaskDto> =>
    api.post<AdminTaskDto>('/admin/tasks', payload).then((r) => r.data),

  updateTask: (
    id: number,
    payload: {
      title?: string;
      groupName?: string | null;
      description?: string | null;
      daysToComplete?: number | null;
      active?: boolean;
    }
  ): Promise<AdminTaskDto> =>
    api.put<AdminTaskDto>(`/admin/tasks/${id}`, payload).then((r) => r.data),

  deleteTask: (id: number): Promise<void> =>
    api.delete(`/admin/tasks/${id}`).then(() => undefined),

  getParameters: (): Promise<ParameterMeta[]> =>
    api.get<ParameterMeta[]>('/admin/metadata/parameters').then((r) => r.data),

  getOperators: (): Promise<OperatorMeta[]> =>
    api.get<OperatorMeta[]>('/admin/metadata/operators').then((r) => r.data),

  getDisplayRules: (taskId: number): Promise<DisplayRuleGroupDto[]> =>
    api.get<DisplayRuleGroupDto[]>(`/admin/tasks/${taskId}/display-rules`).then((r) => r.data),

  createDisplayRuleGroup: (taskId: number, body: DisplayRuleGroupRequest): Promise<DisplayRuleGroupDto> =>
    api.post<DisplayRuleGroupDto>(`/admin/tasks/${taskId}/display-rules`, body).then((r) => r.data),

  updateDisplayRuleGroup: (
    taskId: number,
    groupId: number,
    body: DisplayRuleGroupRequest
  ): Promise<DisplayRuleGroupDto> =>
    api
      .put<DisplayRuleGroupDto>(`/admin/tasks/${taskId}/display-rules/${groupId}`, body)
      .then((r) => r.data),

  deleteDisplayRuleGroup: (taskId: number, groupId: number): Promise<void> =>
    api.delete(`/admin/tasks/${taskId}/display-rules/${groupId}`).then(() => undefined),
};
