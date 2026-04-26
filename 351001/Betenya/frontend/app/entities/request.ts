import { RequestMethod } from "@/app/resources/data";

interface RequestOptions {
    body?: never;
    headers?: Record<string, string>;
    queryParams?: Record<string, string>;
}

export const sendRequest = async (
    method: RequestMethod,
    url: string,
    options?: RequestOptions
) => {
    const { body, headers = {}, queryParams } = options || {};

    let finalUrl = url;
    if (queryParams && Object.keys(queryParams).length > 0) {
        const params = new URLSearchParams(queryParams);
        finalUrl = `${url}?${params.toString()}`;
    }

    const config: RequestInit = {
        method,
        headers: {
            'Content-Type': 'application/json',
            ...headers,
        },
        credentials: 'include',
    };

    if (body && ['POST', 'PUT', 'PATCH'].includes(method)) {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(finalUrl, config);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            return {
                success: true,
                status: response.status,
                data,
                headers: Object.fromEntries(response.headers.entries()),
            };
        } else {
            const text = await response.text();
            return {
                success: true,
                status: response.status,
                data: text,
                headers: Object.fromEntries(response.headers.entries()),
            };
        }
    } catch (error) {
        console.error('Request failed:', error);
        return {
            success: false,
            error: error instanceof Error ? error.message : 'Unknown error',
        };
    }
};