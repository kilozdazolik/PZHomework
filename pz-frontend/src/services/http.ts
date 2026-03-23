export const API_BASE = "http://localhost:8080/api";

const extractErrorMessage = async (response: Response): Promise<string> => {
  try {
    const payload = await response.json();
    if (payload && typeof payload.message === "string" && payload.message.trim().length > 0) {
      return payload.message;
    }
  } catch {
    // Fallback to status code when body is not JSON.
  }

  return `Request failed (${response.status})`;
};

export const requestJson = async <T>(input: string, init?: RequestInit): Promise<T> => {
  const response = await fetch(input, init);
  if (!response.ok) {
    throw new Error(await extractErrorMessage(response));
  }
  return response.json() as Promise<T>;
};

export const requestVoid = async (input: string, init?: RequestInit): Promise<void> => {
  const response = await fetch(input, init);
  if (!response.ok) {
    throw new Error(await extractErrorMessage(response));
  }
};
