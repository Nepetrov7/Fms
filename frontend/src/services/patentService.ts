import api from './api';
import type { PatentTypeDto } from '../types';

export const patentService = {
  getTypes: (): Promise<PatentTypeDto[]> =>
    api.get<PatentTypeDto[]>('/patent-types').then((r) => r.data),
};
