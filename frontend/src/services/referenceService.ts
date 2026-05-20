import api from './api';

export const referenceService = {
  searchCountries: (query: string, limit = 20): Promise<string[]> =>
    api
      .get<string[]>('/reference/countries', { params: { q: query || undefined, limit } })
      .then((r) => r.data),
};
