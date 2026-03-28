import {getBcColor, getColor} from "@/app/resources/utils";
import {RequestMethod} from "@/app/resources/data";

interface ButtonProps {
    method: RequestMethod;
}

export const Button = ({
    method,
} : ButtonProps) => {
    return (
        <div className={"flex gap-2 items-center"}>
            <span className={`font-bold red ${getColor(method)}`}>{method}</span>
            <div className={`cursor-pointer rounded-lg p-1 pl-4 pr-4 duration-150 ${getBcColor(method)}`}>
                Run
            </div>
        </div>
    )
}