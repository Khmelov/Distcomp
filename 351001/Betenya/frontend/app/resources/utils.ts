import {RequestMethod} from "@/app/resources/data";

export const getBorderColor = (method: RequestMethod) => {
    return `${
        method === "POST" ? 'border-green-500'  :
            method === "GET"  ? 'border-blue-500'   :
                method === "PUT"  ? 'border-orange-500' :
                    'border-red-500'
    }`;
}

export const getBcColor = (method: RequestMethod) => {
    return `${
        method === "POST" ? 'bg-green-200 hover:bg-green-100 dark:bg-green-500 dark:hover:bg-green-500'  :
            method === "GET"  ? 'bg-blue-200 hover:bg-blue-100 dark:bg-blue-500 dark:hover:bg-blue-500'   :
                method === "PUT"  ? 'bg-orange-200 hover:bg-orange-100 dark:bg-orange-500 dark:hover:bg-orange-500'  :
                    'bg-red-200 hover:bg-red-100 dark:bg-red-500 dark:hover:bg-red-500'
    }`
}

export const getBcBlockColor = (method: RequestMethod) => {
    return `${
        method === "POST" ? 'bg-green-50 dark:bg-green-950'  :
            method === "GET"  ? 'bg-blue-50 dark:bg-blue-950'   :
                method === "PUT"  ? 'bg-orange-50 dark:bg-orange-950' :
                    'bg-red-50 dark:bg-red-950'
    }`
}

export const getColor = (method: RequestMethod) => {
    return `${
        method === "POST" ? 'text-green-400'  :
            method === "GET"  ? 'text-blue-400'   :
                method === "PUT"  ? 'text-orange-400' :
                    'text-red-400'
    }`
}