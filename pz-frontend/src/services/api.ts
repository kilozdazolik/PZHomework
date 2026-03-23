import type { FamilyDetails, FamilyResponse } from "../types/Family";
import { API_BASE, requestJson, requestVoid } from "./http";

export const createFamily = (name: string): Promise<FamilyResponse> =>
  requestJson<FamilyResponse>(`${API_BASE}/families`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });

export const getFamilyById = (familyId: string): Promise<FamilyDetails> =>
  requestJson<FamilyDetails>(`${API_BASE}/families/${familyId}`);

export const addFamilyMember = (familyId: string, name: string): Promise<FamilyDetails> =>
  requestJson<FamilyDetails>(`${API_BASE}/families/${familyId}/members`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });

export const removeFamilyMember = (familyId: string, userId: string): Promise<void> =>
  requestVoid(`${API_BASE}/families/${familyId}/members/${userId}`, {
    method: "DELETE",
  });

export const deleteFamily = (familyId: string): Promise<void> =>
  requestVoid(`${API_BASE}/families/${familyId}`, {
    method: "DELETE",
  });

export const updateUserBackgroundImage = (userId: string, backgroundImageUrl: string | null): Promise<void> =>
  requestVoid(`${API_BASE}/users/${userId}/background`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ backgroundImageUrl }),
  });

export const updateUserTheme = (userId: string, theme: "LIGHT" | "DARK"): Promise<void> =>
  requestVoid(`${API_BASE}/users/${userId}/theme`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ theme }),
  });