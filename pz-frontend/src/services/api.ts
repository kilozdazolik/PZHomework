import type { FamilyResponse } from "../types/Family";

const BASE = "http://localhost:8080/api";

export const getUsers   = () => fetch(`${BASE}/users`).then(r => r.json());


export const createFamily = async (name: string): Promise<FamilyResponse> => {
	const response = await fetch(`${BASE}/families`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ name }),
	});

	if (!response.ok) {
		throw new Error(`Failed to create family (${response.status})`);
	}

	return response.json();
};