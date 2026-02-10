import {ReactNode} from "react";

export const RequestHeader = ({ children } : { children? : ReactNode}) => {
    return (
        <h2 className="text-xl pl-5 font-semibold text-gray-900">
            {children}
        </h2>
    )
}