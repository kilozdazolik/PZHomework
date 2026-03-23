import type { DesktopApp, DesktopAppRequest } from "../types/Application";
import { API_BASE, requestJson, requestVoid } from "./http";

const APPS_BASE = `${API_BASE}/apps`;

export const getAppsByUserId = (userId: string): Promise<DesktopApp[]> =>
  requestJson<DesktopApp[]>(`${APPS_BASE}/user/${userId}`);

export const addApp = (payload: DesktopAppRequest): Promise<DesktopApp> =>
  requestJson<DesktopApp>(APPS_BASE, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

export const updateApp = (id: string, payload: DesktopAppRequest): Promise<DesktopApp> =>
  requestJson<DesktopApp>(`${APPS_BASE}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

export const deleteApp = (id: string): Promise<void> =>
  requestVoid(`${APPS_BASE}/${id}`, { method: "DELETE" });