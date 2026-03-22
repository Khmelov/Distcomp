import {ReactNode} from "react";

export const RequestHeader = ({ children } : { children? : ReactNode}) => {
    return (
        <h2 className="text-xl pl-5 font-semibold text-black dark:text-zinc-50">
            {children}
        </h2>
    )
}