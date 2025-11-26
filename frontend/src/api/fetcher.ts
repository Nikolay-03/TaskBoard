export class Fetcher {
    private readonly baseUrl: string;

    constructor(baseUrl: string = "http://localhost:8080") {
        this.baseUrl = baseUrl;
    }

    async request<T>(
        path: string,
        init: RequestInit = {}
    ): Promise<T> {
        const url = this.baseUrl + path;

        const res = await fetch(url, {
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                ...(init.headers || {}),
            },
            ...init,
        });

        if (!res.ok) {
            let message;
            try {
                const errBody = await res.json();
                // если бэк отдает { error: "..." }
                message = `${errBody.error ?? JSON.stringify(errBody)}`;
            } catch {
                // игнор, если не json
            }
            throw new Error(message);
        }

        // если нет тела (204), просто вернем as unknown as T
        if (res.status === 204) {
            return undefined as unknown as T;
        }

        return res.json() as Promise<T>;
    }

    get<T>(path: string): Promise<T> {
        return this.request<T>(path, { method: "GET" });
    }

    post<T, B = unknown>(path: string, body?: B): Promise<T> {
        return this.request<T>(path, {
            method: "POST",
            body: body !== undefined ? JSON.stringify(body) : undefined,
        });
    }

    patch<T, B = unknown>(path: string, body?: B): Promise<T> {
        return this.request<T>(path, {
            method: "PATCH",
            body: body !== undefined ? JSON.stringify(body) : undefined,
        });
    }

    delete<T>(path: string): Promise<T> {
        return this.request<T>(path, { method: "DELETE" });
    }
}
