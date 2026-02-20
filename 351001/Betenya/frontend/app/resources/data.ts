export const API_VERSIONS = [ '/v1.0', '/v2.0' ]
export const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:24110/api'

export type RequestMethod = "GET" | "POST" | "PUT" | "DELETE";

export interface IRequestItem {
    url: string;
    method: RequestMethod;
}

export const REQUESTS = [
    {
        header: 'Users',
        items: [
            { url: "/users", method: "POST" },
            { url: "/users", method: "GET" },
            { url: "/users/:id", method: "GET" },
            { url: "/users/:id", method: "PUT" },
            { url: "/users/:id", method: "DELETE" }
        ] as IRequestItem[],
    },
    {
        header: 'Articles',
        items: [
            { url: "/articles", method: "POST" },
            { url: "/articles", method: "GET" },
            { url: "/articles/:id", method: "GET" },
            { url: "/articles/:id", method: "PUT" },
            { url: "/articles/:id", method: "DELETE" }
        ] as IRequestItem[],
    },
    {
        header: 'Notices',
        items: [
            { url: "/notices", method: "POST" },
            { url: "/notices", method: "GET" },
            { url: "/notices/:id", method: "GET" },
            { url: "/notices/:id", method: "PUT" },
            { url: "/notices/:id", method: "DELETE" }
        ] as IRequestItem[],
    },
    {
        header: 'Stickers',
        items: [
            { url: "/stickers", method: "POST" },
            { url: "/stickers", method: "GET" },
            { url: "/stickers/:id", method: "GET" },
            { url: "/stickers/:id", method: "PUT" },
            { url: "/stickers/:id", method: "DELETE" }
        ] as IRequestItem[],
    }
]
