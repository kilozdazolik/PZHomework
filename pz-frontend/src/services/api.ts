import type { FamilyDetails, FamilyResponse } from "../types/Family";

const BASE = "http://localhost:8080/api";

export const getUsers   = () => fetch(`${BASE}/users`).then(r => r.json());

const parseErrorMessage = async (response: Response): Promise<string> => {
	try {
		const data = await response.json();
		if (data && typeof data.message === 'string' && data.message.trim().length > 0) {
			return data.message;
		}
	} catch {
        // Ignore JSON
    	}

	return `Request failed (${response.status})`;
};


export const createFamily = async (name: string): Promise<FamilyResponse> => {
	const response = await fetch(`${BASE}/families`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ name }),
	});

	if (!response.ok) {
		throw new Error(await parseErrorMessage(response));
	}

	return response.json();
};

export const getFamilyById = async (familyId: string): Promise<FamilyDetails> => {
	const response = await fetch(`${BASE}/families/${familyId}`);

	if (!response.ok) {
		throw new Error(await parseErrorMessage(response));
	}

	return response.json();
};

export const addFamilyMember = async (familyId: string, name: string): Promise<FamilyDetails> => {
	const response = await fetch(`${BASE}/families/${familyId}/members`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ name }),
	});

	if (!response.ok) {
		throw new Error(await parseErrorMessage(response));
	}

	return response.json();
};