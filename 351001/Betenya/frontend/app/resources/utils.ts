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
        method === "POST" ? 'bg-green-200 hover:bg-green-100'  :
            method === "GET"  ? 'bg-blue-200 hover:bg-blue-100'   :
                method === "PUT"  ? 'bg-orange-200 hover:bg-orange-100'  :
                    'bg-red-200 hover:bg-red-100'
    }`
}

export const getBcBlockColor = (method: RequestMethod) => {
    return `${
        method === "POST" ? 'bg-green-50'  :
            method === "GET"  ? 'bg-blue-50'   :
                method === "PUT"  ? 'bg-orange-50' :
                    'bg-red-50'
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