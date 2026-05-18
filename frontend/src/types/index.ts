export interface AuthResponse {
  token: string;
  userId: number;
  login: string;
}

export interface UserProfileResponse {
  id: number;
  login: string;
  firstName: string;
  lastName: string;
  middleName: string | null;
  citizenship: string | null;
  countryOfArrival: string | null;
  visitPurpose: string | null;
  visitDurationDays: number | null;
  arrivalDate: string | null;
  hasLanguageCertificate: boolean | null;
  hasPatent: boolean | null;
  patentId: number | null;
  patentTypeId: number | null;
  patentTypeName: string | null;
  patentNumber: string | null;
  patentTitle: string | null;
  patentIssueDate: string | null;
  patentExpiryDate: string | null;
  isProfileComplete: boolean;
}

export interface PatentTypeDto {
  id: number;
  code: string;
  name: string;
}

export interface PatentDetailsForm {
  patentTypeId: number | '';
  patentNumber: string;
  patentTitle: string;
  patentIssueDate: string;
  patentExpiryDate: string;
}

export interface UserProfileUpdateRequest {
  firstName: string;
  lastName: string;
  middleName?: string;
  citizenship: string;
  countryOfArrival: string;
  visitPurpose?: string;
  visitDurationDays?: number;
  arrivalDate: string;
  hasLanguageCertificate?: boolean;
  hasPatent?: boolean;
  patentTypeId?: number;
  patentNumber?: string;
  patentTitle?: string;
  patentIssueDate?: string;
  patentExpiryDate?: string;
}

export interface RoadmapTaskDto {
  id: number;
  title: string;
  groupName: string | null;
  description: string | null;
  daysToComplete: number | null;
}

export interface RoadmapResponse {
  tasks: RoadmapTaskDto[];
  completedTaskIds?: Set<number> | number[];
  isProfileComplete: boolean;
  message: string;
}
